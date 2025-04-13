package codegen;

import src.TugaBaseVisitor;
import src.TugaParser;
import semantic.TugaType;
import semantic.TypeCheckingVisitor;
import vm.OpCode;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CodeGenerationVisitor extends TugaBaseVisitor<Void> {
    private ByteArrayOutputStream bytecodeStream;
    private DataOutputStream dataOutputStream;
    private ConstantPool constantPool;

    public CodeGenerationVisitor() {
        bytecodeStream = new ByteArrayOutputStream();
        dataOutputStream = new DataOutputStream(bytecodeStream);
        constantPool = new ConstantPool();
    }

    private void emit(OpCode opcode) {
        try {
            dataOutputStream.writeByte(opcode.ordinal());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void emit(OpCode opcode, int value) {
        try {
            dataOutputStream.writeByte(opcode.ordinal());
            dataOutputStream.writeInt(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Void visitProgram(TugaParser.ProgramContext ctx) {
        for (TugaParser.InstructionContext instr : ctx.instruction())
            visit(instr);
        emit(OpCode.halt); // Emite o opcode halt (por exemplo, opcode 40)
        return null;
    }

    @Override
    public Void visitInstruction(TugaParser.InstructionContext ctx) {
        // Avalia a expressão associada à instrução "escreve"
        visit(ctx.expression());
        TugaType type = getType(ctx.expression());
        switch (type) {
            case INT:
                emit(OpCode.iprint);
                break;
            case REAL:
                emit(OpCode.dprint);
                break;
            case STRING:
                emit(OpCode.sprint);
                break;
            case BOOLEAN:
                emit(OpCode.bprint);
                break;
            default:
                emit(OpCode.iprint);
        }
        return null;
    }

    // orExpr: orExpr : andExpr ('ou' andExpr)*
    @Override
    public Void visitOrExpr(TugaParser.OrExprContext ctx) {
        visit(ctx.andExpr(0));
        for (int i = 1; i < ctx.andExpr().size(); i++) {
            visit(ctx.andExpr(i));
            emit(OpCode.or);
        }
        return null;
    }

    // andExpr: andExpr : equalityExpr ('e' equalityExpr)*
    @Override
    public Void visitAndExpr(TugaParser.AndExprContext ctx) {
        visit(ctx.equalityExpr(0));
        for (int i = 1; i < ctx.equalityExpr().size(); i++) {
            visit(ctx.equalityExpr(i));
            emit(OpCode.and);
        }
        return null;
    }

    // equalityExpr: equalityExpr : relationalExpr (('igual' | 'diferente') relationalExpr)*
    @Override
    public Void visitEqualityExpr(TugaParser.EqualityExprContext ctx) {
        visit(ctx.relationalExpr(0));
        TugaType type = getType(ctx.relationalExpr(0));
        for (int i = 1; i < ctx.relationalExpr().size(); i++) {
            visit(ctx.relationalExpr(i));
            String op = ctx.getChild(2 * i - 1).getText();
            if (op.equals("igual")) {
                switch (type) {
                    case INT: emit(OpCode.ieq); break;
                    case REAL: emit(OpCode.deq); break;
                    case STRING: emit(OpCode.seq); break;
                    case BOOLEAN: emit(OpCode.beq); break;
                    default: emit(OpCode.ieq);
                }
            } else { // "diferente"
                switch (type) {
                    case INT: emit(OpCode.ineq); break;
                    case REAL: emit(OpCode.dneq); break;
                    case STRING: emit(OpCode.sneq); break;
                    case BOOLEAN: emit(OpCode.bneq); break;
                    default: emit(OpCode.ineq);
                }
            }
        }
        return null;
    }

    // ===================== CORREÇÃO EM visitRelationalExpr =====================
    // relationalExpr: relationalExpr : addSub (('<' | '>' | '<=' | '>=') addSub)*
    @Override
    public Void visitRelationalExpr(TugaParser.RelationalExprContext ctx) {
        // Se houver apenas um operando, apenas o avalia
        if (ctx.addSub().size() == 1) {
            visit(ctx.addSub(0));
            return null;
        }
        // Avalia o operando esquerdo
        visit(ctx.addSub(0));
        TugaType leftType = getType(ctx.addSub(0));

        // Para cada operação relacional (normalmente há apenas uma, mas pode haver encadeamento)
        for (int i = 1; i < ctx.addSub().size(); i++) {
            TugaType rightType = getType(ctx.addSub(i));
            // Determina o tipo alvo: se qualquer operando for REAL, o alvo é REAL; caso contrário, INT
            TugaType targetType = (leftType == TugaType.REAL || rightType == TugaType.REAL) ? TugaType.REAL : TugaType.INT;

            // Se o operando esquerdo for INT e o alvo for REAL, converte imediatamente o valor já empilhado
            if (leftType == TugaType.INT && targetType == TugaType.REAL) {
                emit(OpCode.itod);
            }

            // Avalia o operando direito
            visit(ctx.addSub(i));
            // Se o operando direito for INT e o alvo for REAL, converte o valor empilhado
            if (rightType == TugaType.INT && targetType == TugaType.REAL) {
                emit(OpCode.itod);
            }

            String op = ctx.getChild(2 * i - 1).getText();
            if (targetType == TugaType.REAL) {
                switch (op) {
                    case "<": emit(OpCode.dlt); break;
                    case "<=": emit(OpCode.dleq); break;
                    case ">": emit(OpCode.dgt); break;
                    case ">=": emit(OpCode.dgeq); break;
                }
            } else {
                switch (op) {
                    case "<": emit(OpCode.ilt); break;
                    case "<=": emit(OpCode.ileq); break;
                    case ">": emit(OpCode.igt); break;
                    case ">=": emit(OpCode.igeq); break;
                }
            }
            // O resultado da operação relacional é boolean; para encadeamento (se houver), atualiza leftType
            leftType = TugaType.BOOLEAN;
        }
        return null;
    }

    // ===========================================================================

    // addSub: addSub : mulDivMod (('+' | '-') mulDivMod)*
    @Override
    public Void visitAddSub(TugaParser.AddSubContext ctx) {
        // Avalia e empilha o primeiro operando; guarda o tipo atual
        TugaType currentType = getType(ctx.mulDivMod(0));
        visit(ctx.mulDivMod(0));
        for (int i = 1; i < ctx.mulDivMod().size(); i++) {
            TugaType nextType = getType(ctx.mulDivMod(i));
            // Define o tipo alvo: se algum operando for REAL, o resultado será REAL; caso contrário, INT
            TugaType targetType = (currentType == TugaType.REAL || nextType == TugaType.REAL) ? TugaType.REAL : TugaType.INT;
            // Se o valor acumulado for INT e o tipo alvo for REAL, converte-o imediatamente
            if (currentType == TugaType.INT && targetType == TugaType.REAL) {
                emit(OpCode.itod);
                currentType = TugaType.REAL;
            }
            // Avalia o operando direito
            visit(ctx.mulDivMod(i));
            // Se o operando direito for INT e o alvo for REAL, converte-o
            if (nextType == TugaType.INT && targetType == TugaType.REAL) {
                emit(OpCode.itod);
            }
            String op = ctx.getChild(2 * i - 1).getText();
            if (currentType == TugaType.STRING || nextType == TugaType.STRING) {
                if (currentType != TugaType.STRING) {
                    if (currentType == TugaType.INT)
                        emit(OpCode.itos);
                    else if (currentType == TugaType.REAL)
                        emit(OpCode.dtos);
                    else if (currentType == TugaType.BOOLEAN)
                        emit(OpCode.btos);
                    currentType = TugaType.STRING;
                }
                if (nextType != TugaType.STRING) {
                    if (nextType == TugaType.INT)
                        emit(OpCode.itos);
                    else if (nextType == TugaType.REAL)
                        emit(OpCode.dtos);
                    else if (nextType == TugaType.BOOLEAN)
                        emit(OpCode.btos);
                }
                emit(OpCode.sconcat);
                currentType = TugaType.STRING;
            } else if (targetType == TugaType.REAL) {
                if (op.equals("+"))
                    emit(OpCode.dadd);
                else
                    emit(OpCode.dsub);
                currentType = TugaType.REAL;
            } else {
                if (op.equals("+"))
                    emit(OpCode.iadd);
                else
                    emit(OpCode.isub);
                currentType = TugaType.INT;
            }
        }
        return null;
    }

    // mulDivMod: mulDivMod : unary (('*' | '/' | '%') unary)*
    @Override
    public Void visitMulDivMod(TugaParser.MulDivModContext ctx) {
        // Avalia e empilha o primeiro operando; guarda o tipo atual
        TugaType currentType = getType(ctx.unary(0));
        visit(ctx.unary(0));
        for (int i = 1; i < ctx.unary().size(); i++) {
            TugaType nextType = getType(ctx.unary(i));
            // Define o tipo alvo: se algum operando for REAL, o resultado será REAL; caso contrário, INT
            TugaType targetType = (currentType == TugaType.REAL || nextType == TugaType.REAL) ? TugaType.REAL : TugaType.INT;
            if (currentType == TugaType.INT && targetType == TugaType.REAL) {
                emit(OpCode.itod);
                currentType = TugaType.REAL;
            }
            // Avalia o operando direito
            visit(ctx.unary(i));
            if (nextType == TugaType.INT && targetType == TugaType.REAL) {
                emit(OpCode.itod);
            }
            String op = ctx.getChild(2 * i - 1).getText();
            if (op.equals("*")) {
                if (targetType == TugaType.REAL)
                    emit(OpCode.dmult);
                else
                    emit(OpCode.imult);
            } else if (op.equals("/")) {
                if (targetType == TugaType.REAL)
                    emit(OpCode.ddiv);
                else
                    emit(OpCode.idiv);
            } else if (op.equals("%")) {
                if (targetType == TugaType.REAL)
                    emit(OpCode.dmod);
                else
                    emit(OpCode.imod);
            }
            currentType = targetType;
        }
        return null;
    }

    // unary: unary : ('nao' | '-') unary | primary
    @Override
    public Void visitUnary(TugaParser.UnaryContext ctx) {
        if (ctx.getChild(0).getText().equals("-") || ctx.getChild(0).getText().equals("nao")) {
            String op = ctx.getChild(0).getText();
            visit(ctx.unary());
            if (op.equals("-")) {
                TugaType type = getType(ctx.unary());
                if (type == TugaType.REAL)
                    emit(OpCode.duminus);
                else
                    emit(OpCode.iuminus);
            } else { // "nao"
                emit(OpCode.not);
            }
        } else {
            visit(ctx.primary());
        }
        return null;
    }

    // primary: primary : literal | '(' expression ')'
    @Override
    public Void visitPrimary(TugaParser.PrimaryContext ctx) {
        if (ctx.literal() != null)
            visit(ctx.literal());
        else
            visit(ctx.expression());
        return null;
    }

    @Override
    public Void visitIntLiteral(TugaParser.IntLiteralContext ctx) {
        emit(OpCode.iconst, Integer.parseInt(ctx.INT().getText()));
        return null;
    }

    @Override
    public Void visitRealLiteral(TugaParser.RealLiteralContext ctx) {
        int index = constantPool.addConstant(Double.valueOf(ctx.REAL().getText()));
        emit(OpCode.dconst, index);
        return null;
    }

    @Override
    public Void visitStringLiteral(TugaParser.StringLiteralContext ctx) {
        String text = ctx.STRING().getText();
        String str = text.substring(1, text.length() - 1);
        int index = constantPool.addConstant(str);
        emit(OpCode.sconst, index);
        return null;
    }

    @Override
    public Void visitTrueLiteral(TugaParser.TrueLiteralContext ctx) {
        emit(OpCode.tconst);
        return null;
    }

    @Override
    public Void visitFalseLiteral(TugaParser.FalseLiteralContext ctx) {
        emit(OpCode.fconst);
        return null;
    }

    // Método auxiliar alterado para aceitar qualquer nó (ParseTree)
    private TugaType getType(ParseTree node) {
        return new TypeCheckingVisitor(false).visit(node);
    }

    public byte[] getBytecode() {
        try {
            dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytecodeStream.toByteArray();
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }
}
