import ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SymbolNode<D> {
    private final SymbolNode<D> parent;
    private final Map<Symbol, D> symbols;

    public SymbolNode(SymbolNode<D> parent) {
        this.parent = parent;
        this.symbols = new HashMap<>();
    }

    public SymbolNode() {
        this(null);
    }

    public SymbolNode<D> nextScope() {
        return new SymbolNode<>(this);
    }

    public void add(Symbol symbol, D value) {
        symbols.put(symbol, value);
    }

    public SymbolNode<D> prevScope() {
        return parent;
    }

    public D lookup(Symbol symbol) {
        for (var cur = this; cur != null; cur = cur.prevScope()) {
            var out = cur.probe(symbol);
            if (out != null)
                return out;
        }
        return null;
    }

    public D probe(Symbol symbol) {
        return symbols.get(symbol);
    }
}

public class Semant {
    public static ContractNode contract;
    public static List<Symbol> primitives;
    public static SymbolNode<Symbol> attributes;
    public static HashMap<Symbol, MethodNode> methods;

    public static void analyze(ProgramNode program) {
        contract = program.getContract();

        populatePrimitives();
        populateFeatures();

        var typeCheckVisitor = new TypeCheckingVisitor();
        program.accept(typeCheckVisitor, null);
    }

    private static void populatePrimitives() {
        primitives = new ArrayList<>();
        primitives.add(TreeConstants.uint256);
        primitives.add(TreeConstants.uint8);
        primitives.add(TreeConstants.bool);
        primitives.add(TreeConstants.void_);
    }

    private static void populateFeatures() {
        attributes = new SymbolNode<>();
        methods = new HashMap<>();

        for (var feature : contract.getFeatures()) {
            if (feature instanceof AttributeNode attribute)
                attributes.add(attribute.getName(), attribute.getType_decl());
            else if (feature instanceof MethodNode method)
                methods.put(method.getName(), method);
        }
    }
}
