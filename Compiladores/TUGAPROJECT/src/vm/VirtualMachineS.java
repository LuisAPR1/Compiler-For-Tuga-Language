// VirtualMachineS.java
package vm;

import vm.Instruction.Instruction;
import vm.Instruction.Instruction1Arg;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VirtualMachineS {
    private byte[] bytecodes;
    private Instruction[] code;
    private int IP;
    private Stack<Object> stack;
    private List<Object> constantPool;
    private boolean trace = false; // Add a trace flag if needed

    public VirtualMachineS() {
        stack = new Stack<>();
    }

    public void execute(byte[] bytecodes) {
        this.bytecodes = bytecodes;
        decode();
        this.IP = 0;
        run();
    }

    private void decode() {
        ArrayList<Instruction> instructions = new ArrayList<>();
        try (DataInputStream din = new DataInputStream(new ByteArrayInputStream(bytecodes))) {
            // Read the constant pool
            int constantPoolSize = din.readInt();
            constantPool = new ArrayList<>(constantPoolSize);
            for (int i = 0; i < constantPoolSize; i++) {
                byte typeTag = din.readByte();
                switch (typeTag) {
                    case 0:
                        constantPool.add(din.readInt());
                        break;
                    case 1:
                        constantPool.add(din.readDouble());
                        break;
                    case 2:
                        constantPool.add(din.readUTF());
                        break;
                    case 3:
                        constantPool.add(din.readBoolean());
                        break;
                    default:
                        throw new RuntimeException("Unknown constant pool type tag: " + typeTag);
                }
            }

            // Read the instructions
            while (din.available() > 0) {
                byte opcodeValue = din.readByte();
                OpCode opcode = OpCode.convert(opcodeValue);
                switch (opcode.nArgs()) {
                    case 0:
                        instructions.add(new Instruction(opcode));
                        break;
                    case 1:
                        int arg = din.readInt();
                        instructions.add(new Instruction1Arg(opcode, arg));
                        break;
                    default:
                        throw new RuntimeException("Unsupported number of arguments for opcode: " + opcode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.code = instructions.toArray(new Instruction[0]);
        if (trace) {
            System.out.println("Disassembled instructions:");
            dumpInstructions(bytecodes); // Call the new version internally if needed
        }
    }

    public void dumpInstructions(byte[] bytecodes) {
        try (DataInputStream din = new DataInputStream(new ByteArrayInputStream(bytecodes))) {
            int constantPoolSize = din.readInt();
            System.out.println("*** Constant pool ***");
            for (int i = 0; i < constantPoolSize; i++) {
                byte typeTag = din.readByte();
                System.out.print(i + ": ");
                switch (typeTag) {
                    case 0:
                        System.out.println(din.readInt());
                        break;
                    case 1:
                        System.out.println(din.readDouble());
                        break;
                    case 2:
                        System.out.println("\"" + din.readUTF() + "\""); // Add double quotes here
                        break;
                    case 3:
                        System.out.println(din.readBoolean());
                        break;
                }
            }
            System.out.println("*** Instructions ***");
            List<Instruction> instructions = new ArrayList<>();
            while (din.available() > 0) {
                byte opcodeValue = din.readByte();
                OpCode opcode = OpCode.convert(opcodeValue);
                switch (opcode.nArgs()) {
                    case 0:
                        instructions.add(new Instruction(opcode));
                        break;
                    case 1:
                        int arg = din.readInt();
                        instructions.add(new Instruction1Arg(opcode, arg));
                        break;
                }
            }
            this.code = instructions.toArray(new Instruction[0]);
            for (int i = 0; i < this.code.length; i++) {
                System.out.print(i + ": " + this.code[i].getOpCode());
                if (this.code[i] instanceof Instruction1Arg) {
                    System.out.println(" " + ((Instruction1Arg) this.code[i]).getArg());
                } else {
                    System.out.println();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runtimeError(String message) {
        System.err.println("Runtime Error at instruction " + IP + ": " + message);
        if (trace) {
            System.err.println("Stack: " + stack);
        }
        System.exit(1);
    }

    private void run() {
        while (IP < code.length) {
            Instruction instruction = code[IP];
            OpCode opcode = instruction.getOpCode();
            if (trace) {
                System.out.println("Executing " + IP + ": " + instruction + ", Stack: " + stack);
            }
            switch (opcode) {
                case halt:
                    return;
                case iconst:
                    stack.push(((Instruction1Arg) instruction).getArg());
                    break;
                case dconst:
                    stack.push(constantPool.get(((Instruction1Arg) instruction).getArg()));
                    break;
                case sconst:
                    stack.push(constantPool.get(((Instruction1Arg) instruction).getArg()));
                    break;
                case tconst:
                    stack.push(true);
                    break;
                case fconst:
                    stack.push(false);
                    break;
                case iadd:
                    int i2 = (int) stack.pop();
                    int i1 = (int) stack.pop();
                    stack.push(i1 + i2);
                    break;
                case isub:
                    i2 = (int) stack.pop();
                    i1 = (int) stack.pop();
                    stack.push(i1 - i2);
                    break;
                case imult:
                    i2 = (int) stack.pop();
                    i1 = (int) stack.pop();
                    stack.push(i1 * i2);
                    break;
                case idiv:
                    i2 = (int) stack.pop();
                    i1 = (int) stack.pop();
                    if (i2 == 0) runtimeError("Division by zero");
                    stack.push(i1 / i2);
                    break;
                case imod:
                    i2 = (int) stack.pop();
                    i1 = (int) stack.pop();
                    if (i2 == 0) runtimeError("Modulo by zero");
                    stack.push(i1 % i2);
                    break;
                case iuminus:
                    stack.push(-(int) stack.pop());
                    break;
                case ilt:
                    i2 = (int) stack.pop();
                    i1 = (int) stack.pop();
                    stack.push(i1 < i2);
                    break;
                case ileq:
                    i2 = (int) stack.pop();
                    i1 = (int) stack.pop();
                    stack.push(i1 <= i2);
                    break;
                case igt:
                    i2 = (int) stack.pop();
                    i1 = (int) stack.pop();
                    stack.push(i1 > i2);
                    break;
                case igeq:
                    i2 = (int) stack.pop();
                    i1 = (int) stack.pop();
                    stack.push(i1 >= i2);
                    break;
                case ieq:
                    stack.push(stack.pop().equals(stack.pop()));
                    break;
                case ineq:
                    stack.push(!stack.pop().equals(stack.pop()));
                    break;
                case iprint:
                    System.out.println(stack.pop());
                    break;
                case dadd:
                    double d2 = (double) stack.pop();
                    double d1 = (double) stack.pop();
                    stack.push(d1 + d2);
                    break;
                case dsub:
                    d2 = (double) stack.pop();
                    d1 = (double) stack.pop();
                    stack.push(d1 - d2);
                    break;
                case dmult:
                    d2 = (double) stack.pop();
                    d1 = (double) stack.pop();
                    stack.push(d1 * d2);
                    break;
                case ddiv:
                    d2 = (double) stack.pop();
                    d1 = (double) stack.pop();
                    if (d2 == 0) runtimeError("Division by zero");
                    stack.push(d1 / d2);
                    break;
                case dmod:
                    d2 = (double) stack.pop();
                    d1 = (double) stack.pop();
                    if (d2 == 0) runtimeError("Modulo by zero");
                    stack.push(d1 % d2);
                    break;
                case duminus:
                    stack.push(-(double) stack.pop());
                    break;
                case dlt:
                    double d2_lt = (double) stack.pop();
                    double d1_lt = (double) stack.pop();
                    stack.push(d1_lt < d2_lt);
                    break;
                case dleq:
                    double d2_leq = (double) stack.pop();
                    double d1_leq = (double) stack.pop();
                    stack.push(d1_leq <= d2_leq);
                    break;
                case dgt:
                    double d2_gt = (double) stack.pop();
                    double d1_gt = (double) stack.pop();
                    stack.push(d1_gt > d2_gt);
                    break;
                case dgeq:
                    double d2_geq = (double) stack.pop();
                    double d1_geq = (double) stack.pop();
                    stack.push(d1_geq >= d2_geq);
                    break;
                case deq:
                    stack.push(stack.pop().equals(stack.pop()));
                    break;
                case dneq:
                    stack.push(!stack.pop().equals(stack.pop()));
                    break;
                case dprint:
                    System.out.println(stack.pop());
                    break;
                case sconcat:
                    String s2 = (String) stack.pop();
                    String s1 = (String) stack.pop();
                    stack.push(s1 + s2);
                    break;
                case sprint:
                    System.out.println(stack.pop());
                    break;
                case seq:
                    stack.push(stack.pop().equals(stack.pop()));
                    break;
                case sneq:
                    stack.push(!stack.pop().equals(stack.pop()));
                    break;
                case and:
                    boolean b2 = (boolean) stack.pop();
                    boolean b1 = (boolean) stack.pop();
                    stack.push(b1 && b2);
                    break;
                case or:
                    b2 = (boolean) stack.pop();
                    b1 = (boolean) stack.pop();
                    stack.push(b1 || b2);
                    break;
                case not:
                    stack.push(!(boolean) stack.pop());
                    break;
                case bprint:
                    if ((boolean) stack.pop()) {
                        System.out.println("verdadeiro");
                    } else {
                        System.out.println("falso");
                    }
                    break;
                case beq:
                    stack.push(stack.pop().equals(stack.pop()));
                    break;
                case bneq:
                    stack.push(!stack.pop().equals(stack.pop()));
                    break;
                case itod:
                    stack.push((double) (int) stack.pop());
                    break;
                case itos:
                    stack.push(String.valueOf((int) stack.pop()));
                    break;
                case dtos:
                    stack.push(String.valueOf((double) stack.pop()));
                    break;
                case btos:
                    if ((boolean) stack.pop()) {
                        stack.push("verdadeiro");
                    } else {
                        stack.push("falso");
                    }
                    break;
                default:
                    runtimeError("Unknown opcode: " + opcode);
            }
            IP++;
        }
    }
}