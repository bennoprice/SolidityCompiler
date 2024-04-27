import ast.*;
import ast.visitor.BaseVisitor;

class TypeEnv {
    private MethodNode method;
    private SymbolNode<Symbol> variables;

    public TypeEnv(MethodNode method, SymbolNode<Symbol> variables) {
        this.method = method;
        this.variables = variables;
    }

    public TypeEnv nextScope() {
        return new TypeEnv(method, variables.nextScope());
    }

    public MethodNode getMethod() {
        return method;
    }

    public SymbolNode<Symbol> getVariables() {
        return variables;
    }
}

public class TypeCheckingVisitor extends BaseVisitor<Symbol, TypeEnv> {
    private Symbol error(String message, TreeNode node) {
        Utilities.semantError(Semant.contract.getFilename(), node, message);
        return TreeConstants.void_;
    }

    private Symbol errorUndefined(Symbol symbol, TreeNode node) {
        return error("Undefined symbol '" + symbol + "'", node);
    }

    private Symbol errorTypeMismatch(Symbol expected, Symbol received, TreeNode node) {
        return error("Expected '" + expected + "' but received '" + received + "'", node);
    }

    private boolean isInteger(Symbol type) {
        return type == TreeConstants.uint256 || type == TreeConstants.uint8;
    }

    @Override
    public Symbol visit(AttributeNode node, TypeEnv ctx) {
        var type = node.getType_decl();
        if (!Semant.primitives.contains(type))
            return errorUndefined(type, node);
        return type;
    }

    @Override
    public Symbol visit(MethodNode node, TypeEnv ctx) {
        ctx = new TypeEnv(node, Semant.attributes);

        int arg = 1;
        for (var formal : node.getFormals()) {
            var name = formal.getName();
            var type = formal.getType_decl();

            if (!Semant.primitives.contains(type))
                return error("Formal parameter " + arg + " undefined symbol '" + type + "'", node);

            if (ctx.getVariables().probe(name) != null)
                return error("Formal parameter " + name + " is multiply defined", node);

            ctx.getVariables().add(name, type);
            arg++;
        }

        var innerScope = ctx.nextScope();
        for (var expr : node.getExprs())
            visit(expr, innerScope);

        var returnType = node.getReturn_type();
        if (!Semant.primitives.contains(returnType))
            return error("Undefined return type '" + returnType + "'", node);
        return returnType;
    }

    @Override
    public Symbol visit(AssignNode node, TypeEnv ctx) {
        var lhs = ctx.getVariables().lookup(node.getName());
        if (lhs == null)
            return errorUndefined(node.getName(), node);

        var rhs = visit(node.getExpr(), ctx);
        if (lhs != rhs)
            return errorTypeMismatch(lhs, rhs, node);

        node.setType(TreeConstants.void_);
        return TreeConstants.void_;
    }

    @Override
    public Symbol visit(DeclarationNode node, TypeEnv ctx) {
        var lhs = node.getType_decl();
        if (!Semant.primitives.contains(lhs))
            return errorUndefined(lhs, node);

        var name = node.getName();
        if (ctx.getVariables().probe(name) != null)
            return error("Local variable '" + name + "' is multiply defined", node);

        var rhs = visit(node.getExpr(), ctx);
        if (lhs != rhs)
            return errorTypeMismatch(lhs, rhs, node);

        // expression evaluated before adding to scope
        ctx.getVariables().add(name, lhs);

        node.setType(TreeConstants.void_);
        return TreeConstants.void_;
    }

    @Override
    public Symbol visit(ReturnNode node, TypeEnv ctx) {
        var actual = visit(node.getExpr(), ctx);
        var expected = ctx.getMethod().getReturn_type();

        if (actual != expected)
            return errorTypeMismatch(expected, actual, node);

        node.setType(actual);
        return actual;
    }

    @Override
    public Symbol visit(DispatchNode node, TypeEnv ctx) {
        var method = Semant.methods.get(node.getName());
        if (method == null)
            return error("Dispatch to undefined method " + node.getName() + ".", node);

        var actualNum = node.getActuals().size();
        var formalNum = method.getFormals().size();

        if (actualNum != formalNum)
            return error("Expected " + formalNum + " arguments, but got " + actualNum, node);

        for (int i = 0; i < actualNum; i++) {
            var actual = node.getActuals().get(i);
            var formal = method.getFormals().get(i);

            var actualType = visit(actual, ctx);
            var formalType = formal.getType_decl();

            if (formalType != actualType)
                return error("Argument " + (i+1) + " expected type '" + formalType + "' but got '" + actualType + "'", node);
        }

        node.setType(method.getReturn_type());
        return method.getReturn_type();
    }

    @Override
    public Symbol visit(IntBinopNode node, TypeEnv ctx) {
        var lhs = visit(node.getE1(), ctx);
        if (!isInteger(lhs))
            return error("LHS of expression must be integer", node);

        var rhs = visit(node.getE2(), ctx);
        if (!isInteger(rhs))
            return error("RHS of expression must be integer", node);

        node.setType(TreeConstants.uint256);
        return TreeConstants.uint256;
    }

    @Override
    public Symbol visit(ObjectNode node, TypeEnv ctx) {
        var type = ctx.getVariables().lookup(node.getName());
        if (type == null)
            return errorUndefined(node.getName(), node);

        node.setType(type);
        return type;
    }

    @Override
    public Symbol visit(IntConstNode node, TypeEnv ctx) {
        // need to determine type based on size
        node.setType(TreeConstants.uint256);
        return TreeConstants.uint256;
    }
}
