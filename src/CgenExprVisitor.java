import ast.*;
import ast.visitor.BaseVisitor;

import java.util.ArrayList;

class CgenEnv {
    private MethodNode method;
    private ArrayList<Symbol> locals;
    private int delta;

    public CgenEnv(MethodNode method, ArrayList<Symbol> locals) {
        this.method = method;
        this.locals = locals;
        this.delta = 0;
    }

    public boolean isLocal(Symbol name) {
        return locals.contains(name);
    }

    private int getArgumentIdx(Symbol name) {
        for (int i = 0; i < method.getFormals().size(); i++) {
            if (method.getFormals().get(i).getName() == name)
                return method.getFormals().size() - i - 1; // passed in reverse order
        }
        return -1;
    }

    public boolean isArgument(Symbol name) {
        return getArgumentIdx(name) != -1;
    }

    public void storeReturn() {
        // we add one to skip over the return address (return value comes after return address)
        Cgen.asm.SWAP(locals.size() + method.getFormals().size() + delta + 1);
        Cgen.asm.POP();
        pop();
    }

    public void storeLocal(Symbol name) {
        var idx = locals.indexOf(name);
        Cgen.asm.SWAP(idx + delta);
        Cgen.asm.POP();
        pop();
    }

    public void loadLocal(Symbol name) {
        var idx = locals.indexOf(name);
        Cgen.asm.DUP(idx + delta + 1);
        push();
    }

    public void loadArgument(Symbol name) {
        var idx = getArgumentIdx(name);
        Cgen.asm.DUP(idx + locals.size() + delta + 1);
        push();
    }

    public void storeAttribute(Symbol name) {
        var idx = Cgen.attributes.indexOf(name);
        Cgen.asm.PUSH(4, idx);
        Cgen.asm.SSTORE();
        pop();
    }

    public void loadAttribute(Symbol name) {
        var idx = Cgen.attributes.indexOf(name);
        Cgen.asm.PUSH(4, idx);
        Cgen.asm.SLOAD();
        push();
    }

    public int getDelta() {
        return delta;
    }

    public void push(int slots) {
        delta += slots;
    }

    public void push() {
        push(1);
    }

    public void pop(int slots) {
        delta -= slots;
    }

    public void pop() {
        pop(1);
    }
}

public class CgenExprVisitor extends BaseVisitor<Object, CgenEnv> {
    @Override
    public Integer visit(DeclarationNode node, CgenEnv ctx) {
        visit(node.getExpr(), ctx);
        ctx.storeLocal(node.getName());
        return null;
    }

    @Override
    public Object visit(AssignNode node, CgenEnv ctx) {
        visit(node.getExpr(), ctx);

        var name = node.getName();
        if (ctx.isLocal(name))
            ctx.storeLocal(name);
        else
            ctx.storeAttribute(name);

        return null;
    }

    @Override
    public Object visit(DispatchNode node, CgenEnv ctx) {
        var method = Cgen.methods.get(node.getName());

        if (method.getReturn_type() != TreeConstants.void_) {
            Cgen.asm.PUSH(1, 0);
            ctx.push();
        }

        var ret = Cgen.asm.getLabel();
        Cgen.asm.PUSH(ret);
        ctx.push();

        for (var actual : node.getActuals())
            visit(actual, ctx);

        var body = Cgen.methodLabels.get(method);
        Cgen.asm.JUMP(body);
        ctx.push();

        // pop virtual stack args, jump dest, ret address
        ctx.pop(node.getActuals().size() + 2);

        Cgen.asm.JUMPDEST(ret); // should push if return value
        return null;
    }

    @Override
    public Object visit(ReturnNode node, CgenEnv ctx) {
        visit(node.getExpr(), ctx);
        ctx.storeReturn();
        return null;
    }

    @Override
    public Object visit(TernaryNode node, CgenEnv ctx) {
        visit(node.getCond(), ctx);

        var elseLabel = Cgen.asm.getLabel();
        var endLabel = Cgen.asm.getLabel();

        Cgen.asm.ISZERO();
        Cgen.asm.JUMPI(elseLabel);
        ctx.pop();
        visit(node.getE1(), ctx);
        ctx.pop(); // little hacky, this avoids both e1 & e2 both pushing to virtual stack
        Cgen.asm.JUMP(endLabel);

        Cgen.asm.JUMPDEST(elseLabel);
        visit(node.getE2(), ctx);
        Cgen.asm.JUMPDEST(endLabel);

        return null;
    }

    @Override
    public Object visit(ObjectNode node, CgenEnv ctx) {
        var name = node.getName();

        if (ctx.isLocal(name))
            ctx.loadLocal(name);
        else if (ctx.isArgument(name))
            ctx.loadArgument(name);
        else
            ctx.loadAttribute(name);

        return null;
    }

    @Override
    public Object visit(IntBinopNode node, CgenEnv ctx) {
        visit(node.getE2(), ctx);
        visit(node.getE1(), ctx);

        if (node instanceof PlusNode)
            Cgen.asm.ADD();
        else if (node instanceof SubNode)
            Cgen.asm.SUB();
        else if (node instanceof MulNode)
            Cgen.asm.MUL();
        else /* if (node instanceof DivideNode) */
            Cgen.asm.DIV();

        ctx.pop();
        return null;
    }

    @Override
    public Object visit(BoolBinopNode node, CgenEnv ctx) {
        visit(node.getE2(), ctx);
        visit(node.getE1(), ctx);

        if (node instanceof EqNode)
            Cgen.asm.EQ();
        else if (node instanceof LEqNode) {
            Cgen.asm.GT();
            Cgen.asm.ISZERO();
        }
        else /* if (node instanceof LTNode) */
            Cgen.asm.LT();

        ctx.pop();
        return null;
    }

    @Override
    public Object visit(NegNode node, CgenEnv ctx) {
        visit(node.getE1(), ctx);
        Cgen.asm.ISZERO();
        return null;
    }

    @Override
    public Integer visit(IntConstNode node, CgenEnv ctx) {
        var val = Integer.parseInt(node.getVal().toString());
        Cgen.asm.PUSH(4, val);
        ctx.push();
        return null;
    }

    @Override
    public Integer visit(BoolConstNode node, CgenEnv ctx) {
        Cgen.asm.PUSH(4, node.getVal() ? 1 : 0);
        ctx.push();
        return null;
    }
}
