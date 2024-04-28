package ast;

import ast.visitor.TreeVisitor;

import java.util.List;

public class DispatchNode extends ExpressionNode {
    protected Symbol name;
    protected List<ExpressionNode> actuals;

    public Symbol getName() {
        return name;
    }

    public List<ExpressionNode> getActuals() {
        return actuals;
    }

    public DispatchNode(int lineNumber, Symbol name, List<ExpressionNode> actuals) {
        super(lineNumber);
        this.name = name;
        this.actuals = actuals;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
