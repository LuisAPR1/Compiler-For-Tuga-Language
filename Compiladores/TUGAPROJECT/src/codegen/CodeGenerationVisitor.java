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
    @Override
    public Void visitRelationalExpr(TugaParser.RelationalExprContext ctx) {
        // Se houver só um operando, apenas avalia.
        if (ctx.addSub().size() == 1) {
            visit(ctx.addSub(0));
            return null;
        }

        // Vamos assumir que no TugaParser não há encadeamento complexo do tipo “3 < 4 > 2”.
        // (Se houver, a lógica para unificar tudo em <, <= exige passos adicionais
        //  mas normalmente esse tipo de expressão é inválido ou se avalia passo a passo.)

        for (int i = 0; i < ctx.addSub().size() - 1; i++) {
            // Extrair lado esquerdo e direito
            TugaParser.AddSubContext leftCtx  = ctx.addSub(i);
            TugaParser.AddSubContext rightCtx = ctx.addSub(i+1);

            String op = ctx.getChild(2 * i + 1).getText(); // o operador: <, >, <=, >=

            // Descobrir tipos
            TugaType leftType  = getType(leftCtx);
            TugaType rightType = getType(rightCtx);
            // Decidir tipo alvo (real ou int), caso precise converter
            TugaType targetType = (leftType == TugaType.REAL || rightType == TugaType.REAL)
                    ? TugaType.REAL : TugaType.INT;

            if (op.equals(">")) {
                // Empilha “right” primeiro
                visit(rightCtx);
                if (rightType == TugaType.INT && targetType == TugaType.REAL) {
                    emit(OpCode.itod);
                }

                // Depois empilha “left”
                visit(leftCtx);
                if (leftType == TugaType.INT && targetType == TugaType.REAL) {
                    emit(OpCode.itod);
                }

                // Emite “lt”
                if (targetType == TugaType.REAL) {
                    emit(OpCode.dlt);
                } else {
                    emit(OpCode.ilt);
                }
            }
            else if (op.equals(">=")) {
                // Empilha “right”
                visit(rightCtx);
                if (rightType == TugaType.INT && targetType == TugaType.REAL) {
                    emit(OpCode.itod);
                }

                // Empilha “left”
                visit(leftCtx);
                if (leftType == TugaType.INT && targetType == TugaType.REAL) {
                    emit(OpCode.itod);
                }

                // Emite “leq”
                if (targetType == TugaType.REAL) {
                    emit(OpCode.dleq);
                } else {
                    emit(OpCode.ileq);
                }
            }
            else if (op.equals("<")) {
                // Empilha “left” primeiro
                visit(leftCtx);
                if (leftType == TugaType.INT && targetType == TugaType.REAL) {
                    emit(OpCode.itod);
                }

                // Empilha “right”
                visit(rightCtx);
                if (rightType == TugaType.INT && targetType == TugaType.REAL) {
                    emit(OpCode.itod);
                }

                // Emite “lt”
                if (targetType == TugaType.REAL) {
                    emit(OpCode.dlt);
                } else {
                    emit(OpCode.ilt);
                }
            }
            else if (op.equals("<=")) {
                // Empilha “left”
                visit(leftCtx);
                if (leftType == TugaType.INT && targetType == TugaType.REAL) {
                    emit(OpCode.itod);
                }

                // Empilha “right”
                visit(rightCtx);
                if (rightType == TugaType.INT && targetType == TugaType.REAL) {
                    emit(OpCode.itod);
                }

                // Emite “leq”
                if (targetType == TugaType.REAL) {
                    emit(OpCode.dleq);
                } else {
                    emit(OpCode.ileq);
                }
            }
        }

        return null;
    }

    // ===========================================================================

    @Override
    public Void visitAddSub(TugaParser.AddSubContext ctx) {
        // Empilha o primeiro operando
        TugaType currentType = getType(ctx.mulDivMod(0));
        visit(ctx.mulDivMod(0)); // <-- empilou o primeiro

        // Percorre os demais operandos
        for (int i = 1; i < ctx.mulDivMod().size(); i++) {
            // Descobre o tipo do próximo operando e o operador
            TugaType nextType = getType(ctx.mulDivMod(i));
            String op = ctx.getChild(2 * i - 1).getText();

            // -----------------------------
            // 1) Caso concatenação de strings
            // -----------------------------
            if (op.equals("+") && (currentType == TugaType.STRING || nextType == TugaType.STRING)) {
                // Se o operando esquerdo ainda não estiver em string, converta agora,
                // pois ele está no topo da pilha (resultado da soma anterior ou do primeiro operando).
                if (currentType != TugaType.STRING) {
                    if (currentType == TugaType.INT) {
                        emit(OpCode.itos);
                    } else if (currentType == TugaType.REAL) {
                        emit(OpCode.dtos);
                    } else if (currentType == TugaType.BOOLEAN) {
                        emit(OpCode.btos);
                    }
                    currentType = TugaType.STRING;
                }

                // Agora visite o lado direito (empilha segundo operando)
                visit(ctx.mulDivMod(i));

                // Se o lado direito não for string, converta-o (agora sim ele está no topo)
                if (nextType != TugaType.STRING) {
                    if (nextType == TugaType.INT) {
                        emit(OpCode.itos);
                    } else if (nextType == TugaType.REAL) {
                        emit(OpCode.dtos);
                    } else if (nextType == TugaType.BOOLEAN) {
                        emit(OpCode.btos);
                    }
                }

                // Faz o sconcat
                emit(OpCode.sconcat);
                currentType = TugaType.STRING; // resultado final é string
            }
            // -----------------------------
            // 2) Caso seja soma/subtração numérica
            // -----------------------------
            else {
                // Precisamos tratar conversões entre int e real
                TugaType targetType = (currentType == TugaType.REAL || nextType == TugaType.REAL)
                        ? TugaType.REAL : TugaType.INT;

                // Se o valor acumulado (left) for int e vamos precisar de real, convertemos.
                // (Ele está no topo da pilha neste momento)
                if (currentType == TugaType.INT && targetType == TugaType.REAL) {
                    emit(OpCode.itod);
                    currentType = TugaType.REAL;
                }

                // Visita o lado direito
                visit(ctx.mulDivMod(i));

                // Se o lado direito for int e precisamos de real, convertemos.
                if (nextType == TugaType.INT && targetType == TugaType.REAL) {
                    emit(OpCode.itod);
                }

                // Agora emitimos a operação
                if (targetType == TugaType.REAL) {
                    if (op.equals("+")) {
                        emit(OpCode.dadd);
                    } else {
                        emit(OpCode.dsub);
                    }
                    currentType = TugaType.REAL;
                } else {
                    if (op.equals("+")) {
                        emit(OpCode.iadd);
                    } else {
                        emit(OpCode.isub);
                    }
                    currentType = TugaType.INT;
                }
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
