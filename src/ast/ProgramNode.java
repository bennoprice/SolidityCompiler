package ast;

import ast.visitor.TreeVisitor;

import java.util.LinkedList;
import java.util.List;

public class ProgramNode extends TreeNode {
    protected ContractNode contract;

    public ContractNode getContract() {
        return contract;
    }

    public ProgramNode(int lineNumber, ContractNode contract) {
        super(lineNumber);
        this.contract = contract;
    }

    public <R,D> R accept(TreeVisitor<R,D> visitor, D data) {
        return visitor.visit(this, data);
    }
}
