package semantic;

import java.util.*;

/**
 * Um escopo (frame) da linguagem Tuga.
 *  – Mantém símbolos declarados no bloco/função actual.
 *  – Garante procura encadeada através da referência para o <i>parent</i>.
 *  – Para variáveis locais, gere o próximo <i>offset</i> positivo (FP + offset).
 *
 * No frame de execução da S-VM reservamos:
 *   FP-2, FP-3, ...   →  parâmetros formais (offsets negativos)
 *   FP+0              →  FP antigo  (salvo por CALL)
 *   FP+1              →  endereço de retorno
 *   FP+2, FP+3, ...   →  variáveis locais declaradas via lalloc
 */
public class Scope {

    private final Scope                  parent;
    private final Map<String, Symbol>    table = new LinkedHashMap<>();
    private int nextLocalOffset = 2;   // começa em +2 (0: FP antigo, 1: RET)

    public Scope(Scope parent) { this.parent = parent; }

    /* ---------------- Gestão de símbolos ---------------- */

    /** Devolve <code>true</code> se a definição NÃO existia e foi bem sucedida. */
    public boolean define(Symbol s) {
        if (table.containsKey(s.getName())) return false;
        table.put(s.getName(), s);
        return true;
    }

    /** Procura recursivamente pelo símbolo com o dado nome. */
    public Symbol resolve(String name) {
        for (Scope s = this; s != null; s = s.parent) {
            Symbol found = s.table.get(name);
            if (found != null) return found;
        }
        return null;
    }


    public Symbol resolve_local(String name) {
        return table.get(name);
    }



    /* ---------------- Offset de variáveis locais ---------------- */

    /** Offset seguinte disponível para uma variável local (FP + offset). */
    public int nextLocalOffset() { return nextLocalOffset++; }

    public Scope getParent() { return parent; }

    /** Returns the symbol table for this scope. */
    public Map<String, Symbol> getSymbolTable() { return table; }
}
