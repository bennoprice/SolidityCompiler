import ast.*;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Cgen {
    public static Assembler asm;
    public static ContractNode contract;
    public static HashMap<MethodNode, Integer> methodLabels;
    public static HashMap<Symbol, MethodNode> methods;
    public static ArrayList<Symbol> attributes;

    public static String emit(ProgramNode program) {
        asm = new Assembler();
        contract = program.getContract();
        methodLabels = new HashMap<>();

        populateFeatures();
        emitDispatch();
        emitMethods();

        return asm.get();
    }

    private static void populateFeatures() {
        attributes = new ArrayList<>();
        methods = new HashMap<>();

        for (var feature : contract.getFeatures()) {
            if (feature instanceof AttributeNode attribute)
                attributes.add(attribute.getName());
            else if (feature instanceof MethodNode method) {
                methods.put(method.getName(), method);
                methodLabels.put(method, asm.getLabel());
            }
        }
    }

    private static void emitDispatch() {
        var revert = asm.getLabel();
        var stop = asm.getLabel();

        // revert if no function selector
        asm.PUSH(1, 4);
        asm.CALLDATASIZE();
        asm.LT();
        asm.JUMPI(revert);

        // push function selector
        asm.PUSH(1, 0);
        asm.CALLDATALOAD();
        asm.PUSH(1, 0xe0);
        asm.SHR();

        // jump to selected method
        for (var feature : contract.getFeatures()) {
            if (feature instanceof MethodNode method && method.isPublic())
                emitDispatchMethod(method, stop);
        }

        // revert
        asm.JUMPDEST(revert);
        asm.PUSH(1, 0);
        asm.DUP(1);
        asm.REVERT();

        // stop
        asm.JUMPDEST(stop);
        asm.STOP();
    }

    private static void emitDispatchMethod(MethodNode method, int stop) {
        var types = method.getFormals().stream().map(x -> x.getType_decl().toString()).toList();
        var signature = method.getName().toString() + "(" + String.join(",", types) + ") packed";

        var hash = new Keccak.Digest256().digest(signature.getBytes());
        var selector = Hex.toHexString(hash).substring(0, 8);

        var next = asm.getLabel();
        var body = methodLabels.get(method);

        asm.DUP(1);
        asm.PUSH(4, selector);
        asm.EQ();
        asm.ISZERO();
        asm.JUMPI(next);

        asm.PUSH(stop);
        emitPushArgs(method.getFormals());
        asm.JUMP(body);

        asm.JUMPDEST(next);
    }

    private static void emitPushArgs(List<FormalNode> formals) {
        int offset = 4; // skip function selector
        for (var formal : formals) {
            var size = switch (formal.getType_decl().toString()) {
                case "bool" -> 1;
                case "uint8" -> 1;
                case "uint256" -> 32;
                default -> 0;
            };

            if (size == 0)
                Utilities.fatalError("Cgen.emitPushArgs: unknown type " + formal.getType_decl());

            asm.PUSH(4, offset);
            asm.CALLDATALOAD();

            if (size < 32) {
                asm.PUSH(1, (32 - size) * 8);
                asm.SHR();
            }

            offset += size;
        }
    }

    private static void emitMethods() {
        for (var feature : contract.getFeatures()) {
            if (feature instanceof MethodNode method)
                emitMethod(method);
        }
    }

    private static void emitMethod(MethodNode method) {
        var label = methodLabels.get(method);
        asm.JUMPDEST(label);

        var locals = new ArrayList<Symbol>();
        for (var expr : method.getExprs()) {
            if (expr instanceof DeclarationNode decl) {
                locals.add(0, decl.getName());
                asm.PUSH(1, 0);
            }
        }

        var exprVisitor = new CgenExprVisitor();
        var env = new CgenEnv(method, locals);

        for (var expr : method.getExprs())
            expr.accept(exprVisitor, env);

        // return exprs need to jump to this epilogue
        // stack must be return val, return address, args, locals
        for (int i = 0; i < method.getFormals().size() + locals.size() + env.getDelta(); i++)
            asm.POP();

        asm.JUMP();
    }
}
