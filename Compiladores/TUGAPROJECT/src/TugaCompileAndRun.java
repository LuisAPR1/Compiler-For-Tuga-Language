import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import Tuga.TugaLexer;
import Tuga.TugaParser;
import semantic.TypeCheckingVisitor;
import codegen.CodeGenerationVisitor;
import vm.VirtualMachineS;

import java.io.*;
import java.nio.file.Files;

public class TugaCompileAndRun {

    public static void main(String[] args) {
        boolean showLexerErrors = false;
        boolean showParserErrors = false;
        boolean showTypeCheckingErrors = false;

        // Leitura do código de entrada
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    break;
                }
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Erro na leitura da entrada: " + e.getMessage());
            return;
        }

        try {
            CharStream input = CharStreams.fromString(sb.toString());
            TugaLexer lexer = new TugaLexer(input);

            // Configuração do listener de erros
            MyErrorListener errorListener = new MyErrorListener(showLexerErrors, showParserErrors);
            lexer.removeErrorListeners();
            lexer.addErrorListener(errorListener);

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            TugaParser parser = new TugaParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            ParseTree tree = parser.program();

            // Checa erros léxicos e sintáticos
            if (errorListener.getNumLexerErrors() > 0) {
                System.out.println("Input possui erros léxicos");
                return;
            }
            if (errorListener.getNumParsingErrors() > 0) {
                System.out.println("Input possui erros de parsing");
                return;
            }

            // Checagem de tipos
            TypeCheckingVisitor typeChecker = new TypeCheckingVisitor(showTypeCheckingErrors);
            typeChecker.visit(tree);
            if (!typeChecker.isTypeCheckSuccessful()) {
                System.out.println("Input possui erros de type checking");
                return;
            }

            // Geração do bytecode
            CodeGenerationVisitor codeGen = new CodeGenerationVisitor();
            codeGen.visit(tree);
            byte[] bytecodes = codeGen.getBytecode();

            // Escrita do bytecode no arquivo
            try (FileOutputStream fos = new FileOutputStream("bytecodes.bc")) {
                fos.write(bytecodes);
            }

            // Leitura e impressão do bytecode
            byte[] codeFromFile = Files.readAllBytes(new File("bytecodes.bc").toPath());
            String bytecodeText = new String(codeFromFile);
            System.out.print(bytecodeText);

            System.out.println("*** VM output ***");

            // Execução na máquina virtual
            VirtualMachineS vm = new VirtualMachineS();
            vm.execute(codeFromFile);

        } catch (IOException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
}
