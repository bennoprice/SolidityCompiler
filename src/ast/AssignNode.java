package ast;

import ast.visitor.TreeVisitor;

public class AssignNode extends ExpressionNode {
    protected Symbol name;
    protected ExpressionNode expr;

    public Symbol getName() {
        return name;
    }

    public ExpressionNode getExpr() {
        return expr;
    }

    public AssignNode(int lineNumber, Symbol name, ExpressionNode expr) {
        super(lineNumber);
        this.name = name;
        this.expr = expr;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
