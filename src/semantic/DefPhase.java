package semantic;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.ParserRuleContext;
import src.TugaBaseListener;
import src.TugaParser;

import java.util.ArrayList;
import java.util.List;

/**
 * DefPhase — primeira fase semântica: só define símbolos (variáveis, funções) e scopes.
 * Guarda para cada bloco a Scope correspondente em scopesProperty, para ser usada depois.
 */
public class DefPhase extends TugaBaseListener {
    public final ParseTreeProperty<Scope> scopesProperty = new ParseTreeProperty<>();
    public Scope globalScope = null;
    private Scope currentScope = null;
    public final SymbolTable symtab = new SymbolTable();

    @Override
    public void enterProg(TugaParser.ProgContext ctx) {
        globalScope = symtab.getCurrentScope();
        currentScope = globalScope;
    }

    @Override
    public void enterDecl(TugaParser.DeclContext ctx) {
        TugaType type = translateType(ctx.tipo());
        for (TerminalNode id : ctx.ID()) {
            // Check if the variable is already declared in the current scope
            if (currentScope.resolve_local(id.getText()) != null) {
                // Skip declaration if already exists - this prevents the error
                continue;
            }
            VarSymbol vs = new VarSymbol(id.getText(), type, currentScope == globalScope, -1); // offset só interessa no codegen
            currentScope.define(vs);
        }
    }

    @Override
    public void enterFdecl(TugaParser.FdeclContext ctx) {
        String fname = ctx.ID().getText();
        List<TugaType> params = new ArrayList<>();
        if (ctx.paramList() != null)
            for (var p : ctx.paramList().param())
                params.add(translateType(p.tipo()));
        TugaType ret = (ctx.tipo() != null) ? translateType(ctx.tipo()) : null;
        FunctionSymbol fs = new FunctionSymbol(fname, params, ret);
        currentScope.define(fs);
    }

    @Override
    public void enterBlock(TugaParser.BlockContext ctx) {
        Scope localScope = new Scope(currentScope);
        scopesProperty.put(ctx, localScope);
        currentScope = localScope;
    }

    @Override
    public void exitBlock(TugaParser.BlockContext ctx) {
        currentScope = currentScope.getParent();
    }

    @Override
    public void enterParamList(TugaParser.ParamListContext ctx) {
        // handled in enterParam
    }

    @Override
    public void enterParam(TugaParser.ParamContext ctx) {
        // Os parâmetros são tratados no codegen/refphase (offsets negativos). Aqui podemos apenas guardar o nome/tipo.
        // Se quiseres registar já como VarSymbol no scope local da função, podes fazer:
        // Só funciona se currentScope for o da função!
        TugaType type = translateType(ctx.tipo());
        VarSymbol vs = new VarSymbol(ctx.ID().getText(), type, false, -1); // offset só interessa no codegen
        currentScope.define(vs);
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
