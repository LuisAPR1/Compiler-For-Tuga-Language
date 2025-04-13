package codegen;

import java.util.ArrayList;
import java.util.List;

public class ConstantPool {

    private final List<Object> pool;

    public ConstantPool() {
        pool = new ArrayList<>();
    }

    public int addConstant(Object constant) {
        int index = pool.indexOf(constant);
        if (index >= 0) {
            return index;
        }
        pool.add(constant);
        return pool.size() - 1;
    }

    public List<Object> getPool() {
        return pool;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("*** Constant pool ***\n");
        for (int i = 0; i < pool.size(); i++) {
            Object obj = pool.get(i);
            if (obj instanceof String) {
                sb.append(i).append(": ").append("\"").append(obj).append("\"\n");
            } else {
                sb.append(i).append(": ").append(obj).append("\n");
            }
        }
        return sb.toString();
    }
}
