import org.antlr.v4.runtime.*;

/**
 * Ouvinte de erros para o lexer e parser do ANTLR.
 *  – Conta o nº de erros léxicos e de parsing.
 *  – Se as flags <code>showLexerErrors</code> / <code>showParserErrors</code>
 *    estiverem a <code>true</code>, imprime a mensagem detalhada.
 *  – O TugaCompileAndRun usa estes contadores para decidir se deve
 *    terminar com “Input tem erros lexicais” ou “Input tem erros de parsing”.
 */
public class MyErrorListener extends BaseErrorListener {

    private final boolean showLexerErrors;
    private final boolean showParserErrors;
    private int numLexerErrors   = 0;
    private int numParsingErrors = 0;

    public MyErrorListener(boolean showLexerErrors, boolean showParserErrors) {
        this.showLexerErrors  = showLexerErrors;
        this.showParserErrors = showParserErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e) {

        /* ---- Erro léxico ---- */
        if (recognizer instanceof Lexer) {
            numLexerErrors++;
            if (showLexerErrors)
                System.err.printf("line %d:%d error: %s%n", line, charPositionInLine, msg);
        }

        /* ---- Erro de parsing ---- */
        if (recognizer instanceof Parser) {
            numParsingErrors++;
            if (showParserErrors)
                System.err.printf("line %d:%d error: %s%n", line, charPositionInLine, msg);
        }
    }

    /* ---------- getters ---------- */
    public int getNumLexerErrors()   { return numLexerErrors;   }
    public int getNumParsingErrors() { return numParsingErrors; }
}
