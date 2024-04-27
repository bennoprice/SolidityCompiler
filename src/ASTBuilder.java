import ast.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;

public class ASTBuilder extends SolidityParserBaseVisitor<Tree> {
    private int getLineNumber(ParserRuleContext ctx) {
        return ctx.getStart().getLine();
    }

    private Symbol getFileName(ParserRuleContext ctx) {
        String source = ctx.getStart().getTokenSource().getSourceName();
        return StringTable.stringtable.addString(source); // should this be idtable?
    }

    @Override
    public Tree visitProgram(SolidityParser.ProgramContext ctx) {
        var contract = (ContractNode)visit(ctx.contract());
        return new ProgramNode(getLineNumber(ctx), contract);
    }

    @Override
    public Tree visitContract(SolidityParser.ContractContext ctx) {
        String name = ctx.typeId.getText();

        var contract = new ContractNode(
                getLineNumber(ctx),
                StringTable.idtable.addString(name),
                getFileName(ctx)
        );

        for (var feature : ctx.feature())
            contract.add((FeatureNode)visit(feature));

        return contract;
    }

    @Override
    public Tree visitAttribute(SolidityParser.AttributeContext ctx) {
        String name = ctx.objectId.getText();
        String type = ctx.typeId.getText();

        return new AttributeNode(
                getLineNumber(ctx),
                StringTable.idtable.addString(name),
                StringTable.idtable.addString(type)
        );
    }

    @Override
    public Tree visitMethod(SolidityParser.MethodContext ctx) {
        String name = ctx.objectId.getText();
        Symbol type = ctx.returnType() != null
                ? StringTable.idtable.addString(ctx.returnType().typeId.getText())
                : TreeConstants.void_;
        boolean public_ = ctx.accessibility().PUBLIC() != null;

        var formals = new ArrayList<FormalNode>();
        if (ctx.params() != null) {
            for (var formal : ctx.params().formal())
                formals.add((FormalNode)visit(formal));
        }

        var exprs = new ArrayList<ExpressionNode>();
        for (var expr : ctx.expr())
            exprs.add((ExpressionNode)visit(expr));

        return new MethodNode(
                getLineNumber(ctx),
                StringTable.idtable.addString(name),
                formals,
                public_,
                type,
                exprs
        );
    }

    @Override
    public Tree visitFormal(SolidityParser.FormalContext ctx) {
        String name = ctx.objectId.getText();
        String type = ctx.typeId.getText();

        return new FormalNode(
                getLineNumber(ctx),
                StringTable.idtable.addString(name),
                StringTable.idtable.addString(type)
        );
    }

    @Override
    public Tree visitDispatch(SolidityParser.DispatchContext ctx) {
        String name = ctx.objectId.getText();

        var args = new ArrayList<ExpressionNode>();
        if (ctx.args() != null) {
            for (var arg : ctx.args().expr())
                args.add((ExpressionNode) visit(arg));
        }

        return new DispatchNode(
                getLineNumber(ctx),
                StringTable.idtable.addString(name),
                args
        );
    }

    @Override
    public Tree visitAssignment(SolidityParser.AssignmentContext ctx) {
        String name = ctx.objectId.getText();
        var expr = (ExpressionNode)visit(ctx.expr());

        return new AssignNode(
                getLineNumber(ctx),
                StringTable.idtable.addString(name),
                expr
        );
    }

    @Override
    public Tree visitDeclaration(SolidityParser.DeclarationContext ctx) {
        String name = ctx.objectId.getText();
        String type = ctx.typeId.getText();
        var expr = (ExpressionNode)visit(ctx.expr());

        return new DeclarationNode(
                getLineNumber(ctx),
                StringTable.idtable.addString(name),
                StringTable.idtable.addString(type),
                expr
        );
    }

    @Override
    public Tree visitReturn(SolidityParser.ReturnContext ctx) {
        var expr = (ExpressionNode)visit(ctx.expr());

        return new ReturnNode(
                getLineNumber(ctx),
                expr
        );
    }

    @Override
    public Tree visitAddSubtract(SolidityParser.AddSubtractContext ctx) {
        var lhs = (ExpressionNode) visit(ctx.expr(0));
        var rhs = (ExpressionNode) visit(ctx.expr(1));
        var line = getLineNumber(ctx);

        return switch (ctx.op.getType()) { // Java 13
            case SolidityLexer.PLUS_OPERATOR -> new PlusNode(line, lhs, rhs);
            case SolidityLexer.MINUS_OPERATOR -> new SubNode(line, lhs, rhs);
            default -> null; // unreachable
        };
    }

    @Override
    public Tree visitMultiplyDivide(SolidityParser.MultiplyDivideContext ctx) {
        var lhs = (ExpressionNode)visit(ctx.expr(0));
        var rhs = (ExpressionNode)visit(ctx.expr(1));
        var line = getLineNumber(ctx);

        return switch (ctx.op.getType()) { // Java 13
            case SolidityLexer.MULT_OPERATOR -> new MulNode(line, lhs, rhs);
            case SolidityLexer.DIV_OPERATOR -> new DivideNode(line, lhs, rhs);
            default -> null; // unreachable
        };
    }

    @Override
    public Tree visitLessEqual(SolidityParser.LessEqualContext ctx) {
        var lhs = (ExpressionNode)visit(ctx.expr(0));
        var rhs = (ExpressionNode)visit(ctx.expr(1));
        var line = getLineNumber(ctx);

        return switch (ctx.op.getType()) { // Java 13
            case SolidityLexer.LESS_EQ_OPERATOR -> new LEqNode(line, lhs, rhs);
            case SolidityLexer.LESS_OPERATOR -> new LTNode(line, lhs, rhs);
            case SolidityLexer.EQ_OPERATOR -> new EqNode(line, lhs, rhs);
            default -> null; // unreachable
        };
    }

    @Override
    public Tree visitNegate(SolidityParser.NegateContext ctx) {
        var expr = (ExpressionNode)visit(ctx.expr());

        return new CompNode(
                getLineNumber(ctx),
                expr
        );
    }

    @Override
    public Tree visitBrackets(SolidityParser.BracketsContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Tree visitID(SolidityParser.IDContext ctx) {
        String name = ctx.objectId.getText();

        return new ObjectNode(
                getLineNumber(ctx),
                StringTable.idtable.addString(name)
        );
    }

    @Override
    public Tree visitInteger(SolidityParser.IntegerContext ctx) {
        String value = ctx.INT_CONST().getText();

        return new IntConstNode(
                getLineNumber(ctx),
                StringTable.inttable.addString(value)
        );
    }

    @Override
    public Tree visitTrue(SolidityParser.TrueContext ctx) {
        return new BoolConstNode(
                getLineNumber(ctx),
                true
        );
    }

    @Override
    public Tree visitFalse(SolidityParser.FalseContext ctx) {
        return new BoolConstNode(
                getLineNumber(ctx),
                false
        );
    }
}
