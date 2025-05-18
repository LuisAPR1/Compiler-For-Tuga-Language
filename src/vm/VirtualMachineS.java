// ---------------------------------------------------------------------------
//  vm/VirtualMachineS.java   –   S-VM com suporte a Funções (Parte 3)
// ---------------------------------------------------------------------------
package vm;

import vm.Instruction.Instruction;
import vm.Instruction.Instruction1Arg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementação interpretada da S-VM (stack machine) com:
 *   – constant-pool em memória (constPool[])
 *   – memória global independente (Object[] globals)
 *   – stack de execução (List<Object>) + registos IP e FP.
 *   – opcodes da Parte 3 (lalloc, lload, lstore, pop, call, retval, ret)
 */
public class VirtualMachineS {

    /* ------------------------- memória ------------------------- */
    private final Object[]      constPool;    // tabela de constantes
    private final Instruction[] code;         // bytecodes já decodificados

    private Object[]            globals;      // área global (tamanho definido pelo 1º galloc)
    private final List<Object>  stack = new ArrayList<>();

    /* ------------------------- registos ------------------------ */
    private int ip = 0;   // instruction pointer (índice no array code)
    private int fp = 0;   // frame pointer (índice na lista stack)

    /* ------------------------- construtor ---------------------- */
    public VirtualMachineS(Object[] constPool, Instruction[] code) {
        this.constPool = constPool;
        this.code      = code;
    }

    /* ------------------------- helpers ------------------------- */
    private void push(Object v) { stack.add(v); }
    private Object pop()        { return stack.remove(stack.size() - 1); }

    /* ------------------------- execução ------------------------ */
    public void run() {
        mainloop:
        while (true) {
            Instruction ins = code[ip++];
            switch (ins.getOpCode()) {

                /* ======== Constantes ======== */
                case iconst -> push(((Instruction1Arg) ins).getArg());
                case dconst, sconst -> push(constPool[((Instruction1Arg) ins).getArg()]);
                case tconst -> push(Boolean.TRUE);
                case fconst -> push(Boolean.FALSE);

                /* ======== Conversões ======== */
                case itod -> push(((Integer) pop()).doubleValue());
                case itos -> push(Integer.toString((Integer) pop()));
                case dtos -> push(Double.toString((Double) pop()));
                case btos -> push(((Boolean) pop()) ? "verdadeiro" : "falso");

                /* ======== Inteiros ======== */
                case iadd  -> push((Integer) pop() + (Integer) pop());
                case isub  -> { int b = (Integer) pop(); int a = (Integer) pop(); push(a - b); }
                case imult -> push((Integer) pop() * (Integer) pop());
                case idiv  -> { int b = (Integer) pop(); int a = (Integer) pop(); push(a / b); }
                case imod  -> { int b = (Integer) pop(); int a = (Integer) pop(); push(a % b); }
                case iuminus -> push(- (Integer) pop());

                case ilt  -> { int b = (Integer) pop(); int a = (Integer) pop(); push(a <  b); }
                case ileq -> { int b = (Integer) pop(); int a = (Integer) pop(); push(a <= b); }
                case ieq  -> push(((Integer) pop()).equals((Integer) pop()));
                case ineq -> push(!((Integer) pop()).equals((Integer) pop()));

                /* ======== Reais ======== */
                case dadd  -> push((Double) pop() + (Double) pop());
                case dsub  -> { double b = (Double) pop(); double a = (Double) pop(); push(a - b); }
                case dmult -> push((Double) pop() * (Double) pop());
                case ddiv  -> { double b = (Double) pop(); double a = (Double) pop(); push(a / b); }
                case duminus -> push(- (Double) pop());

                case dlt  -> { double b = (Double) pop(); double a = (Double) pop(); push(a <  b); }
                case dleq -> { double b = (Double) pop(); double a = (Double) pop(); push(a <= b); }
                case deq  -> push(((Double) pop()).equals((Double) pop()));
                case dneq -> push(!((Double) pop()).equals((Double) pop()));

                /* ======== Strings ======== */
                case sconcat -> { String b = (String) pop(); String a = (String) pop(); push(a + b); }
                case seq    -> push(((String) pop()).equals((String) pop()));
                case sneq   -> push(!((String) pop()).equals((String) pop()));

                /* ======== Booleanos ======== */
                case and -> push((Boolean) pop() & (Boolean) pop());
                case or  -> push((Boolean) pop() | (Boolean) pop());
                case not -> push(! (Boolean) pop());

                case beq  -> push(((Boolean) pop()).equals((Boolean) pop()));
                case bneq -> push(!((Boolean) pop()).equals((Boolean) pop()));

                /* ======== I/O ======== */
                case iprint -> System.out.println(pop());
                case dprint -> System.out.println(pop());
                case sprint -> System.out.println(pop());
                case bprint -> System.out.println(((Boolean) pop()) ? "verdadeiro" : "falso");

                /* ======== Globais ======== */
                case galloc -> {
                    int n = ((Instruction1Arg) ins).getArg();
                    globals = new Object[n];
                }
                case gload -> {
                    int addr = ((Instruction1Arg) ins).getArg();
                    Object v = globals[addr];
                    if (v == null)
                        throw new RuntimeException("erro de runtime: acesso a global nao inicializada");
                    push(v);
                }
                case gstore -> globals[((Instruction1Arg) ins).getArg()] = pop();

                /* ======== Locais ======== */
                case lalloc -> {
                    int n = ((Instruction1Arg) ins).getArg();
                    for (int i = 0; i < n; i++) push(null);
                }
                case lload -> {
                    int off = ((Instruction1Arg) ins).getArg();
                    Object v = stack.get(fp + off);
                    if (v == null) throw new RuntimeException("erro: local nao inicializada");
                    push(v);
                }
                case lstore -> {
                    int off = ((Instruction1Arg) ins).getArg();
                    stack.set(fp + off, pop());
                }
                case pop -> {
                    int n = ((Instruction1Arg) ins).getArg();
                    for (int i = 0; i < n; i++) pop();
                }

                /* ======== Controlo de fluxo ======== */
                case jump  -> ip = ((Instruction1Arg) ins).getArg();
                case jumpf -> {
                    int addr = ((Instruction1Arg) ins).getArg();
                    if (! (Boolean) pop()) ip = addr;
                }

                /* ======== Chamada de função ======== */
                case call -> {
                    int addr = ((Instruction1Arg) ins).getArg();
                    push(fp);            // (1) guarda FP actual
                    push(ip);            // (2) endereço de retorno (já aponta para próxima instrução)
                    fp = stack.size() - 2; // (3) novo FP = posição onde guardámos o velho FP
                    ip = addr;           // (4) salta para função
                }

                /* ======== Retorno de função (com valor) ======== */
                case retval -> {
                    int nArgs = ((Instruction1Arg) ins).getArg();
                    Object retVal = pop();
                    // 1) remover variáveis locais
                    while (stack.size() > fp + 2) pop();
                    // 2) restaurar estado
                    int retAddr = (Integer) pop();
                    fp          = (Integer) pop();
                    // 3) remover argumentos
                    for (int i = 0; i < nArgs; i++) pop();
                    // 4) empilhar valor devolvido
                    push(retVal);
                    // 5) saltar de volta
                    ip = retAddr;
                }

                /* ======== Retorno void ======== */
                case ret -> {
                    int nArgs = ((Instruction1Arg) ins).getArg();
                    // 1) remover variáveis locais
                    while (stack.size() > fp + 2) pop();
                    // 2) restaurar estado
                    int retAddr = (Integer) pop();
                    fp          = (Integer) pop();
                    // 3) descartar argumentos
                    for (int i = 0; i < nArgs; i++) pop();
                    // 4) voltar
                    ip = retAddr;
                }

                /* ======== Terminar ======== */
                case halt -> { break mainloop; }

                /* ======== Fallback ======== */
                default -> throw new RuntimeException("opcode nao implementado: " + ins.getOpCode());
            }
        }
    }
}
