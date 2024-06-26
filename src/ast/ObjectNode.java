package ast;

import ast.visitor.TreeVisitor;

public class ObjectNode extends ExpressionNode {
    protected Symbol name;

    public Symbol getName() {
        return name;
    }

    public ObjectNode(int lineNumber, Symbol name) {
        super(lineNumber);
        this.name = name;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
