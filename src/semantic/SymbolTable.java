package semantic;

import java.util.*;

/**
 * Tabela de símbolos com escopos encadeados.
 *  – «globals» é sempre o escopo raíz.
 *  – pushScope / popScope criam e removem novos blocos (incluindo corpos de funções).
 *  – Guarda também uma lista linear de variáveis globais para saber o índice em galloc.
 */
public class SymbolTable {

    private final Scope globals = new Scope(null);
    private       Scope current = globals;
    private final List<VarSymbol> globalList = new ArrayList<>();

    /** Cria um novo escopo filho do actual. */
    public void pushScope() { current = new Scope(current); }

    /** Sobe para o escopo pai. */
    public void popScope()  { current = current.getParent(); }

    /**
     * Declara uma variável.
     * @param id nome da variável
     * @param t tipo
     * @param isGlobal true=global, false=local
     * @return false se já existia em algum escopo visível
     */
    public boolean declareVar(String id, TugaType t, boolean isGlobal) {
        return declareVar(id, t, isGlobal, null);
    }

    /**
     * Declara uma variável, permitindo especificar offset explícito (para parâmetros).
     * @param id nome
     * @param t tipo
     * @param isGlobal global ou local
     * @param explicitOffset se não null, usa este deslocamento; senão auto aloca
     * @return false se nome já existir
     */
    public boolean declareVar(String id, TugaType t, boolean isGlobal, Integer explicitOffset) {
        if (resolve(id) != null) return false;
        int offset;
        if (isGlobal) {
            offset = globalList.size();
            VarSymbol v = new VarSymbol(id, t, true, offset);
            globals.define(v);
            globalList.add(v);
        } else {
            if (explicitOffset != null) {
                offset = explicitOffset;
            } else {
                offset = current.nextLocalOffset();
            }
            current.define(new VarSymbol(id, t, false, offset));
        }
        return true;
    }

    /**
     * Declara uma função no escopo global.
     * @param f símbolo de função
     * @return false se já existia
     */
    public boolean declareFunction(FunctionSymbol f) {
        return globals.define(f);
    }

    /** Procura símbolo (variável ou função) no escopo actual ou em pais. */
    public Symbol resolve(String id) {
        return current.resolve(id);
    }

    /** Número de variáveis globais (para galloc). */
    public int numGlobals() {
        return globalList.size();
    }

    /** Índice de uma variável global em galloc (ou -1). */
    public int globalIndex(String id) {
        for (VarSymbol v : globalList) {
            if (v.getName().equals(id)) return v.getOffset();
        }
        return -1;
    }

    /** Returns the current scope. */
    public Scope getCurrentScope() { return current; }
}
