package semantic;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.TerminalNode;
import src.TugaBaseListener;
import src.TugaParser;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * RefPhase — segunda fase semântica: verifica usos de nomes e consistência semântica.
 * Valida referências, tipos de argumentos, duplicação, tipos de return, etc.
 */
public class RefPhase extends TugaBaseListener {
    private final ParseTreeProperty<Scope> scopesProperty;
    private Scope currentScope;
    private final List<String> errors = new ArrayList<>();
    private FunctionSymbol currentFunc = null;
    private Set<String> globalDeclarations = new HashSet<>();

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
    public void enterProg(TugaParser.ProgContext ctx) {
        // The prog rule doesn't have direct ID tokens
        // We'll check global declarations when we visit each decl node
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
        
        // Check for duplicate function declarations
        Symbol existingFunc = currentScope.resolve(fname);
        if (existingFunc != null && !fname.equals("principal")) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' ja foi declarado");
        }
        
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
        // Check for duplicate variable declarations
        TugaType type = translateType(ctx.tipo());
        for (TerminalNode id : ctx.ID()) {
            String name = id.getText();
            
            // Check if we're in the global scope
            if (currentScope.getParent() == null) {
                // This is a global declaration
                if (globalDeclarations.contains(name)) {
                    errors.add("erro na linha " + id.getSymbol().getLine() + ": '" + name + "' ja foi declarado");
                } else {
                    globalDeclarations.add(name);
                }
            }
            
            // Also check for local duplicates
            Symbol existingVar = currentScope.resolve_local(name);
            if (existingVar != null) {
                errors.add("erro na linha " + id.getSymbol().getLine() + ": '" + name + "' ja foi declarado");
            }
        }
    }

    @Override
    public void exitAssign(TugaParser.AssignContext ctx) {
        String id = ctx.ID().getText();
        Symbol s = currentScope.resolve(id);
        
        // Check if variable exists
        if (s == null) {
            errors.add("erro na linha " + line(ctx) + ": '" + id + "' nao foi declarado ou nao eh variavel");
            return;
        }
        
        // Check if it's a variable (not a function)
        if (s instanceof FunctionSymbol) {
            errors.add("erro na linha " + line(ctx) + ": '" + id + "' nao eh variavel");
            return;
        }
        
        // Type checking for assignment
        TugaType varType = s.getType();
        TugaType exprType = new TypeCheckingVisitor(false, new SymbolTable()).visit(ctx.expr());
        
        // Handle null type (could be from function call that returns void)
        if (exprType == null) {
            errors.add("erro na linha " + line(ctx) + ": operador '<-' eh invalido entre " + 
                       pt(varType) + " e vazio");
        } 
        // Check type compatibility
        else if (!compatible(varType, exprType)) {
            errors.add("erro na linha " + line(ctx) + ": operador '<-' eh invalido entre " + 
                       pt(varType) + " e " + pt(exprType));
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
        
        // Check if function exists
        if (s == null) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' nao foi declarado");
            return;
        }
        
        // Check if it's a function
        if (!(s instanceof FunctionSymbol)) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' nao eh funcao");
            return;
        }
        
        FunctionSymbol fs = (FunctionSymbol) s;
        
        // Check argument count
        int nArgs = ctx.argList() == null ? 0 : ctx.argList().expr().size();
        if (nArgs != fs.getParamTypes().size()) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' requer " + 
                       fs.getParamTypes().size() + " argumentos");
        }
        
        // Check argument types if count matches
        if (nArgs == fs.getParamTypes().size() && ctx.argList() != null) {
            for (int i = 0; i < nArgs; i++) {
                TugaType paramType = fs.getParamTypes().get(i);
                TugaType argType = new TypeCheckingVisitor(false, new SymbolTable()).visit(ctx.argList().expr(i));
                if (!compatible(paramType, argType)) {
                    errors.add("erro na linha " + line(ctx) + ": '" + (i+1) + "º argumento devia ser do tipo " + 
                               pt(paramType));
                }
            }
        }
    }

    @Override
    public void exitCallStat(TugaParser.CallStatContext ctx) {
        String fname = ctx.ID().getText();
        Symbol s = currentScope.resolve(fname);
        
        // Check if function exists
        if (s == null) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' nao foi declarado");
            return;
        }
        
        // Check if it's a function
        if (!(s instanceof FunctionSymbol)) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' nao eh funcao");
            return;
        }
        
        FunctionSymbol fs = (FunctionSymbol) s;
        
        // Check if function returns a value (should be assigned)
        if (fs.getReturnType() != null) {
            errors.add("erro na linha " + line(ctx) + ": valor de '" + fs.getName() + 
                       "' tem de ser atribuido a uma variavel");
        }
        
        // Check argument count
        int nArgs = ctx.argList() == null ? 0 : ctx.argList().expr().size();
        if (nArgs != fs.getParamTypes().size()) {
            errors.add("erro na linha " + line(ctx) + ": '" + fname + "' requer " + 
                       fs.getParamTypes().size() + " argumentos");
        }
        
        // Check argument types if count matches
        if (nArgs == fs.getParamTypes().size() && ctx.argList() != null) {
            for (int i = 0; i < nArgs; i++) {
                TugaType paramType = fs.getParamTypes().get(i);
                TugaType argType = new TypeCheckingVisitor(false, new SymbolTable()).visit(ctx.argList().expr(i));
                if (!compatible(paramType, argType)) {
                    errors.add("erro na linha " + line(ctx) + ": '" + (i+1) + "º argumento devia ser do tipo " + 
                               pt(paramType));
                }
            }
        }
    }

    @Override
    public void exitReturn(TugaParser.ReturnContext ctx) {
        if (currentFunc == null) {
            errors.add("erro na linha " + line(ctx) + ": 'retorna' fora de funcao");
            return;
        }
        
        // Check return type
        TugaType retType = currentFunc.getReturnType();
        if (ctx.expr() != null) {
            TugaType exprType = new TypeCheckingVisitor(false, new SymbolTable()).visit(ctx.expr());
            if (retType == null) {
                errors.add("erro na linha " + line(ctx) + ": funcao vazia nao deve retornar valor");
            } else if (!compatible(retType, exprType)) {
                errors.add("erro na linha " + line(ctx) + ": return devia ser do tipo " + pt(retType));
            }
        } else if (retType != null) {
            errors.add("erro na linha " + line(ctx) + ": retorna precisa de expressao do tipo " + pt(retType));
        }
    }
    
    @Override
    public void exitEqDif(TugaParser.EqDifContext ctx) {
        TugaType left = new TypeCheckingVisitor(false, new SymbolTable()).visit(ctx.expr(0));
        TugaType right = new TypeCheckingVisitor(false, new SymbolTable()).visit(ctx.expr(1));
        if (left != right) {
            errors.add("erro na linha " + line(ctx) + ": operador '" + ctx.op.getText() + 
                       "' aplicado a tipos incompativeis");
        }
    }
    
    @Override
    public void exitAddSub(TugaParser.AddSubContext ctx) {
        TugaType left = new TypeCheckingVisitor(false, new SymbolTable()).visit(ctx.expr(0));
        TugaType right = new TypeCheckingVisitor(false, new SymbolTable()).visit(ctx.expr(1));
        
        if (ctx.op.getText().equals("+")) {
            // String concatenation is allowed
            if (left == TugaType.STRING || right == TugaType.STRING) {
                return;
            }
        }
        
        // For numeric operations
        if (!numeric(left) || !numeric(right)) {
            errors.add("erro na linha " + line(ctx) + ": operador '" + ctx.op.getText() + 
                       "' aplicado a tipos invalidos");
        }
    }

    @Override
    public void exitProg(TugaParser.ProgContext ctx) {
        // Check for principal function
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
    
    private boolean numeric(TugaType t) {
        return t == TugaType.INT || t == TugaType.REAL;
    }
    
    private boolean compatible(TugaType target, TugaType expr) {
        if (expr == null) return false;
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
}
