package ast;

import ast.visitor.TreeVisitor;

public abstract class TreeNode implements Tree {
    protected int lineNumber;

    public int getLineNumber() {
        return lineNumber;
    }

    protected TreeNode(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
