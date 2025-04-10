package vm.Instruction;

import vm.OpCode;

import java.io.DataOutputStream;
import java.io.IOException;

public class Instruction {
    OpCode opc;

    public Instruction(OpCode opc) {
        this.opc = opc;
    }

    public OpCode getOpCode() {
        return opc;
    }

    public int nArgs() {
        return 0;
    }

    @Override
    public String toString() {
        return opc.toString();
    }

    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(opc.ordinal());
    }
}