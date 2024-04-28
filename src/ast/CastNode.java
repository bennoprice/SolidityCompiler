package ast;

import ast.visitor.TreeVisitor;

public class CastNode extends ExpressionNode {
    protected Symbol type_decl;
    protected ExpressionNode expr;

    public Symbol getType_decl() {
        return type_decl;
    }

    public ExpressionNode getExpr() {
        return expr;
    }

    public CastNode(int lineNumber, Symbol type, ExpressionNode expr) {
        super(lineNumber);
        this.type_decl = type;
        this.expr = expr;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
