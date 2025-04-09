// Generated from C:/Users/Luisr/Documents/Univ_Prog/COMP_2425/Compiladores/TUGAPROJECT/src/p1/Tuga.g4 by ANTLR 4.13.2
package p1;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TugaParser}.
 */
public interface TugaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TugaParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(TugaParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(TugaParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterInstruction(TugaParser.InstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitInstruction(TugaParser.InstructionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Not}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNot(TugaParser.NotContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Not}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNot(TugaParser.NotContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMulDivMod(TugaParser.MulDivModContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMulDivMod(TugaParser.MulDivModContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(TugaParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(TugaParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterParens(TugaParser.ParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitParens(TugaParser.ParensContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Relational}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterRelational(TugaParser.RelationalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Relational}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitRelational(TugaParser.RelationalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLiteralExpr(TugaParser.LiteralExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLiteralExpr(TugaParser.LiteralExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Logical}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterLogical(TugaParser.LogicalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Logical}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitLogical(TugaParser.LogicalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Negate}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNegate(TugaParser.NegateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Negate}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNegate(TugaParser.NegateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Equality}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterEquality(TugaParser.EqualityContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Equality}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitEquality(TugaParser.EqualityContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterIntLiteral(TugaParser.IntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitIntLiteral(TugaParser.IntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RealLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterRealLiteral(TugaParser.RealLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RealLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitRealLiteral(TugaParser.RealLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterStringLiteral(TugaParser.StringLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitStringLiteral(TugaParser.StringLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code TrueLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterTrueLiteral(TugaParser.TrueLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code TrueLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitTrueLiteral(TugaParser.TrueLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FalseLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterFalseLiteral(TugaParser.FalseLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FalseLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitFalseLiteral(TugaParser.FalseLiteralContext ctx);
}