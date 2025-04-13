package vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class VirtualMachineS {

    public void execute(byte[] bytecode) {
        String codeText = new String(bytecode);
        String[] lines = codeText.split("\\r?\\n");

        List<String> constantPool = new ArrayList<>();
        List<String> instructions = new ArrayList<>();
        boolean inPool = false, inInstr = false;

        for (String line : lines) {
            line = line.trim();
            if (line.equals("*** Constant pool ***")) {
                inPool = true;
                inInstr = false;
                continue;
            }
            if (line.equals("*** Instructions ***")) {
                inInstr = true;
                inPool = false;
                continue;
            }
            if (inPool && !line.isEmpty()) {
                int colonIndex = line.indexOf(":");
                if (colonIndex >= 0) {
                    String constant = line.substring(colonIndex + 1).trim();
                    if (constant.startsWith("\"") && constant.endsWith("\"") && constant.length() >= 2) {
                        constant = constant.substring(1, constant.length() - 1);
                    }
                    constantPool.add(constant);
                }
            }
            if (inInstr && !line.isEmpty()) {
                int colonIndex = line.indexOf(":");
                if (colonIndex >= 0) {
                    String instr = line.substring(colonIndex + 1).trim();
                    instructions.add(instr);
                }
            }
        }

        Stack<Object> stack = new Stack<>();
        int ip = 0;
        while (ip < instructions.size()) {
            String instrLine = instructions.get(ip);
            String[] parts = instrLine.split(" ");
            String opcode = parts[0];
            switch (opcode) {
                case "iconst": {
                    int value = Integer.parseInt(parts[1]);
                    stack.push(value);
                    break;
                }
                case "dconst": {
                    int index = Integer.parseInt(parts[1]);
                    double d = Double.parseDouble(constantPool.get(index));
                    stack.push(d);
                    break;
                }
                case "sconst": {
                    int index = Integer.parseInt(parts[1]);
                    stack.push(constantPool.get(index));
                    break;
                }
                case "tconst": {
                    stack.push(true);
                    break;
                }
                case "fconst": {
                    stack.push(false);
                    break;
                }
                case "itod": {
                    int value = (Integer) stack.pop();
                    stack.push((double) value);
                    break;
                }
                case "itos": {
                    int value = (Integer) stack.pop();
                    stack.push(String.valueOf(value));
                    break;
                }
                case "dtos": {
                    double value = (Double) stack.pop();
                    stack.push(String.valueOf(value));
                    break;
                }
                case "iadd": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a + b);
                    break;
                }
                case "isub": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a - b);
                    break;
                }
                case "imult": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a * b);
                    break;
                }
                case "idiv": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a / b);
                    break;
                }
                case "imod": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a % b);
                    break;
                }
                case "iuminus": {
                    int a = (Integer) stack.pop();
                    stack.push(-a);
                    break;
                }
                case "dadd": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a + b);
                    break;
                }
                case "dsub": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a - b);
                    break;
                }
                case "dmult": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a * b);
                    break;
                }
                case "ddiv": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a / b);
                    break;
                }
                case "dmod": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a % b);
                    break;
                }
                case "duminus": {
                    double a = (Double) stack.pop();
                    stack.push(-a);
                    break;
                }
                // Operações de comparação para inteiros
                case "ieq": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a == b);
                    break;
                }
                case "ineq": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a != b);
                    break;
                }
                case "ilt": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a < b);
                    break;
                }
                case "ileq": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a <= b);
                    break;
                }
                case "igt": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a > b);
                    break;
                }
                case "igeq": {
                    int b = (Integer) stack.pop();
                    int a = (Integer) stack.pop();
                    stack.push(a >= b);
                    break;
                }
                // Comparações para doubles
                case "deq": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a == b);
                    break;
                }
                case "dneq": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a != b);
                    break;
                }
                case "dlt": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a < b);
                    break;
                }
                case "dleq": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a <= b);
                    break;
                }
                case "dgt": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a > b);
                    break;
                }
                case "dgeq": {
                    double b = (Double) stack.pop();
                    double a = (Double) stack.pop();
                    stack.push(a >= b);
                    break;
                }
                // Comparações de strings
                case "seq": {
                    String b = (String) stack.pop();
                    String a = (String) stack.pop();
                    stack.push(a.equals(b));
                    break;
                }
                case "sneq": {
                    String b = (String) stack.pop();
                    String a = (String) stack.pop();
                    stack.push(!a.equals(b));
                    break;
                }
                // Comparações booleanas
                case "beq": {
                    boolean b = (Boolean) stack.pop();
                    boolean a = (Boolean) stack.pop();
                    stack.push(a == b);
                    break;
                }
                case "bneq": {
                    boolean b = (Boolean) stack.pop();
                    boolean a = (Boolean) stack.pop();
                    stack.push(a != b);
                    break;
                }
                // Operadores lógicos
                case "and": {
                    boolean b = (Boolean) stack.pop();
                    boolean a = (Boolean) stack.pop();
                    stack.push(a && b);
                    break;
                }
                case "or": {
                    boolean b = (Boolean) stack.pop();
                    boolean a = (Boolean) stack.pop();
                    stack.push(a || b);
                    break;
                }
                case "not": {
                    boolean a = (Boolean) stack.pop();
                    stack.push(!a);
                    break;
                }
                // Conversões para string
                case "btos": {
                    boolean value = (Boolean) stack.pop();
                    stack.push(value ? "verdadeiro" : "falso");
                    break;
                }
                // Concatenação de strings
                case "sconcat": {
                    String b = (String) stack.pop();
                    String a = (String) stack.pop();
                    if (a.endsWith("=") && (b.isEmpty() || b.charAt(0) != ' ')) {
                        a = a + " ";
                    }
                    stack.push(a + b);
                    break;
                }
                // Impressão
                case "iprint": {
                    Object value = stack.pop();
                    System.out.println(value);
                    break;
                }
                case "dprint": {
                    Object value = stack.pop();
                    System.out.println(value);
                    break;
                }
                case "sprint": {
                    Object value = stack.pop();
                    System.out.println(value);
                    break;
                }
                case "bprint": {
                    boolean value = (Boolean) stack.pop();
                    System.out.println(value ? "verdadeiro" : "falso");
                    break;
                }
                case "halt": {
                    return;
                }
                default: {
                    System.err.println("Instrução desconhecida: " + opcode);
                    return;
                }
            }
            ip++;
        }
    }
}
