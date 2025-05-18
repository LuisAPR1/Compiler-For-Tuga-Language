package semantic;

import java.util.List;

/**
 * Símbolo de função.
 */
public class FunctionSymbol extends Symbol {
    private final List<TugaType> paramTypes;
    private final TugaType       retType;  // null ⇒ void
    private       int            addr = -1;

    public FunctionSymbol(String name, List<TugaType> params, TugaType retType) {
        super(name, TugaType.ERROR);
        this.paramTypes = params;
        this.retType    = retType;
    }

    public List<TugaType> getParamTypes()   { return paramTypes; }
    public TugaType       getReturnType()   { return retType; }
    public int            getAddr()         { return addr; }
    public void           setAddr(int addr) { this.addr = addr; }
}
