package ast;

import ast.visitor.TreeVisitor;

import java.util.LinkedList;
import java.util.List;

public class ContractNode extends TreeNode {
    protected Symbol name;
    protected List<FeatureNode> features = new LinkedList<FeatureNode>();
    protected Symbol filename;

    public ContractNode(int lineNumber, Symbol name, Symbol filename) {
        super(lineNumber);
        this.name = name;
        this.filename = filename;
    }

    public Symbol getName() {
        return name;
    }

    public List<FeatureNode> getFeatures() {
        return features;
    }

    public Symbol getFilename() {
        return filename;
    }

    public void add(FeatureNode f) {features.add(f);}

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
