package ast.visitor;

import ast.*;
import org.antlr.v4.codegen.model.decl.Decl;

public interface TreeVisitor<R,D> {

    R visit(Symbol sym, D data);

    R visit(Tree node, D data);

    R visit(TreeNode node, D data);

    R visit(ProgramNode node, D data);

    R visit(ContractNode node, D data);

    R visit(FeatureNode node, D data);

    R visit(MethodNode node, D data);

    R visit(AttributeNode node, D data);

    R visit(FormalNode node, D data);

    R visit(ExpressionNode node, D data);

    R visit(AssignNode node, D data);

    R visit(DeclarationNode node, D data);

    R visit(ReturnNode node, D data);

    R visit(TernaryNode node, D data);

    R visit(CastNode node, D data);

    R visit(DispatchNode node, D data);

    R visit(BinopNode node, D data);

    R visit(UnopNode node, D data);

    R visit(IntBinopNode node, D data);

    R visit(BoolBinopNode node, D data);

    R visit(IntUnopNode node, D data);

    R visit(BoolUnopNode node, D data);

    R visit(PlusNode node, D data);

    R visit(SubNode node, D data);

    R visit(MulNode node, D data);

    R visit(DivideNode node, D data);

    R visit(NegNode node, D data);

    R visit(LTNode node, D data);

    R visit(EqNode node, D data);

    R visit(LEqNode node, D data);

    R visit(CompNode node, D data);

    R visit(ConstNode node, D data);

    R visit(IntConstNode node, D data);
    R visit(BoolConstNode node, D data);

    R visit(ObjectNode node, D data);

    R visit(NoExpressionNode node, D data);

}