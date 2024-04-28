package ast;

import ast.visitor.TreeVisitor;

public class DeclarationNode extends ExpressionNode {
    protected Symbol name;
    protected Symbol type_decl;
    protected ExpressionNode expr;

    public Symbol getName() {
        return name;
    }

    public Symbol getType_decl() {
        return type_decl;
    }

    public ExpressionNode getExpr() {
        return expr;
    }

    public DeclarationNode(int lineNumber, Symbol name, Symbol type, ExpressionNode expr) {
        super(lineNumber);
        this.name = name;
        this.type_decl = type;
        this.expr = expr;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
