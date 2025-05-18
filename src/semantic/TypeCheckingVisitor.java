// ---------------------------------------------------------------------------
//  semantic/TypeCheckingVisitor.java   –   Parte 3 (funções, escopos aninhados)
// ---------------------------------------------------------------------------
package semantic;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import src.TugaBaseVisitor;
import src.TugaParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Verificação semântica da linguagem Tuga.
 *  – Mantém uma SymbolTable com escopos.
 *  – Garante: declarações duplicadas, tipos nas expressões, aridade e tipos nas
 *    chamadas, return coerente, existência da função principal().
 */
public class TypeCheckingVisitor extends TugaBaseVisitor<TugaType> {

    private final SymbolTable symtab;
    private final boolean     showErrors;
    private boolean           ok = true;

    private FunctionSymbol currentFunc = null;   // função que estamos a visitar

    /* ------------------------------------------------------------------ */
    public TypeCheckingVisitor(boolean showErrors) {
        this(showErrors, new SymbolTable());
    }
    public TypeCheckingVisitor(boolean showErrors, SymbolTable st) {
        this.showErrors = showErrors; this.symtab = st;
    }
    public boolean isTypeCheckSuccessful() { return ok; }

    /* ------------------------------------------------------------------ */
    /*  Utilitários                                                        */
    /* ------------------------------------------------------------------ */

    private int line(ParseTree ctx) {
        return ((ParserRuleContext) ctx).getStart().getLine();
    }

    private void semErr(int ln, String msg) {
        if (showErrors) System.out.println("erro na linha " + ln + ": " + msg);
        ok = false;
    }

    private boolean numeric(TugaType t) {
        return t == TugaType.INT || t == TugaType.REAL;
    }
    private boolean compatible(TugaType target, TugaType expr) {
        return target == expr || (target == TugaType.REAL && expr == TugaType.INT);
    }
    private String pt(TugaType t) {
        return switch (t) {
            case INT -> "inteiro";
            case REAL -> "real";
            case BOOLEAN -> "booleano";
            case STRING -> "string";
            default -> "error";
        };
    }
    private TugaType translateType(TugaParser.TipoContext c) {
        return switch (c.getText()) {
            case "inteiro"  -> TugaType.INT;
            case "real"     -> TugaType.REAL;
            case "booleano" -> TugaType.BOOLEAN;
            default          -> TugaType.STRING;
        };
    }

    /* ------------------------------------------------------------------ */
    /*  P R O G R A M A                                                    */
    /* ------------------------------------------------------------------ */
    @Override
    public TugaType visitProg(TugaParser.ProgContext ctx) {
        /* 1) declarar variáveis globais */
        for (TugaParser.DeclContext d : ctx.decl()) visit(d);

        /* 2) protótipos de funções */
        boolean hasMain = false;
        for (TugaParser.FdeclContext f : ctx.fdecl()) {
            String fname = f.ID().getText();
            List<TugaType> params = new ArrayList<>();
            if (f.paramList() != null)
                for (var p : f.paramList().param()) params.add(translateType(p.tipo()));
            TugaType ret = (f.tipo() != null) ? translateType(f.tipo()) : null;

            FunctionSymbol fs = new FunctionSymbol(fname, params, ret);
            if (!symtab.declareFunction(fs))
                semErr(line(f), "'" + fname + "' ja foi declarado");
            if (fname.equals("principal")) hasMain = true;
        }
        if (!hasMain)
            semErr(line(ctx), "falta funcao principal()");

        /* 3) corpo das funções */
        for (TugaParser.FdeclContext f : ctx.fdecl()) visit(f);
        return null;
    }

    /* ------------------------------------------------------------------ */
    /*  Declarações globais                                               */
    /* ------------------------------------------------------------------ */
    @Override
    public TugaType visitDecl(TugaParser.DeclContext ctx) {
        TugaType t = translateType(ctx.tipo());
        for (TerminalNode id : ctx.ID()) {
            if (!symtab.declareVar(id.getText(), t, true))
                semErr(id.getSymbol().getLine(), "'" + id.getText() + "' ja foi declarado");
        }
        return null;
    }

    /* ------------------------------------------------------------------ */
    /*  Declaração de função                                              */
    /* ------------------------------------------------------------------ */
    @Override
    public TugaType visitFdecl(TugaParser.FdeclContext ctx) {
        currentFunc = (FunctionSymbol) symtab.resolve(ctx.ID().getText());
        symtab.pushScope();

        /* parâmetros são adicionados ao escopo actual (offset negativo tratado na code-gen) */
        if (ctx.paramList() != null) {
            int i = 0;
            for (var p : ctx.paramList().param()) {
                TugaType t = translateType(p.tipo());
                String id  = p.ID().getText();
                if (!symtab.declareVar(id, t, false))
                    semErr(line(p), "parametro '" + id + "' duplicado");
                i++;
            }
        }

        /* corpo */
        visit(ctx.block());
        symtab.popScope();
        currentFunc = null;
        return null;
    }

    /* ------------------------------------------------------------------ */
    /*  Bloco                                                              */
    /* ------------------------------------------------------------------ */
    @Override
    public TugaType visitBlock(TugaParser.BlockContext ctx) {
        symtab.pushScope();
        for (TugaParser.DeclContext d : ctx.decl()) visit(d);
        for (TugaParser.StatContext s : ctx.stat()) visit(s);
        symtab.popScope();
        return null;
    }

    /* ------------------------------------------------------------------ */
    /*  Instruções                                                         */
    /* ------------------------------------------------------------------ */
    @Override
    public TugaType visitAssign(TugaParser.AssignContext ctx) {
        String id = ctx.ID().getText();
        Symbol s  = symtab.resolve(id);
        if (!(s instanceof VarSymbol)) {
            semErr(line(ctx), "'" + id + "' nao foi declarado ou nao eh variavel");
            return null;
        }
        TugaType vType = s.getType();
        TugaType eType = visit(ctx.expr());
        if (!compatible(vType, eType))
            semErr(line(ctx), "operador '<-' eh invalido entre " + pt(vType) + " e " + pt(eType));
        return null;
    }

    @Override
    public TugaType visitWrite(TugaParser.WriteContext ctx) {
        visit(ctx.expr());
        return null;
    }

    @Override
    public TugaType visitCallStat(TugaParser.CallStatContext ctx) {
        FunctionSymbol fs = checkFuncCall(ctx.ID().getText(), ctx.argList(), ctx);
        if (fs != null && fs.getReturnType() != null)
            semErr(line(ctx), "valor de '" + fs.getName() + "' tem de ser atribuido a uma variavel");
        return null;
    }

    @Override
    public TugaType visitReturn(TugaParser.ReturnContext ctx) {
        if (currentFunc == null) {
            semErr(line(ctx), "'retorna' fora de funcao");
            return null;
        }
        TugaType retType = currentFunc.getReturnType();
        TugaType given   = (ctx.expr() != null) ? visit(ctx.expr()) : null;
        if (retType == null) {
            if (given != null)
                semErr(line(ctx), "funcao vazia nao deve retornar valor");
        } else {
            if (given == null)
                semErr(line(ctx), "retorna precisa de expressao do tipo " + pt(retType));
            else if (!compatible(retType, given))
                semErr(line(ctx), "return devia ser do tipo " + pt(retType));
        }
        return null;
    }

    @Override
    public TugaType visitIfElse(TugaParser.IfElseContext ctx) {
        if (visit(ctx.expr()) != TugaType.BOOLEAN)
            semErr(line(ctx), "expressao de 'se' nao eh do tipo booleano");
        visit(ctx.stat(0));
        if (ctx.stat().size() == 2) visit(ctx.stat(1));
        return null;
    }

    @Override
    public TugaType visitWhile(TugaParser.WhileContext ctx) {
        if (visit(ctx.expr()) != TugaType.BOOLEAN)
            semErr(line(ctx), "expressao de 'enquanto' nao eh do tipo booleano");
        visit(ctx.stat());
        return null;
    }
    @Override
    public TugaType visitStatBlock(TugaParser.StatBlockContext ctx) {
        // é simplesmente um alias para o teu antigo visitBlock
        return visitBlock(ctx.block());
    }


    /* ------------------------------------------------------------------ */
    /*  Expressões                                                         */
    /* ------------------------------------------------------------------ */
    @Override public TugaType visitParens   (TugaParser.ParensContext   c){ return visit(c.expr()); }
    @Override public TugaType visitInt      (TugaParser.IntContext      c){ return TugaType.INT; }
    @Override public TugaType visitReal     (TugaParser.RealContext     c){ return TugaType.REAL; }
    @Override public TugaType visitString   (TugaParser.StringContext   c){ return TugaType.STRING; }
    @Override public TugaType visitBoolean  (TugaParser.BooleanContext  c){ return TugaType.BOOLEAN; }

    @Override
    public TugaType visitVar(TugaParser.VarContext ctx) {
        String id = ctx.ID().getText();
        Symbol s  = symtab.resolve(id);
        if (!(s instanceof VarSymbol)) {
            semErr(line(ctx), "'" + id + "' nao foi declarado ou nao eh variavel");
            return TugaType.ERROR;
        }
        return s.getType();
    }

    /* ---------------- Operadores unários ---------------- */
    @Override
    public TugaType visitUminus(TugaParser.UminusContext ctx) {
        TugaType sub = visit(ctx.expr());
        String op = ctx.op.getText();
        if (op.equals("-")) {
            if (numeric(sub)) return sub;
            semErr(line(ctx), "operador '-' aplicado a tipo nao numerico");
            return TugaType.ERROR;
        } else { // 'nao'
            if (sub == TugaType.BOOLEAN) return TugaType.BOOLEAN;
            semErr(line(ctx), "operador 'nao' aplicado a tipo nao booleano");
            return TugaType.ERROR;
        }
    }

    /* ---------------- Operadores binários ---------------- */
    @Override
    public TugaType visitMulDivMod(TugaParser.MulDivModContext ctx) {
        TugaType a = visit(ctx.expr(0)), b = visit(ctx.expr(1));
        if (!numeric(a) || !numeric(b)) {
            semErr(line(ctx), "operador '" + ctx.op.getText() + "' aplicado a tipos invalidos");
            return TugaType.ERROR;
        }
        return (a == TugaType.REAL || b == TugaType.REAL) ? TugaType.REAL : TugaType.INT;
    }

    @Override
    public TugaType visitAddSub(TugaParser.AddSubContext ctx) {
        TugaType a = visit(ctx.expr(0)), b = visit(ctx.expr(1));
        String op = ctx.op.getText();

        if (op.equals("+") && (a == TugaType.STRING || b == TugaType.STRING))
            return TugaType.STRING;

        if (numeric(a) && numeric(b))
            return (a == TugaType.REAL || b == TugaType.REAL) ? TugaType.REAL : TugaType.INT;

        semErr(line(ctx), "operador '" + op + "' aplicado a tipos invalidos");
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitRelational(TugaParser.RelationalContext ctx) {
        TugaType a = visit(ctx.expr(0)), b = visit(ctx.expr(1));
        if (!numeric(a) || !numeric(b)) {
            semErr(line(ctx), "operador relacional '" + ctx.op.getText() + "' aplicado a tipos invalidos");
            return TugaType.ERROR;
        }
        return TugaType.BOOLEAN;
    }

    @Override
    public TugaType visitEqDif(TugaParser.EqDifContext ctx) {
        TugaType a = visit(ctx.expr(0)), b = visit(ctx.expr(1));
        if (a == b && a != TugaType.ERROR) return TugaType.BOOLEAN;
        semErr(line(ctx), "operador '" + ctx.op.getText() + "' aplicado a tipos incompativeis");
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitElogic(TugaParser.ElogicContext ctx) {
        TugaType a = visit(ctx.expr(0)), b = visit(ctx.expr(1));
        if (a == TugaType.BOOLEAN && b == TugaType.BOOLEAN) return TugaType.BOOLEAN;
        semErr(line(ctx), "operador 'e' aplicado a tipos nao booleanos");
        return TugaType.ERROR;
    }

    @Override
    public TugaType visitORlogic(TugaParser.ORlogicContext ctx) {
        TugaType a = visit(ctx.expr(0)), b = visit(ctx.expr(1));
        if (a == TugaType.BOOLEAN && b == TugaType.BOOLEAN) return TugaType.BOOLEAN;
        semErr(line(ctx), "operador 'ou' aplicado a tipos nao booleanos");
        return TugaType.ERROR;
    }

    /* ---------------- Chamada de função em expressão ---------------- */
    @Override
    public TugaType visitFuncCall(TugaParser.FuncCallContext ctx) {
        FunctionSymbol fs = checkFuncCall(ctx.ID().getText(), ctx.argList(), ctx);
        return fs == null ? TugaType.ERROR : (fs.getReturnType() == null ? TugaType.ERROR : fs.getReturnType());
    }

    /* ------------------------------------------------------------------ */
    /*  Auxiliar: verificação de chamada de função                        */
    /* ------------------------------------------------------------------ */
    private FunctionSymbol checkFuncCall(String fname, TugaParser.ArgListContext args, ParseTree where) {
        Symbol s = symtab.resolve(fname);
        if (!(s instanceof FunctionSymbol)) {
            semErr(line(where), "'" + fname + "' nao foi declarado");
            return null;
        }
        FunctionSymbol fs = (FunctionSymbol) s;
        int nArgs = (args == null) ? 0 : args.expr().size();
        List<TugaType> params = fs.getParamTypes();
        if (nArgs != params.size())
            semErr(line(where), "'" + fname + "' requer " + params.size() + " argumentos");
        else {
            for (int i = 0; i < nArgs; i++) {
                TugaType at = visit(args.expr(i));
                if (!compatible(params.get(i), at))
                    semErr(line(args.expr(i)), "tipo de argumento #" + (i + 1) + " incompativel");
            }
        }
        return fs;
    }
}
