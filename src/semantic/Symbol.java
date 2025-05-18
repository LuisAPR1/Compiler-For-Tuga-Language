package semantic;

/**
 * Super-classe abstrata de todos os símbolos (variáveis e funções).
 */
public abstract class Symbol {
    private final String   name;
    private final TugaType type;

    protected Symbol(String name, TugaType type) {
        this.name = name;
        this.type = type;
    }

    public String   getName() { return name; }
    public TugaType getType() { return type; }
}
