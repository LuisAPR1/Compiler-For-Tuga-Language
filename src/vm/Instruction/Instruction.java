package vm.Instruction;

import vm.OpCode;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Representa uma instrução de 0 argumentos.
 * Usada para escrever/ler bytecodes e para mostrar listagens amigáveis.
 */
public class Instruction {

    /* opcode puro (a enum OpCode já sabe se tem 0 ou 1 argumento) */
    protected final OpCode opc;

    public Instruction(OpCode opc) {
        this.opc = opc;
    }

    /** Quantos operandos esta instrução transporta (0 para esta classe). */
    public int nArgs() { return 0; }

    public OpCode getOpCode() { return opc; }

    /* -------- escrita em ficheiro .bc -------- */
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(opc.ordinal());
    }

    @Override
    public String toString() {
        return opc.toString();
    }
}
