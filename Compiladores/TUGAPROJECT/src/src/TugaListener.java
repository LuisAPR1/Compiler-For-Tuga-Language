// Generated from C:/Users/Luisr/Documents/Univ_Prog/COMP_2425/Compiladores/ORIGINAL_TUGAPROJECT/src/Tuga.g4 by ANTLR 4.13.2
package src;
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
	 * Enter a parse tree produced by {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(TugaParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(TugaParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterOrExpr(TugaParser.OrExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitOrExpr(TugaParser.OrExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void enterAndExpr(TugaParser.AndExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void exitAndExpr(TugaParser.AndExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#equalityExpr}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpr(TugaParser.EqualityExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#equalityExpr}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpr(TugaParser.EqualityExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpr(TugaParser.RelationalExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#relationalExpr}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpr(TugaParser.RelationalExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#addSub}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(TugaParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#addSub}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(TugaParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#mulDivMod}.
	 * @param ctx the parse tree
	 */
	void enterMulDivMod(TugaParser.MulDivModContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#mulDivMod}.
	 * @param ctx the parse tree
	 */
	void exitMulDivMod(TugaParser.MulDivModContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#unary}.
	 * @param ctx the parse tree
	 */
	void enterUnary(TugaParser.UnaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#unary}.
	 * @param ctx the parse tree
	 */
	void exitUnary(TugaParser.UnaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(TugaParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(TugaParser.PrimaryContext ctx);
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