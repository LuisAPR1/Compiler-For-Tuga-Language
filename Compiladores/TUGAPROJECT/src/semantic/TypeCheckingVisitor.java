package semantic;

import src.TugaBaseVisitor;
import src.TugaParser;

public class TypeCheckingVisitor extends TugaBaseVisitor<TugaType> {
    private boolean typeCheckSuccessful = true;
    private boolean showErrors = false;

    public TypeCheckingVisitor(boolean showErrors) {
        this.showErrors = showErrors;
    }

    public boolean isTypeCheckSuccessful() {
        return typeCheckSuccessful;
    }

    @Override
    public TugaType visitInstruction(TugaParser.InstructionContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public TugaType visitLiteralExpr(TugaParser.LiteralExprContext ctx) {
        // Delegar para a regra 'literal', que possui os métodos específicos
        return visit(ctx.literal());
    }

    @Override
    public TugaType visitParens(TugaParser.ParensContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public TugaType visitNegate(TugaParser.NegateContext ctx) {
        TugaType type = visit(ctx.expression());
        if (type == TugaType.INT || type == TugaType.REAL)
            return type;
        typeError(ctx.getText(), "Unary '-' applied to non-numeric type");
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitNot(TugaParser.NotContext ctx) {
        TugaType type = visit(ctx.expression());
        if (type == TugaType.BOOLEAN)
            return TugaType.BOOLEAN;
        typeError(ctx.getText(), "Operator 'nao' applied to non-boolean type");
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitAddSub(TugaParser.AddSubContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        if (left == TugaType.STRING || right == TugaType.STRING) {
            if (ctx.op.getText().equals("+"))
                return TugaType.STRING;
            typeError(ctx.getText(), "Operator '-' not defined for strings");
            return TugaType.ERROR;
        }
        if ((left == TugaType.INT || left == TugaType.REAL) &&
                (right == TugaType.INT || right == TugaType.REAL)) {
            return (left == TugaType.REAL || right == TugaType.REAL) ? TugaType.REAL : TugaType.INT;
        }
        typeError(ctx.getText(), "Invalid types for operator " + ctx.op.getText());
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitMulDivMod(TugaParser.MulDivModContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        if ((left == TugaType.INT || left == TugaType.REAL) &&
                (right == TugaType.INT || right == TugaType.REAL)) {
            return (left == TugaType.REAL || right == TugaType.REAL) ? TugaType.REAL : TugaType.INT;
        }
        typeError(ctx.getText(), "Invalid types for operator " + ctx.op.getText());
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitRelational(TugaParser.RelationalContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        if ((left == TugaType.INT || left == TugaType.REAL) &&
                (right == TugaType.INT || right == TugaType.REAL))
            return TugaType.BOOLEAN;
        typeError(ctx.getText(), "Invalid types for relational operator " + ctx.op.getText());
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitEquality(TugaParser.EqualityContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        if (left == right)
            return TugaType.BOOLEAN;
        typeError(ctx.getText(), "Equality operator applied to mismatched types");
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitLogical(TugaParser.LogicalContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        if (left == TugaType.BOOLEAN && right == TugaType.BOOLEAN)
            return TugaType.BOOLEAN;
        typeError(ctx.getText(), "Logical operator applied to non-boolean types");
        return TugaType.ERROR;
    }

    private void typeError(String context, String msg) {
        if (showErrors)
            System.err.println("Type error in expression '" + context + "': " + msg);
        typeCheckSuccessful = false;
    }

    @Override
    public TugaType visitIntLiteral(TugaParser.IntLiteralContext ctx) {
        return TugaType.INT;
    }

    @Override
    public TugaType visitRealLiteral(TugaParser.RealLiteralContext ctx) {
        return TugaType.REAL;
    }

    @Override
    public TugaType visitStringLiteral(TugaParser.StringLiteralContext ctx) {
        return TugaType.STRING;
    }

    @Override
    public TugaType visitTrueLiteral(TugaParser.TrueLiteralContext ctx) {
        return TugaType.BOOLEAN;
    }

    @Override
    public TugaType visitFalseLiteral(TugaParser.FalseLiteralContext ctx) {
        return TugaType.BOOLEAN;
    }

}