package semantic;

/**
 * Símbolo de variável (global ou local).
 */
public class VarSymbol extends Symbol {
    private final boolean global;
    private final int     offset;

    public VarSymbol(String name, TugaType type, boolean global, int offset) {
        super(name, type);
        this.global = global;
        this.offset = offset;
    }

    public boolean isGlobal()  { return global; }
    public int     getOffset() { return offset; }
}
