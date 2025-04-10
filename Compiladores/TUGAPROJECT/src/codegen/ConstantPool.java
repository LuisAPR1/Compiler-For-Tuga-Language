package codegen;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConstantPool {
    private List<Object> pool;

    public ConstantPool() {
        pool = new ArrayList<>();
    }

    public int addConstant(Object constant) {
        int index = pool.indexOf(constant);
        if (index >= 0)
            return index;
        pool.add(constant);
        return pool.size() - 1;
    }

    public List<Object> getPool() {
        return pool;
    }

    public void writeTo(DataOutputStream out) throws IOException {
        out.writeInt(pool.size()); // Write the size of the constant pool
        for (Object constant : pool) {
            if (constant instanceof Integer) {
                out.writeByte(0); // Tag for Integer
                out.writeInt((Integer) constant);
            } else if (constant instanceof Double) {
                out.writeByte(1); // Tag for Double
                out.writeDouble((Double) constant);
            } else if (constant instanceof String) {
                out.writeByte(2); // Tag for String
                out.writeUTF((String) constant);
            } else if (constant instanceof Boolean) {
                out.writeByte(3); // Tag for Boolean
                out.writeBoolean((Boolean) constant);
            } else {
                throw new RuntimeException("Unsupported constant pool type: " + constant.getClass());
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("*** Constant pool ***\n");
        for (int i = 0; i < pool.size(); i++) {
            sb.append(i).append(": ");
            Object constant = pool.get(i);
            if (constant instanceof String) {
                sb.append("\"").append(constant).append("\"");
            } else {
                sb.append(constant);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}