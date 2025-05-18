import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import src.TugaLexer;
import src.TugaParser;
import codegen.CodeGenerationVisitor;
import codegen.ConstantPool;
import semantic.DefPhase;
import semantic.RefPhase;
import semantic.SymbolTable;
import vm.Instruction.Instruction;
import vm.Instruction.Instruction1Arg;
import vm.OpCode;
import vm.VirtualMachineS;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class TugaCompileAndRun {

    /* -------------------------------------------------- */
    /* 1. Ler o código-fonte (stdin ou ficheiro)           */
    /* -------------------------------------------------- */
    private static CharStream readSource(String[] args) throws IOException {
        if (args.length > 0) {
            return CharStreams.fromFileName(args[0]);
        }
        StringBuilder sb = new StringBuilder();
        try (Scanner in = new Scanner(System.in)) {
            while (in.hasNextLine()) {
                sb.append(in.nextLine()).append('\n');
            }
        }
        return CharStreams.fromString(sb.toString());
    }

    /* -------------------------------------------------- */
    /* 2. Decoder: bytes -> constant-pool + instructions  */
    /* -------------------------------------------------- */
    private static Object[][] decode(byte[] bytes) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(bytes);

        // constant-pool
        int nConst = buf.getInt();
        Object[] cpool = new Object[nConst];
        for (int i = 0; i < nConst; i++) {
            byte tag = buf.get();
            if (tag == 0x01) {
                cpool[i] = buf.getDouble();
            } else if (tag == 0x03) {
                int len = buf.getInt();
                byte[] utf16 = new byte[len * 2];
                buf.get(utf16);
                cpool[i] = new String(utf16, StandardCharsets.UTF_16BE);
            } else {
                throw new IOException("tag desconhecido: " + tag);
            }
        }

        // instructions
        List<Instruction> list = new ArrayList<>();
        while (buf.hasRemaining()) {
            OpCode op = OpCode.convert(buf.get());
            if (op.nArgs() == 0) {
                list.add(new Instruction(op));
            } else {
                list.add(new Instruction1Arg(op, buf.getInt()));
            }
        }
        return new Object[][] { cpool, list.toArray(new Instruction[0]) };
    }

    /* -------------------------------------------------- */
    public static void main(String[] args) {
        boolean showLexerErrors  = false; // false para submissão Mooshak
        boolean showParserErrors = false;

        try {
            // 1. Leitura
            CharStream input = readSource(args);

            // 2. Lexer + Parser com listener de erros
            TugaLexer lexer = new TugaLexer(input);
            MyErrorListener errListener = new MyErrorListener(showLexerErrors, showParserErrors);
            lexer.removeErrorListeners();
            lexer.addErrorListener(errListener);

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            TugaParser parser = new TugaParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(errListener);

            ParseTree tree = parser.prog();

            if (errListener.getNumLexerErrors() > 0) {
                System.out.println("Input tem erros lexicais");
                return;
            }
            if (errListener.getNumParsingErrors() > 0) {
                System.out.println("Input tem erros de parsing");
                return;
            }

            // 3. Análise semântica em DUAS FASES (DefPhase/RefPhase)
            ParseTreeWalker walker = new ParseTreeWalker();

            // Fase 1: Definição (DefPhase)
            semantic.DefPhase def = new semantic.DefPhase();
            walker.walk(def, tree);

            // Fase 2: Referências e validações (RefPhase)
            semantic.RefPhase ref = new semantic.RefPhase(def.scopesProperty, def.globalScope);
            walker.walk(ref, tree);

            if (!ref.getErrors().isEmpty()) {
                for (String err : ref.getErrors())
                    System.out.println(err);
                return;
            }

            // 4. Geração de código
            CodeGenerationVisitor cg = new CodeGenerationVisitor(def.symtab);
            cg.visit(tree);
            
            // Check if the code generation visitor found any errors
            if (!cg.getErrors().isEmpty()) {
                for (String err : cg.getErrors()) {
                    System.out.println(err);
                }
                return;
            }

            // 5. Escrita dos bytecodes em ficheiro
            ConstantPool cp = cg.getConstantPool();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            cp.writeTo(dos);
            for (Instruction ins : cg.getInstructions()) {
                ins.writeTo(dos);
            }
            dos.close();

            byte[] bytecodes = bos.toByteArray();
            Files.write(new File("bytecodes.bc").toPath(), bytecodes);

            // 6. (Opcional) listagem na consola
            System.out.print(cp.toString());
            System.out.println("*** Instructions ***");
            List<Instruction> instList = cg.getInstructions();
            for (int i = 0; i < instList.size(); i++) {
                System.out.println(i + ": " + instList.get(i));
            }

            // 7. Execução na VM
            System.out.println("*** VM output ***");
            Object[][] decoded = decode(bytecodes);
            new VirtualMachineS(decoded[0], (Instruction[]) decoded[1]).run();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
