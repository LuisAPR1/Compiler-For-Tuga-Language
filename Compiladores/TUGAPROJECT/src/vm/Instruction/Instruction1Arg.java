package vm.Instruction;

import vm.OpCode;

import java.io.DataOutputStream;
import java.io.IOException;

public class Instruction1Arg extends Instruction {
    int arg;

    public Instruction1Arg(OpCode opc, int arg) {
        super(opc);
        this.arg = arg;
    }

    public int getArg() {
        return arg;
    }

    public void setArg(int arg) {
        this.arg = arg;
    }

    @Override
    public int nArgs() {
        return 1;
    }

    @Override
    public String toString() {
        return opc.toString() + " " + arg;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        super.writeTo(out);
        out.writeInt(arg);
    }
}