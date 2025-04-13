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

    // instruction: escreve expression ';'
    @Override
    public TugaType visitInstruction(TugaParser.InstructionContext ctx) {
        return visit(ctx.expression());
    }

    // orExpr: orExpr : andExpr ('ou' andExpr)*
    @Override
    public TugaType visitOrExpr(TugaParser.OrExprContext ctx) {
        // Se houver apenas um operando, retorna seu tipo sem forçar BOOLEAN
        if (ctx.andExpr().size() == 1) {
            return visit(ctx.andExpr(0));
        }
        TugaType type = visit(ctx.andExpr(0));
        for (int i = 1; i < ctx.andExpr().size(); i++) {
            TugaType nextType = visit(ctx.andExpr(i));
            if (type != TugaType.BOOLEAN || nextType != TugaType.BOOLEAN) {
                typeError(ctx.getText(), "Operador 'ou' aplicado a tipos não booleanos");
                return TugaType.ERROR;
            }
        }
        return TugaType.BOOLEAN;
    }

    // andExpr: andExpr : equalityExpr ('e' equalityExpr)*
    @Override
    public TugaType visitAndExpr(TugaParser.AndExprContext ctx) {
        // Se houver apenas um operando, retorna seu tipo sem forçar BOOLEAN
        if (ctx.equalityExpr().size() == 1) {
            return visit(ctx.equalityExpr(0));
        }
        TugaType type = visit(ctx.equalityExpr(0));
        for (int i = 1; i < ctx.equalityExpr().size(); i++) {
            TugaType nextType = visit(ctx.equalityExpr(i));
            if (type != TugaType.BOOLEAN || nextType != TugaType.BOOLEAN) {
                typeError(ctx.getText(), "Operador 'e' aplicado a tipos não booleanos");
                return TugaType.ERROR;
            }
        }
        return TugaType.BOOLEAN;
    }

    // equalityExpr: equalityExpr : relationalExpr (('igual' | 'diferente') relationalExpr)*
    @Override
    public TugaType visitEqualityExpr(TugaParser.EqualityExprContext ctx) {
        // Se houver apenas um operando, retorna seu tipo
        if (ctx.relationalExpr().size() == 1) {
            return visit(ctx.relationalExpr(0));
        }
        TugaType type = visit(ctx.relationalExpr(0));
        for (int i = 1; i < ctx.relationalExpr().size(); i++) {
            TugaType nextType = visit(ctx.relationalExpr(i));
            String op = ctx.getChild(2 * i - 1).getText();
            if (type == TugaType.ERROR || nextType == TugaType.ERROR)
                return TugaType.ERROR;
            if (!type.equals(nextType)) {
                typeError(ctx.getText(), "Operador de igualdade '" + op + "' aplicado a tipos incompatíveis");
                return TugaType.ERROR;
            }
        }
        return TugaType.BOOLEAN;
    }

    // relationalExpr: relationalExpr : addSub (('<' | '>' | '<=' | '>=') addSub)*
    @Override
    public TugaType visitRelationalExpr(TugaParser.RelationalExprContext ctx) {
        // Se houver apenas um operando, retorna seu tipo
        if (ctx.addSub().size() == 1) {
            return visit(ctx.addSub(0));
        }
        TugaType left = visit(ctx.addSub(0));
        for (int i = 1; i < ctx.addSub().size(); i++) {
            TugaType right = visit(ctx.addSub(i));
            String op = ctx.getChild(2 * i - 1).getText();
            if ((left == TugaType.INT || left == TugaType.REAL) &&
                    (right == TugaType.INT || right == TugaType.REAL)) {
                // Conversões implícitas serão tratadas no gerador de código
            } else {
                typeError(ctx.getText(), "Operador relacional '" + op + "' aplicado a tipos inválidos");
                return TugaType.ERROR;
            }
        }
        return TugaType.BOOLEAN;
    }

    // addSub: addSub : mulDivMod (('+' | '-') mulDivMod)*
    @Override
    public TugaType visitAddSub(TugaParser.AddSubContext ctx) {
        TugaType left = visit(ctx.mulDivMod(0));
        for (int i = 1; i < ctx.mulDivMod().size(); i++) {
            TugaType right = visit(ctx.mulDivMod(i));
            String op = ctx.getChild(2 * i - 1).getText();

            // Verifica se há concatenação de strings
            if (left == TugaType.STRING || right == TugaType.STRING) {
                if (op.equals("+"))
                    left = TugaType.STRING;
                else {
                    typeError(ctx.getText(), "Operador '-' não definido para strings");
                    return TugaType.ERROR;
                }
            } else if ((left == TugaType.INT || left == TugaType.REAL) &&
                    (right == TugaType.INT || right == TugaType.REAL)) {
                left = (left == TugaType.REAL || right == TugaType.REAL) ? TugaType.REAL : TugaType.INT;
            } else {
                typeError(ctx.getText(), "Operador '" + op + "' aplicado a tipos inválidos");
                return TugaType.ERROR;
            }
        }
        return left;
    }

    // mulDivMod: mulDivMod : unary (('*' | '/' | '%') unary)*
    @Override
    public TugaType visitMulDivMod(TugaParser.MulDivModContext ctx) {
        TugaType left = visit(ctx.unary(0));
        for (int i = 1; i < ctx.unary().size(); i++) {
            TugaType right = visit(ctx.unary(i));
            String op = ctx.getChild(2 * i - 1).getText();
            if ((left == TugaType.INT || left == TugaType.REAL) &&
                    (right == TugaType.INT || right == TugaType.REAL)) {
                left = (left == TugaType.REAL || right == TugaType.REAL) ? TugaType.REAL : TugaType.INT;
            } else {
                typeError(ctx.getText(), "Operador '" + op + "' aplicado a tipos inválidos");
                return TugaType.ERROR;
            }
        }
        return left;
    }

    // unary: unary : ('nao' | '-') unary | primary
    @Override
    public TugaType visitUnary(TugaParser.UnaryContext ctx) {
        if (ctx.getChild(0).getText().equals("-") || ctx.getChild(0).getText().equals("nao")) {
            String op = ctx.getChild(0).getText();
            TugaType type = visit(ctx.unary());
            if (op.equals("-")) {
                if (type == TugaType.INT || type == TugaType.REAL)
                    return type;
                else {
                    typeError(ctx.getText(), "Operador '-' aplicado a tipo não numérico");
                    return TugaType.ERROR;
                }
            } else { // "nao"
                if (type == TugaType.BOOLEAN)
                    return TugaType.BOOLEAN;
                else {
                    typeError(ctx.getText(), "Operador 'nao' aplicado a tipo não booleano");
                    return TugaType.ERROR;
                }
            }
        } else {
            return visit(ctx.primary());
        }
    }

    // primary: primary : literal | '(' expression ')'
    @Override
    public TugaType visitPrimary(TugaParser.PrimaryContext ctx) {
        if (ctx.literal() != null)
            return visit(ctx.literal());
        else
            return visit(ctx.expression());
    }

    // Literais: os métodos para int, real, string, verdadeiro e falso permanecem inalterados
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

    private void typeError(String context, String msg) {
        if (showErrors)
            System.err.println("Erro de tipos na expressão '" + context + "': " + msg);
        typeCheckSuccessful = false;
    }
}
