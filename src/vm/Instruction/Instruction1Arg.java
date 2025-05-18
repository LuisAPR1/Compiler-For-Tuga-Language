package vm.Instruction;

import vm.OpCode;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Instrução com um argumento inteiro (32-bit).
 *  – Usada por opcodes que transportam endereços, constantes, etc.
 */
public class Instruction1Arg extends Instruction {

    private int arg;

    public Instruction1Arg(OpCode opc, int arg) {
        super(opc);
        this.arg = arg;
    }

    /* ---------- getters / setters ---------- */
    @Override public int nArgs()   { return 1; }
    public  int getArg()          { return arg; }
    public  void setArg(int arg)  { this.arg = arg; }

    /* ---------- escrita no ficheiro ---------- */
    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        super.writeTo(out);          // opcode
        out.writeInt(arg);           // argumento
    }

    @Override
    public String toString() {
        return opc + " " + arg;
    }
}
