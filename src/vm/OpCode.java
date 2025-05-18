package vm;

/**
 * OpCodes da S-VM (parte 3).
 *  – Cada constante contém o nº de argumentos (0 ou 1) que o bytecode carrega.
 *  – A ordem é importante: ordinal() == byte lido/escrito no ficheiro .bc
 */
public enum OpCode {
    /* -------- Integer -------- */
    iconst(1), iadd(0), isub(0), imult(0), idiv(0), imod(0), iuminus(0),
    ilt(0), ileq(0), ieq(0), ineq(0), iprint(0), itos(0),

    /* -------- Real -------- */
    dconst(1), dadd(0), dsub(0), dmult(0), ddiv(0), duminus(0),
    dlt(0), dleq(0), deq(0), dneq(0), dprint(0), dtos(0), itod(0),

    /* -------- String -------- */
    sconst(1), sprint(0), sconcat(0), seq(0), sneq(0),

    /* -------- Boolean -------- */
    tconst(0), fconst(0), bprint(0), and(0), or(0), not(0),
    beq(0), bneq(0), btos(0),

    /* -------- Controlo de fluxo & globais -------- */
    jump(1), jumpf(1),
    galloc(1), gload(1), gstore(1),

    /* -------- Parte 3 – Funções & variáveis locais -------- */
    lalloc(1),   // aloca n posições no topo do stack para locais (inicializadas a NIL)
    lload(1),    // empilha Stack[FP + addr]
    lstore(1),   // pop → Stack[FP + addr]
    pop(1),      // descarta n valores do topo do stack
    call(1),     // chama função no endereço addr
    retval(1),   // return de função não-void (n = nº de args)
    ret(1),      // return de função void      (n = nº de args)

    /* -------- Vários -------- */
    halt(0);

    /* ===== infra-estrutura ===== */
    private final int nArgs;
    OpCode(int nArgs) { this.nArgs = nArgs; }
    public int nArgs() { return nArgs; }

    /** Converte um byte lido do ficheiro .bc para o OpCode correspondente. */
    public static OpCode convert(byte b) { return values()[b]; }
}
