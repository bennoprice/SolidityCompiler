package ast;

import ast.visitor.TreeVisitor;

public class ReturnNode extends ExpressionNode {
    public ExpressionNode getExpr() {
        return expr;
    }

    protected ExpressionNode expr;

    public ReturnNode(int lineNumber, ExpressionNode expr) {
        super(lineNumber);
        this.expr = expr;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
