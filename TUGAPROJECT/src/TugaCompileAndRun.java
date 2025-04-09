import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import p1.MyErrorListener;
import p1.semantic.TypeCheckingVisitor;
import p1.codegen.CodeGenerationVisitor;
import p1.vm.VirtualMachineS;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class TugaCompileAndRun {
    public static void main(String[] args) {
        // Flags para controlo de erros – devem ser false para submissão no Mooshak
        boolean showLexerErrors = false;
        boolean showParserErrors = false;
        boolean showTypeCheckingErrors = false;

        String inputFile = (args.length > 0) ? args[0] : null;
        CharStream input = null;

        try {
            // Se houver ficheiro, ler normalmente; se não, ler da consola até EOF
            if (inputFile != null) {
                input = CharStreams.fromFileName(inputFile);
            } else {
                StringBuilder sourceBuilder = new StringBuilder();
                Scanner scanner = new Scanner(System.in);
                while (scanner.hasNextLine()) {
                    sourceBuilder.append(scanner.nextLine()).append("\n");
                }
                String src = sourceBuilder.toString().trim();
                input = CharStreams.fromString(src);
                scanner.close();
            }

            // Criação do lexer e adição do error listener
            p1.TugaLexer lexer = new p1.TugaLexer(input);
            MyErrorListener errorListener = new MyErrorListener(showLexerErrors, showParserErrors);
            lexer.removeErrorListeners();
            lexer.addErrorListener(errorListener);

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Criação do parser e adição do error listener
            p1.TugaParser parser = new p1.TugaParser(tokens);
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

            // Fase de verificação semântica (type checking)
            TypeCheckingVisitor typeChecker = new TypeCheckingVisitor(showTypeCheckingErrors);
            typeChecker.visit(tree);
            if (!typeChecker.isTypeCheckSuccessful()) {
                System.out.println("Input has type checking errors");
                return;
            }

            // Geração de código (bytecodes) – criação da constant pool e instruções
            CodeGenerationVisitor codeGen = new CodeGenerationVisitor();
            codeGen.visit(tree);
            byte[] bytecodes = codeGen.getBytecode();

            // Escrever os bytecodes no ficheiro "bytecodes.bc"
            FileOutputStream fos = new FileOutputStream("bytecodes.bc");
            fos.write(bytecodes);
            fos.close();

            // Ler imediatamente o ficheiro gerado
            byte[] codeFromFile = Files.readAllBytes(new File("bytecodes.bc").toPath());
            String bytecodeText = new String(codeFromFile);

            // Imprime a constant pool e as instruções (conforme o enunciado)
            System.out.println(bytecodeText);

            // Imprime um cabeçalho para o output da VM
            System.out.println("*** VM output ***");

            // Executa os bytecodes na máquina virtual
            VirtualMachineS vm = new VirtualMachineS();
            vm.execute(codeFromFile);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
