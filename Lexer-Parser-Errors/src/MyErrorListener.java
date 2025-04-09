import org.antlr.v4.runtime.*;

public class MyErrorListener extends BaseErrorListener {
    private boolean showLexerErrors;
    private boolean showParserErrors;
    private int numLexerErrors = 0;
    private int numParsingErrors = 0;

    public MyErrorListener(boolean showLexerErrors, boolean showParserErrors){
        super();
        this.showLexerErrors = showLexerErrors;
        this.showParserErrors = showParserErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e)
    {
        //System.out.println(msg);
        if (recognizer instanceof Lexer) {
            this.numLexerErrors++;
            if (this.showLexerErrors)
                System.err.printf("line %d:%d error: %s\n", line, charPositionInLine, msg);
        }
        if (recognizer instanceof Parser) {
            this.numParsingErrors++;
            if (this.showParserErrors)
                System.err.printf("line %d:%d error: %s\n", line, charPositionInLine, msg);
        }
    }

    public int getNumLexerErrors() {
        return this.numLexerErrors;
    }

    public int getNumParsingErrors() {
        return this.numParsingErrors;
    }
}

