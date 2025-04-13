import org.antlr.v4.runtime.*;

public class MyErrorListener extends BaseErrorListener {

    private final boolean showLexerErrors;
    private final boolean showParserErrors;
    private int numLexerErrors = 0;
    private int numParsingErrors = 0;

    public MyErrorListener(boolean showLexerErrors, boolean showParserErrors) {
        this.showLexerErrors = showLexerErrors;
        this.showParserErrors = showParserErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        if (recognizer instanceof Lexer) {
            numLexerErrors++;
            if (showLexerErrors) {
                System.err.printf("Linha %d:%d erro: %s%n", line, charPositionInLine, msg);
            }
        } else if (recognizer instanceof Parser) {
            numParsingErrors++;
            if (showParserErrors) {
                System.err.printf("Linha %d:%d erro: %s%n", line, charPositionInLine, msg);
            }
        }
    }

    public int getNumLexerErrors() {
        return numLexerErrors;
    }

    public int getNumParsingErrors() {
        return numParsingErrors;
    }
}
