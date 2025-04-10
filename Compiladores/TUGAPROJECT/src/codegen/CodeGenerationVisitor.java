package codegen;

import src.TugaBaseVisitor;
import src.TugaParser;
import semantic.TugaType;
import semantic.TypeCheckingVisitor;
import vm.OpCode;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CodeGenerationVisitor extends TugaBaseVisitor<Void> { // Specify <Void> here
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
        emit(OpCode.halt); // opcode 40
        return null;
    }

    @Override
    public Void visitInstruction(TugaParser.InstructionContext ctx) {
        visit(ctx.expression());
        TugaType type = getType(ctx.expression());
        switch (type) {
            case INT:
                emit(OpCode.iprint); // opcode 3
                break;
            case REAL:
                emit(OpCode.dprint); // opcode 16
                break;
            case STRING:
                emit(OpCode.sprint); // opcode 27
                break;
            case BOOLEAN:
                emit(OpCode.bprint); // opcode 33
                break;
            default:
                emit(OpCode.iprint);
        }
        return null;
    }

    @Override
    public Void visitLiteralExpr(TugaParser.LiteralExprContext ctx) {
        visit(ctx.literal());
        return null; // Changed to return null
    }

    @Override
    public Void visitParens(TugaParser.ParensContext ctx) {
        visit(ctx.expression());
        return null; // Changed to return null
    }

    @Override
    public Void visitNegate(TugaParser.NegateContext ctx) {
        TugaType type = getType(ctx.expression());
        visit(ctx.expression());
        if (type == TugaType.REAL) {
            emit(OpCode.duminus); // opcode 17
        } else {
            emit(OpCode.iuminus); // opcode 4
        }
        return null;
    }

    @Override
    public Void visitNot(TugaParser.NotContext ctx) {
        visit(ctx.expression());
        emit(OpCode.not); // opcode 38
        return null;
    }

    @Override
    public Void visitAddSub(TugaParser.AddSubContext ctx) {
        TugaType leftType = getType(ctx.expression(0));
        TugaType rightType = getType(ctx.expression(1));
        String op = ctx.op.getText();

        if (leftType == TugaType.STRING || rightType == TugaType.STRING) {
            visit(ctx.expression(0));
            if (leftType != TugaType.STRING) {
                if (leftType == TugaType.INT)
                    emit(OpCode.itos); // opcode 15
                else if (leftType == TugaType.REAL)
                    emit(OpCode.dtos); // opcode 26
                else if (leftType == TugaType.BOOLEAN)
                    emit(OpCode.btos); // opcode 39
            }
            visit(ctx.expression(1));
            if (rightType != TugaType.STRING) {
                if (rightType == TugaType.INT)
                    emit(OpCode.itos);
                else if (rightType == TugaType.REAL)
                    emit(OpCode.dtos);
                else if (rightType == TugaType.BOOLEAN)
                    emit(OpCode.btos);
            }
            emit(OpCode.sconcat); // opcode 28
            return null;
        }

        if (leftType == TugaType.REAL || rightType == TugaType.REAL) {
            visit(ctx.expression(0));
            if (leftType == TugaType.INT)
                emit(OpCode.itod); // opcode 14
            visit(ctx.expression(1));
            if (rightType == TugaType.INT)
                emit(OpCode.itod);
            if (op.equals("+"))
                emit(OpCode.dadd); // opcode 18
            else
                emit(OpCode.dsub); // opcode 19
        } else {
            visit(ctx.expression(0));
            visit(ctx.expression(1));
            if (op.equals("+"))
                emit(OpCode.iadd); // opcode 5
            else
                emit(OpCode.isub); // opcode 6
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
                emit(OpCode.itod);
            visit(ctx.expression(1));
            if (rightType == TugaType.INT)
                emit(OpCode.itod);
            if (op.equals("*"))
                emit(OpCode.dmult); // opcode 20
            else if (op.equals("/"))
                emit(OpCode.ddiv); // opcode 21
            else if (op.equals("%"))
                emit(OpCode.dmod); // opcional – conforme implementação
        } else {
            visit(ctx.expression(0));
            visit(ctx.expression(1));
            if (op.equals("*"))
                emit(OpCode.imult); // opcode 7
            else if (op.equals("/"))
                emit(OpCode.idiv); // opcode 8
            else if (op.equals("%"))
                emit(OpCode.imod); // opcode 9
        }
        return null;
    }

    @Override
    public Void visitRelational(TugaParser.RelationalContext ctx) {
        TugaType leftType = getType(ctx.expression(0));
        TugaType rightType = getType(ctx.expression(1));
        String op = ctx.op.getText();
        if (leftType == TugaType.REAL || rightType == TugaType.REAL) {
            visit(ctx.expression(0));
            if (leftType == TugaType.INT)
                emit(OpCode.itod);
            visit(ctx.expression(1));
            if (rightType == TugaType.INT)
                emit(OpCode.itod);
            if (op.equals("<"))
                emit(OpCode.dlt); // opcode 24
            else if (op.equals("<="))
                emit(OpCode.dleq); // opcode 25
            else if (op.equals(">"))
                emit(OpCode.dgt);  // (definido na VM)
            else if (op.equals(">="))
                emit(OpCode.dgeq); // (definido na VM)
        } else {
            visit(ctx.expression(0));
            visit(ctx.expression(1));
            if (op.equals("<"))
                emit(OpCode.ilt); // opcode 12
            else if (op.equals("<="))
                emit(OpCode.ileq); // opcode 13
            else if (op.equals(">"))
                emit(OpCode.igt);  // (definido na VM)
            else if (op.equals(">="))
                emit(OpCode.igeq); // (definido na VM)
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
                case INT:
                    emit(OpCode.ieq);
                    break; // opcode 10
                case REAL:
                    emit(OpCode.deq);
                    break; // opcode 22
                case STRING:
                    emit(OpCode.seq);
                    break; // opcode 29
                case BOOLEAN:
                    emit(OpCode.beq);
                    break; // opcode 34
                default:
                    emit(OpCode.ieq);
            }
        } else { // "diferente"
            switch (type) {
                case INT:
                    emit(OpCode.ineq);
                    break; // opcode 11
                case REAL:
                    emit(OpCode.dneq);
                    break; // opcode 23
                case STRING:
                    emit(OpCode.sneq);
                    break; // opcode 30
                case BOOLEAN:
                    emit(OpCode.bneq);
                    break; // opcode 35
                default:
                    emit(OpCode.ineq);
            }
        }
        return null;
    }

    @Override
    public Void visitLogical(TugaParser.LogicalContext ctx) {
        visit(ctx.expression(0));
        visit(ctx.expression(1));
        if (ctx.op.getText().equals("e"))
            emit(OpCode.and); // opcode 36
        else if (ctx.op.getText().equals("ou"))
            emit(OpCode.or);  // opcode 37
        return null;
    }

    @Override
    public Void visitIntLiteral(TugaParser.IntLiteralContext ctx) {
        emit(OpCode.iconst, Integer.parseInt(ctx.INT().getText())); // opcode 0
        return null;
    }

    @Override
    public Void visitRealLiteral(TugaParser.RealLiteralContext ctx) {
        int index = constantPool.addConstant(Double.valueOf(ctx.REAL().getText()));
        emit(OpCode.dconst, index); // opcode 1
        return null;
    }

    @Override
    public Void visitStringLiteral(TugaParser.StringLiteralContext ctx) {
        String text = ctx.STRING().getText();
        String str = text.substring(1, text.length() - 1);
        int index = constantPool.addConstant(str);
        emit(OpCode.sconst, index); // opcode 2
        return null;
    }

    @Override
    public Void visitTrueLiteral(TugaParser.TrueLiteralContext ctx) {
        emit(OpCode.tconst); // opcode 31
        return null;
    }

    @Override
    public Void visitFalseLiteral(TugaParser.FalseLiteralContext ctx) {
        emit(OpCode.fconst); // opcode 32
        return null;
    }

    private TugaType getType(TugaParser.ExpressionContext ctx) {
        return new TypeCheckingVisitor(false).visit(ctx);
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