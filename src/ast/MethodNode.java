package ast;

import ast.visitor.TreeVisitor;

import java.util.List;

public class MethodNode  extends FeatureNode {
    protected Symbol name;
    protected List<FormalNode> formals;
    protected boolean public_;
    protected Symbol return_type;
    protected List<ExpressionNode> exprs;

    public Symbol getName() {
        return name;
    }
    public List<FormalNode> getFormals() {
        return formals;
    }
    public boolean isPublic() {
        return public_;
    }
    public Symbol getReturn_type() {
        return return_type;
    }
    public List<ExpressionNode> getExprs() {
        return exprs;
    }

    public MethodNode(int lineNumber, Symbol name, List<FormalNode> formals, boolean public_, Symbol return_type, List<ExpressionNode> exprs) {
        super(lineNumber);
        this.name = name;
        this.formals = formals;
        this.public_ = public_;
        this.return_type = return_type;
        this.exprs = exprs;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
