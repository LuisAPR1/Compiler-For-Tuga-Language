package p1.codegen;

import p1.TugaBaseVisitor;
import p1.TugaParser;
import p1.semantic.TugaType;
import p1.semantic.TypeCheckingVisitor;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerationVisitor extends TugaBaseVisitor<Void> {
    private List<String> instructions;
    private ConstantPool constantPool;

    public CodeGenerationVisitor() {
        instructions = new ArrayList<>();
        constantPool = new ConstantPool();
    }

    @Override
    public Void visitProgram(TugaParser.ProgramContext ctx) {
        for (TugaParser.InstructionContext instr : ctx.instruction())
            visit(instr);
        instructions.add("halt"); // opcode 40
        return null;
    }

    @Override
    public Void visitInstruction(TugaParser.InstructionContext ctx) {
        // Gera o código para avaliar a expressão
        visit(ctx.expression());
        // Escolhe o comando de impressão conforme o tipo da expressão
        TugaType type = getType(ctx.expression());
        switch (type) {
            case INT:
                instructions.add("iprint"); // opcode 3
                break;
            case REAL:
                instructions.add("dprint"); // opcode 16
                break;
            case STRING:
                instructions.add("sprint"); // opcode 27
                break;
            case BOOLEAN:
                instructions.add("bprint"); // opcode 33
                break;
            default:
                instructions.add("iprint");
        }
        return null;
    }

    @Override
    public Void visitLiteralExpr(TugaParser.LiteralExprContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public Void visitParens(TugaParser.ParensContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Void visitNegate(TugaParser.NegateContext ctx) {
        TugaType type = getType(ctx.expression());
        visit(ctx.expression());
        if (type == TugaType.REAL) {
            instructions.add("duminus"); // opcode 17
        } else {
            instructions.add("iuminus"); // opcode 4
        }
        return null;
    }

    @Override
    public Void visitNot(TugaParser.NotContext ctx) {
        visit(ctx.expression());
        instructions.add("not"); // opcode 38
        return null;
    }

    @Override
    public Void visitAddSub(TugaParser.AddSubContext ctx) {
        TugaType leftType = getType(ctx.expression(0));
        TugaType rightType = getType(ctx.expression(1));
        String op = ctx.op.getText();

        // Caso de concatenação de strings (quando pelo menos um operando é string)
        if (leftType == TugaType.STRING || rightType == TugaType.STRING) {
            visit(ctx.expression(0));
            if (leftType != TugaType.STRING) {
                if (leftType == TugaType.INT)
                    instructions.add("itos"); // opcode 15
                else if (leftType == TugaType.REAL)
                    instructions.add("dtos"); // opcode 26
                else if (leftType == TugaType.BOOLEAN)
                    instructions.add("btos"); // opcode 39
            }
            visit(ctx.expression(1));
            if (rightType != TugaType.STRING) {
                if (rightType == TugaType.INT)
                    instructions.add("itos");
                else if (rightType == TugaType.REAL)
                    instructions.add("dtos");
                else if (rightType == TugaType.BOOLEAN)
                    instructions.add("btos");
            }
            instructions.add("sconcat"); // opcode 28
            return null;
        }

        // Operação aritmética numérica: se algum operando é real, gera código para double
        if (leftType == TugaType.REAL || rightType == TugaType.REAL) {
            visit(ctx.expression(0));
            if (leftType == TugaType.INT)
                instructions.add("itod"); // opcode 14
            visit(ctx.expression(1));
            if (rightType == TugaType.INT)
                instructions.add("itod");
            if (op.equals("+"))
                instructions.add("dadd"); // opcode 18
            else
                instructions.add("dsub"); // opcode 19
        } else {
            // Operação inteira
            visit(ctx.expression(0));
            visit(ctx.expression(1));
            if (op.equals("+"))
                instructions.add("iadd"); // opcode 5
            else
                instructions.add("isub"); // opcode 6
        }
        return null;
    }

    @Override
    public Void visitMulDivMod(TugaParser.MulDivModContext ctx) {
        TugaType leftType = getType(ctx.expression(0));
        TugaType rightType = getType(ctx.expression(1));
        String op = ctx.op.getText();
        if (leftType == TugaType.REAL || rightType == TugaType.REAL) {
            visit(ctx.expression(0));
            if (leftType == TugaType.INT)
                instructions.add("itod");
            visit(ctx.expression(1));
            if (rightType == TugaType.INT)
                instructions.add("itod");
            if (op.equals("*"))
                instructions.add("dmult"); // opcode 20
            else if (op.equals("/"))
                instructions.add("ddiv"); // opcode 21
            else if (op.equals("%"))
                instructions.add("dmod"); // opcional – conforme implementação
        } else {
            visit(ctx.expression(0));
            visit(ctx.expression(1));
            if (op.equals("*"))
                instructions.add("imult"); // opcode 7
            else if (op.equals("/"))
                instructions.add("idiv"); // opcode 8
            else if (op.equals("%"))
                instructions.add("imod"); // opcode 9
        }
        return null;
    }

    @Override
    public Void visitRelational(TugaParser.RelationalContext ctx) {
        TugaType leftType = getType(ctx.expression(0));
        TugaType rightType = getType(ctx.expression(1));
        String op = ctx.op.getText();
        // Se os operandos são reais (ou mistos), converte inteiros para double
        if (leftType == TugaType.REAL || rightType == TugaType.REAL) {
            visit(ctx.expression(0));
            if (leftType == TugaType.INT)
                instructions.add("itod");
            visit(ctx.expression(1));
            if (rightType == TugaType.INT)
                instructions.add("itod");
            if (op.equals("<"))
                instructions.add("dlt"); // opcode 24
            else if (op.equals("<="))
                instructions.add("dleq"); // opcode 25
            else if (op.equals(">"))
                instructions.add("dgt"); // (definido na VM)
            else if (op.equals(">="))
                instructions.add("dgeq"); // (definido na VM)
        } else {
            // Operação relacional inteira
            visit(ctx.expression(0));
            visit(ctx.expression(1));
            if (op.equals("<"))
                instructions.add("ilt"); // opcode 12
            else if (op.equals("<="))
                instructions.add("ileq"); // opcode 13
            else if (op.equals(">"))
                instructions.add("igt");  // (definido na VM)
            else if (op.equals(">="))
                instructions.add("igeq");  // (definido na VM)
        }
        return null;
    }

    @Override
    public Void visitEquality(TugaParser.EqualityContext ctx) {
        TugaType type = getType(ctx.expression(0));
        visit(ctx.expression(0));
        visit(ctx.expression(1));
        String op = ctx.op.getText();
        if (op.equals("igual")) {
            switch (type) {
                case INT:    instructions.add("ieq"); break; // opcode 10
                case REAL:   instructions.add("deq"); break; // opcode 22
                case STRING: instructions.add("seq"); break; // opcode 29
                case BOOLEAN:instructions.add("beq"); break; // opcode 34
                default:     instructions.add("ieq");
            }
        } else { // "diferente"
            switch (type) {
                case INT:    instructions.add("ineq"); break; // opcode 11
                case REAL:   instructions.add("dneq"); break; // opcode 23
                case STRING: instructions.add("sneq"); break; // opcode 30
                case BOOLEAN:instructions.add("bneq"); break; // opcode 35
                default:     instructions.add("ineq");
            }
        }
        return null;
    }

    @Override
    public Void visitLogical(TugaParser.LogicalContext ctx) {
        visit(ctx.expression(0));
        visit(ctx.expression(1));
        if (ctx.op.getText().equals("e"))
            instructions.add("and"); // opcode 36
        else if (ctx.op.getText().equals("ou"))
            instructions.add("or");  // opcode 37
        return null;
    }

    @Override
    public Void visitIntLiteral(TugaParser.IntLiteralContext ctx) {
        instructions.add("iconst " + ctx.INT().getText()); // opcode 0
        return null;
    }

    @Override
    public Void visitRealLiteral(TugaParser.RealLiteralContext ctx) {
        int index = constantPool.addConstant(Double.valueOf(ctx.REAL().getText()));
        instructions.add("dconst " + index); // opcode 1
        return null;
    }

    @Override
    public Void visitStringLiteral(TugaParser.StringLiteralContext ctx) {
        String text = ctx.STRING().getText();
        String str = text.substring(1, text.length() - 1);
        int index = constantPool.addConstant(str);
        instructions.add("sconst " + index); // opcode 2
        return null;
    }

    @Override
    public Void visitTrueLiteral(TugaParser.TrueLiteralContext ctx) {
        instructions.add("tconst"); // opcode 31
        return null;
    }

    @Override
    public Void visitFalseLiteral(TugaParser.FalseLiteralContext ctx) {
        instructions.add("fconst"); // opcode 32
        return null;
    }

    // Método auxiliar para determinar o tipo de uma expressão
    private TugaType getType(TugaParser.ExpressionContext ctx) {
        return new TypeCheckingVisitor(false).visit(ctx);
    }

    public byte[] getBytecode() {
        StringBuilder sb = new StringBuilder();
        sb.append("*** Constant pool ***\n");
        List<Object> pool = constantPool.getPool();
        for (int i = 0; i < pool.size(); i++) {
            sb.append(i).append(": ").append(pool.get(i).toString()).append("\n");
        }
        sb.append("*** Instructions ***\n");
        for (int i = 0; i < instructions.size(); i++) {
            sb.append(i).append(": ").append(instructions.get(i)).append("\n");
        }
        return sb.toString().getBytes();
    }
}
