// Generated from C:/Users/Luisr/Documents/Univ_Prog/COMP_2425/Compiladores/src/Tuga.g4 by ANTLR 4.13.2
package src;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TugaParser}.
 */
public interface TugaListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TugaParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(TugaParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(TugaParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#decl}.
	 * @param ctx the parse tree
	 */
	void enterDecl(TugaParser.DeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#decl}.
	 * @param ctx the parse tree
	 */
	void exitDecl(TugaParser.DeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#fdecl}.
	 * @param ctx the parse tree
	 */
	void enterFdecl(TugaParser.FdeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#fdecl}.
	 * @param ctx the parse tree
	 */
	void exitFdecl(TugaParser.FdeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(TugaParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(TugaParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(TugaParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(TugaParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(TugaParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(TugaParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Write}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterWrite(TugaParser.WriteContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Write}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitWrite(TugaParser.WriteContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Assign}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterAssign(TugaParser.AssignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Assign}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitAssign(TugaParser.AssignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CallStat}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterCallStat(TugaParser.CallStatContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CallStat}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitCallStat(TugaParser.CallStatContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Return}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterReturn(TugaParser.ReturnContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Return}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitReturn(TugaParser.ReturnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code StatBlock}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterStatBlock(TugaParser.StatBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code StatBlock}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitStatBlock(TugaParser.StatBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code While}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterWhile(TugaParser.WhileContext ctx);
	/**
	 * Exit a parse tree produced by the {@code While}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitWhile(TugaParser.WhileContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfElse}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterIfElse(TugaParser.IfElseContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfElse}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitIfElse(TugaParser.IfElseContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Empty}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void enterEmpty(TugaParser.EmptyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Empty}
	 * labeled alternative in {@link TugaParser#stat}.
	 * @param ctx the parse tree
	 */
	void exitEmpty(TugaParser.EmptyContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ORlogic}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterORlogic(TugaParser.ORlogicContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ORlogic}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitORlogic(TugaParser.ORlogicContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddSub(TugaParser.AddSubContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddSub}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddSub(TugaParser.AddSubContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParens(TugaParser.ParensContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Parens}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParens(TugaParser.ParensContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Uminus}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUminus(TugaParser.UminusContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Uminus}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUminus(TugaParser.UminusContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Var}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterVar(TugaParser.VarContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Var}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitVar(TugaParser.VarContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Relational}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterRelational(TugaParser.RelationalContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Relational}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitRelational(TugaParser.RelationalContext ctx);
	/**
	 * Enter a parse tree produced by the {@code String}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterString(TugaParser.StringContext ctx);
	/**
	 * Exit a parse tree produced by the {@code String}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitString(TugaParser.StringContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Int}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterInt(TugaParser.IntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Int}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitInt(TugaParser.IntContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FuncCall}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterFuncCall(TugaParser.FuncCallContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FuncCall}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitFuncCall(TugaParser.FuncCallContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMulDivMod(TugaParser.MulDivModContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MulDivMod}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMulDivMod(TugaParser.MulDivModContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EqDif}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterEqDif(TugaParser.EqDifContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EqDif}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitEqDif(TugaParser.EqDifContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Real}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterReal(TugaParser.RealContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Real}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitReal(TugaParser.RealContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Boolean}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBoolean(TugaParser.BooleanContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Boolean}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBoolean(TugaParser.BooleanContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Elogic}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterElogic(TugaParser.ElogicContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Elogic}
	 * labeled alternative in {@link TugaParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitElogic(TugaParser.ElogicContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#argList}.
	 * @param ctx the parse tree
	 */
	void enterArgList(TugaParser.ArgListContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#argList}.
	 * @param ctx the parse tree
	 */
	void exitArgList(TugaParser.ArgListContext ctx);
	/**
	 * Enter a parse tree produced by {@link TugaParser#tipo}.
	 * @param ctx the parse tree
	 */
	void enterTipo(TugaParser.TipoContext ctx);
	/**
	 * Exit a parse tree produced by {@link TugaParser#tipo}.
	 * @param ctx the parse tree
	 */
	void exitTipo(TugaParser.TipoContext ctx);
}