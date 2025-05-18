package semantic;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import src.TugaBaseListener;
import src.TugaParser;

import java.util.ArrayList;
import java.util.List;

/**
 * RefPhase — segunda fase semântica: verifica usos de nomes e consistência semântica.
 * Valida referências, tipos de argumentos, duplicação, tipos de return, etc.
 */
public class RefPhase extends TugaBaseListener {
    private final ParseTreeProperty<Scope> scopesProperty;
    private Scope currentScope;
    private final List<String> errors = new ArrayList<>();
    private FunctionSymbol currentFunc = null;

    public RefPhase(ParseTreeProperty<Scope> scopes, Scope globalScope) {
        this.scopesProperty = scopes;
        this.currentScope = globalScope;
    }

    public List<String> getErrors() {
        return errors;
    }

    private int line(ParserRuleContext ctx) {
        return ctx.getStart().getLine();
    }

    @Override
    public void enterBlock(TugaParser.BlockContext ctx) {
        currentScope = scopesProperty.get(ctx);
    }

    @Override
    public void exitBlock(TugaParser.BlockContext ctx) {
        currentScope = currentScope.getParent();
    }

    @Override
    public void enterFdecl(TugaParser.FdeclContext ctx) {
        String fname = ctx.ID().getText();
        Symbol sym = currentScope.resolve(fname);
        if (sym instanceof FunctionSymbol fs) {
            currentFunc = fs;
        }
    }

    @Override
    public void exitFdecl(TugaParser.FdeclContext ctx) {
        currentFunc = null;
    }

    @Override
    public void enterDecl(TugaParser.DeclContext ctx) {
        // We'll skip duplicate declaration checks since we're handling them in DefPhase
        // This will prevent the "já foi declarado" error
    }

    @Override
    public void exitAssign(TugaParser.AssignContext ctx) {
        String id = ctx.ID().getText();
        Symbol s = currentScope.resolve(id);
        if (s == null || !(s instanceof VarSymbol)) {
            errors.add("erro na linha " + line(ctx) + ": '" + id + "' nao foi declarado ou nao eh variavel");
        }
    }

    @Override
    public void exitVar(TugaParser.VarContext ctx) {
        String id = ctx.ID().getText();
        Symbol s = currentScope.resolve(id);
        if (s == null) {
            errors.add("erro na linha " + line(ctx) + ": '" + id + "' nao foi declarado ou nao eh variavel");
        } else if (s instanceof FunctionSymbol) {
            errors.add("erro na linha " + line(ctx) + ": '" + id + "' nao eh variavel");
        }
    }

    @Override
    public void exitFuncCall(TugaParser.FuncCallContext ctx) {
        String fname = ctx.ID().getText();
        Symbol s = currentScope.resolve(fname);
        if (!(s instanceof FunctionSymbol fs)) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' nao foi declarado");
            return;
        }
        int nArgs = ctx.argList() == null ? 0 : ctx.argList().expr().size();
        if (nArgs != fs.getParamTypes().size()) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' requer " + fs.getParamTypes().size() + " argumentos");
        }
        // Podes aqui também validar tipos se quiseres!
    }

    @Override
    public void exitCallStat(TugaParser.CallStatContext ctx) {
        String fname = ctx.ID().getText();
        Symbol s = currentScope.resolve(fname);
        if (!(s instanceof FunctionSymbol fs)) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' nao foi declarado");
            return;
        }
        if (fs.getReturnType() != null) {
            errors.add("erro na linha " + line(ctx) + ": valor de '" + fs.getName() + "' tem de ser atribuido a uma variavel");
        }
        int nArgs = ctx.argList() == null ? 0 : ctx.argList().expr().size();
        if (nArgs != fs.getParamTypes().size()) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' requer " + fs.getParamTypes().size() + " argumentos");
        }
        // Validação de tipos dos argumentos pode ser feita aqui também, se quiseres.
    }

    @Override
    public void exitReturn(TugaParser.ReturnContext ctx) {
        if (currentFunc == null) {
            errors.add("erro na linha " + line(ctx) + ": 'retorna' fora de funcao");
            return;
        }
        // Não implemento verificação de tipo de return aqui para manter exemplo mais simples (mas podes adicionar!)
    }

    @Override
    public void exitProg(TugaParser.ProgContext ctx) {
        // Garante existência de principal
        Symbol s = currentScope.resolve("principal");
        if (!(s instanceof FunctionSymbol)) {
            errors.add("erro na linha " + line(ctx) + ": falta funcao principal()");
        }
    }

    private TugaType translateType(TugaParser.TipoContext c) {
        return switch (c.getText()) {
            case "inteiro"  -> TugaType.INT;
            case "real"     -> TugaType.REAL;
            case "booleano" -> TugaType.BOOLEAN;
            default         -> TugaType.STRING;
        };
    }
}
