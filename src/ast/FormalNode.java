package ast;

import ast.visitor.TreeVisitor;

public class FormalNode extends TreeNode {
    protected Symbol name;
    protected Symbol type_decl;

    public Symbol getName() {
        return name;
    }

    public Symbol getType_decl() {
        return type_decl;
    }

    public FormalNode(int lineNumber, Symbol name, Symbol type) {
        super(lineNumber);
        this.name = name;
        this.type_decl = type;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
