
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import codegen.CodeGenerationVisitor;
import codegen.ConstantPool;
import semantic.TypeCheckingVisitor;
import vm.VirtualMachineS;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

public class TugaCompileAndRun {
    public static void main(String[] args) {
        boolean showLexerErrors = false;
        boolean showParserErrors = false;
        boolean showTypeCheckingErrors = false;

        String inputFile = (args.length > 0) ? args[0] : null;
        CharStream input = null;

        try {
            if (inputFile != null) {
                input = CharStreams.fromFileName(inputFile);
            } else {
                StringBuilder sourceBuilder = new StringBuilder();
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty())
                        break;
                    sourceBuilder.append(line).append("\n");
                }
                String src = sourceBuilder.toString().trim();
                input = CharStreams.fromString(src);
                scanner.close();
            }

            src.TugaLexer lexer = new src.TugaLexer(input);
            MyErrorListener errorListener = new MyErrorListener(showLexerErrors, showParserErrors);
            lexer.removeErrorListeners();
            lexer.addErrorListener(errorListener);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            src.TugaParser parser = new src.TugaParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            ParseTree tree = parser.program();

            if (errorListener.getNumLexerErrors() > 0) {
                System.out.println("Input has lexical errors");
                return;
            }
            if (errorListener.getNumParsingErrors() > 0) {
                System.out.println("Input has parsing errors");
                return;
            }

            TypeCheckingVisitor typeChecker = new TypeCheckingVisitor(showTypeCheckingErrors);
            typeChecker.visit(tree);
            if (!typeChecker.isTypeCheckSuccessful()) {
                System.out.println("Input has type checking errors");
                return;
            }

            CodeGenerationVisitor codeGen = new CodeGenerationVisitor();
            codeGen.visit(tree);
            byte[] bytecodes = codeGen.getBytecode();
            ConstantPool constantPool = codeGen.getConstantPool();

            // Write constant pool and bytecodes to file
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(bos);
            constantPool.writeTo(dos);
            dos.write(bytecodes);
            byte[] finalBytecodes = bos.toByteArray();
            dos.close();

            FileOutputStream fos = new FileOutputStream("bytecodes.bc");
            fos.write(finalBytecodes);
            fos.close();

            // Read immediately the generated file
            byte[] codeFromFile = Files.readAllBytes(new File("bytecodes.bc").toPath());

            // Print constant pool and instructions
            VirtualMachineS tempVm = new VirtualMachineS();
            tempVm.dumpInstructions(codeFromFile); // Call the new method with the byte array

            System.out.println("*** VM output ***");
            VirtualMachineS vm = new VirtualMachineS();
            vm.execute(codeFromFile);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}