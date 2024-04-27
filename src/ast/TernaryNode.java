package ast;

import ast.visitor.TreeVisitor;

public class TernaryNode extends ExpressionNode {
    protected ExpressionNode cond;
    protected ExpressionNode e1;
    protected ExpressionNode e2;

    public ExpressionNode getCond() {
        return cond;
    }

    public ExpressionNode getE1() {
        return e1;
    }

    public ExpressionNode getE2() {
        return e2;
    }

    public TernaryNode(int lineNumber, ExpressionNode cond, ExpressionNode e1, ExpressionNode e2) {
        super(lineNumber);
        this.cond = cond;
        this.e1 = e1;
        this.e2 = e2;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
