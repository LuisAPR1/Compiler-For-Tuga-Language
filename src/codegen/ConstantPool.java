package codegen;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabela de constantes da S-VM.
 *  – Tag 0x01  →  double
 *  – Tag 0x03  →  string   (UTF-16BE)
 */
public class ConstantPool {

    private final List<Object> pool = new ArrayList<>();

    /** Se a constante já existir devolve o índice antigo; caso contrário acrescenta-a. */
    public int addConstant(Object c) {
        int idx = pool.indexOf(c);
        if (idx >= 0) return idx;
        pool.add(c);
        return pool.size() - 1;
    }

    /** Adiciona uma string à constant-pool. */
    public int addString(String s) {
        return addConstant(s);
    }

    /** Adiciona um double à constant-pool. */
    public int addDouble(double d) {
        return addConstant(d);
    }

    public List<Object> getPool() { return pool; }

    /** Escreve a constant-pool para um DataOutputStream no formato da VM. */
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(pool.size());
        for (Object c : pool) {
            if (c instanceof Double d) {
                out.writeByte(0x01);
                out.writeDouble(d);
            } else if (c instanceof String s) {
                out.writeByte(0x03);
                byte[] utf16 = s.getBytes(StandardCharsets.UTF_16BE);
                out.writeInt(utf16.length / 2);     // nº de caracteres
                out.write(utf16);
            } else {
                throw new RuntimeException("tipo incompatível na constant pool: " + c.getClass());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("*** Constant pool ***\n");
        for (int i = 0; i < pool.size(); i++) {
            sb.append(i).append(": ");
            Object o = pool.get(i);
            sb.append(o instanceof String ? "\"" + o + "\"" : o);
            sb.append('\n');
        }
        return sb.toString();
    }
}
