// Generated from C:/Users/Luisr/Documents/Univ_Prog/COMP_2425/Compiladores/src/Tuga.g4 by ANTLR 4.13.2
package src;
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
	 * Visit a parse tree produced by {@link TugaParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(TugaParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link TugaParser#decl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecl(TugaParser.DeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TugaParser#fdecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFdecl(TugaParser.FdeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link TugaParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(TugaParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link TugaParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(TugaParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link TugaParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(TugaParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Write}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWrite(TugaParser.WriteContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Assign}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign(TugaParser.AssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CallStat}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallStat(TugaParser.CallStatContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Return}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn(TugaParser.ReturnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code StatBlock}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatBlock(TugaParser.StatBlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code While}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhile(TugaParser.WhileContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfElse}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfElse(TugaParser.IfElseContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Empty}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEmpty(TugaParser.EmptyContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ORlogic}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitORlogic(TugaParser.ORlogicContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSub(TugaParser.AddSubContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParens(TugaParser.ParensContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Uminus}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUminus(TugaParser.UminusContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Var}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar(TugaParser.VarContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Relational}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelational(TugaParser.RelationalContext ctx);
	/**
	 * Visit a parse tree produced by the {@code String}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(TugaParser.StringContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Int}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt(TugaParser.IntContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FuncCall}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncCall(TugaParser.FuncCallContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDivMod(TugaParser.MulDivModContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EqDif}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqDif(TugaParser.EqDifContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Real}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReal(TugaParser.RealContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Boolean}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean(TugaParser.BooleanContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Elogic}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElogic(TugaParser.ElogicContext ctx);
	/**
	 * Visit a parse tree produced by {@link TugaParser#argList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgList(TugaParser.ArgListContext ctx);
	/**
	 * Visit a parse tree produced by {@link TugaParser#tipo}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTipo(TugaParser.TipoContext ctx);
}