package src;// Generated from C:/Users/Luisr/Documents/Univ_Prog/COMP_2425/Compiladores/TUGAPROJECT/src/Tuga.g4 by ANTLR 4.13.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TugaParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TugaVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TugaParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(TugaParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link TugaParser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstruction(TugaParser.InstructionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Not}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot(TugaParser.NotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDivMod(TugaParser.MulDivModContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSub(TugaParser.AddSubContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParens(TugaParser.ParensContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Relational}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelational(TugaParser.RelationalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LiteralExpr}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralExpr(TugaParser.LiteralExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Logical}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogical(TugaParser.LogicalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Negate}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegate(TugaParser.NegateContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Equality}
	 * labeled alternative in {@link TugaParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquality(TugaParser.EqualityContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IntLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLiteral(TugaParser.IntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RealLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRealLiteral(TugaParser.RealLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StringLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringLiteral(TugaParser.StringLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code TrueLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTrueLiteral(TugaParser.TrueLiteralContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FalseLiteral}
	 * labeled alternative in {@link TugaParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFalseLiteral(TugaParser.FalseLiteralContext ctx);
}