package codegen;

import Tuga.TugaBaseVisitor;
import Tuga.TugaParser;
import semantic.TugaType;
import semantic.TypeCheckingVisitor;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerationVisitor extends TugaBaseVisitor<Void> {

    private final List<String> instructions = new ArrayList<>();
    private final ConstantPool constantPool = new ConstantPool();

    public byte[] getBytecode() {
        StringBuilder sb = new StringBuilder();
        sb.append("*** Constant pool ***\n");
        for (int i = 0; i < constantPool.getPool().size(); i++) {
            Object constant = constantPool.getPool().get(i);
            sb.append(i).append(": ");
            sb.append(constant instanceof String ? '"' + constant.toString() + '"' : constant);
            sb.append("\n");
        }
        sb.append("*** Instructions ***\n");
        for (int i = 0; i < instructions.size(); i++) {
            sb.append(i).append(": ").append(instructions.get(i)).append("\n");
        }
        return sb.toString().getBytes();
    }

    private TugaType getType(TugaParser.ExpressionContext ctx) {
        return new TypeCheckingVisitor(false).visit(ctx);
    }

    @Override
    public Void visitProgram(TugaParser.ProgramContext ctx) {
        for (TugaParser.InstructionContext instr : ctx.instruction()) {
            visit(instr);
        }
        instructions.add("halt");
        return null;
    }

    @Override
    public Void visitInstruction(TugaParser.InstructionContext ctx) {
        visit(ctx.expression());
        TugaType type = getType(ctx.expression());
        instructions.add(
                switch (type) {
                    case INT -> "iprint";
                    case REAL -> "dprint";
                    case STRING -> "sprint";
                    case BOOLEAN -> "bprint";
                    default -> "iprint";
                }
        );
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
        visit(ctx.expression());
        instructions.add(getType(ctx.expression()) == TugaType.REAL ? "duminus" : "iuminus");
        return null;
    }

    @Override
    public Void visitNot(TugaParser.NotContext ctx) {
        visit(ctx.expression());
        instructions.add("not");
        return null;
    }

    @Override
    public Void visitAddSub(TugaParser.AddSubContext ctx) {
        TugaType left = getType(ctx.expression(0));
        TugaType right = getType(ctx.expression(1));
        String op = ctx.getChild(1).getText();

        // Operação de concatenação para strings
        if (left == TugaType.STRING || right == TugaType.STRING) {
            visit(ctx.expression(0));
            if (left != TugaType.STRING) {
                instructions.add(castToStr(left));
            }
            visit(ctx.expression(1));
            if (right != TugaType.STRING) {
                instructions.add(castToStr(right));
            }
            instructions.add("sconcat");
            return null;
        }

        // Caso operações numéricas: converte INT -> REAL se necessário
        visit(ctx.expression(0));
        if (left == TugaType.INT && right == TugaType.REAL) {
            instructions.add("itod");
        }
        visit(ctx.expression(1));
        if (right == TugaType.INT && left == TugaType.REAL) {
            instructions.add("itod");
        }

        instructions.add(
                switch (op) {
                    case "+" -> (left == TugaType.REAL || right == TugaType.REAL) ? "dadd" : "iadd";
                    case "-" -> (left == TugaType.REAL || right == TugaType.REAL) ? "dsub" : "isub";
                    default -> throw new RuntimeException("Operador desconhecido: " + op);
                }
        );
        return null;
    }

    @Override
    public Void visitMulDivMod(TugaParser.MulDivModContext ctx) {
        TugaType left = getType(ctx.expression(0));
        TugaType right = getType(ctx.expression(1));
        String op = ctx.getChild(1).getText();

        visit(ctx.expression(0));
        if (left == TugaType.INT && right == TugaType.REAL) {
            instructions.add("itod");
        }
        visit(ctx.expression(1));
        if (right == TugaType.INT && left == TugaType.REAL) {
            instructions.add("itod");
        }

        instructions.add(
                switch (op) {
                    case "*" -> (left == TugaType.REAL || right == TugaType.REAL) ? "dmult" : "imult";
                    case "/" -> (left == TugaType.REAL || right == TugaType.REAL) ? "ddiv" : "idiv";
                    case "%" -> (left == TugaType.REAL || right == TugaType.REAL) ? "dmod" : "imod";
                    default -> throw new RuntimeException("Operador desconhecido: " + op);
                }
        );
        return null;
    }

    @Override
    public Void visitRelational(TugaParser.RelationalContext ctx) {
        String op = ctx.getChild(1).getText();
        TugaType leftType = getType(ctx.expression(0));
        TugaType rightType = getType(ctx.expression(1));

        boolean bothInt = (leftType == TugaType.INT && rightType == TugaType.INT);
        boolean isReal = !bothInt;

        if (op.equals("<") || op.equals("<=")) {
            visit(ctx.expression(0));
            if (leftType == TugaType.INT && isReal) {
                instructions.add("itod");
            }
            visit(ctx.expression(1));
            if (rightType == TugaType.INT && isReal) {
                instructions.add("itod");
            }
            instructions.add(op.equals("<") ? (isReal ? "dlt" : "ilt") : (isReal ? "dleq" : "ileq"));
        } else if (op.equals(">") || op.equals(">=")) {
            visit(ctx.expression(1));
            if (rightType == TugaType.INT && isReal) {
                instructions.add("itod");
            }
            visit(ctx.expression(0));
            if (leftType == TugaType.INT && isReal) {
                instructions.add("itod");
            }
            instructions.add(op.equals(">") ? (isReal ? "dlt" : "ilt") : (isReal ? "dleq" : "ileq"));
        }
        return null;
    }

    @Override
    public Void visitEquality(TugaParser.EqualityContext ctx) {
        TugaType type = getType(ctx.expression(0));
        String op = ctx.getChild(1).getText();

        visit(ctx.expression(0));
        visit(ctx.expression(1));

        boolean isEq = op.equals("igual");
        instructions.add(
                switch (type) {
                    case INT -> isEq ? "ieq" : "ineq";
                    case REAL -> isEq ? "deq" : "dneq";
                    case STRING -> isEq ? "seq" : "sneq";
                    case BOOLEAN -> isEq ? "beq" : "bneq";
                    default -> throw new RuntimeException("Tipo desconhecido em igualdade: " + type);
                }
        );
        return null;
    }

    @Override
    public Void visitLogicalAnd(TugaParser.LogicalAndContext ctx) {
        visit(ctx.expression(0));
        visit(ctx.expression(1));
        instructions.add("and");
        return null;
    }

    @Override
    public Void visitLogicalOr(TugaParser.LogicalOrContext ctx) {
        visit(ctx.expression(0));
        visit(ctx.expression(1));
        instructions.add("or");
        return null;
    }

    @Override
    public Void visitIntLiteral(TugaParser.IntLiteralContext ctx) {
        instructions.add("iconst " + ctx.INT().getText());
        return null;
    }

    @Override
    public Void visitRealLiteral(TugaParser.RealLiteralContext ctx) {
        int index = constantPool.addConstant(Double.valueOf(ctx.REAL().getText()));
        instructions.add("dconst " + index);
        return null;
    }

    @Override
    public Void visitStringLiteral(TugaParser.StringLiteralContext ctx) {
        String raw = ctx.STRING().getText();
        int index = constantPool.addConstant(raw.substring(1, raw.length() - 1));
        instructions.add("sconst " + index);
        return null;
    }

    @Override
    public Void visitTrueLiteral(TugaParser.TrueLiteralContext ctx) {
        instructions.add("tconst");
        return null;
    }

    @Override
    public Void visitFalseLiteral(TugaParser.FalseLiteralContext ctx) {
        instructions.add("fconst");
        return null;
    }

    private String castToStr(TugaType type) {
        return switch (type) {
            case INT -> "itos";
            case REAL -> "dtos";
            case BOOLEAN -> "btos";
            default -> "";
        };
    }
}
