package semantic;

import Tuga.TugaBaseVisitor;
import Tuga.TugaParser;

public class TypeCheckingVisitor extends TugaBaseVisitor<TugaType> {

    private boolean typeCheckSuccessful = true;
    private final boolean showErrors;

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
        return visit(ctx.literal());
    }

    @Override
    public TugaType visitParens(TugaParser.ParensContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public TugaType visitNegate(TugaParser.NegateContext ctx) {
        TugaType type = visit(ctx.expression());
        if (type == TugaType.INT || type == TugaType.REAL) {
            return type;
        }
        typeError(ctx.getText(), "Operador '-' aplicado a tipo não numérico");
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitNot(TugaParser.NotContext ctx) {
        TugaType type = visit(ctx.expression());
        if (type == TugaType.BOOLEAN) {
            return TugaType.BOOLEAN;
        }
        typeError(ctx.getText(), "Operador 'nao' aplicado a tipo não booleano");
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitAddSub(TugaParser.AddSubContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        String op = ctx.getChild(1).getText();

        if (left == TugaType.STRING || right == TugaType.STRING) {
            if (op.equals("+")) {
                return TugaType.STRING;
            }
            typeError(ctx.getText(), "Operador '-' não definido para strings");
            return TugaType.ERROR;
        }
        if ((left == TugaType.INT || left == TugaType.REAL) &&
                (right == TugaType.INT || right == TugaType.REAL)) {
            return (left == TugaType.REAL || right == TugaType.REAL) ? TugaType.REAL : TugaType.INT;
        }
        typeError(ctx.getText(), "Tipos inválidos para o operador " + op);
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitMulDivMod(TugaParser.MulDivModContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        String op = ctx.getChild(1).getText();

        if ((left == TugaType.INT || left == TugaType.REAL) &&
                (right == TugaType.INT || right == TugaType.REAL)) {
            return (left == TugaType.REAL || right == TugaType.REAL) ? TugaType.REAL : TugaType.INT;
        }
        typeError(ctx.getText(), "Tipos inválidos para o operador " + op);
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitRelational(TugaParser.RelationalContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        if ((left == TugaType.INT || left == TugaType.REAL) && (right == TugaType.INT || right == TugaType.REAL)) {
            return TugaType.BOOLEAN;
        }
        return typeErrorAndReturn(ctx.getText(), "Tipos inválidos para o operador relacional " + ctx.getChild(1).getText());
    }

    @Override
    public TugaType visitEquality(TugaParser.EqualityContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        if (left == right) {
            return TugaType.BOOLEAN;
        }
        return typeErrorAndReturn(ctx.getText(), "Operador de igualdade aplicado a tipos diferentes");
    }

    @Override
    public TugaType visitLogicalAnd(TugaParser.LogicalAndContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        if (left == TugaType.BOOLEAN && right == TugaType.BOOLEAN) {
            return TugaType.BOOLEAN;
        }
        return typeErrorAndReturn(ctx.getText(), "Operador lógico 'e' aplicado a tipos não booleanos");
    }

    @Override
    public TugaType visitLogicalOr(TugaParser.LogicalOrContext ctx) {
        TugaType left = visit(ctx.expression(0));
        TugaType right = visit(ctx.expression(1));
        if (left == TugaType.BOOLEAN && right == TugaType.BOOLEAN) {
            return TugaType.BOOLEAN;
        }
        return typeErrorAndReturn(ctx.getText(), "Operador lógico 'ou' aplicado a tipos não booleanos");
    }

    private TugaType typeErrorAndReturn(String context, String msg) {
        typeError(context, msg);
        return TugaType.ERROR;
    }

    private void typeError(String context, String msg) {
        if (showErrors) {
            System.err.println("Erro de tipo na expressão '" + context + "': " + msg);
        }
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
