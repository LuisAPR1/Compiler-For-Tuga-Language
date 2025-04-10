/***
 * Excerpted from "The Definitive ANTLR 4 Reference",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/tpantlr2 for more book information.
***/
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

import LExpr.*;

public class TestLEvalVisitor {
    // a4 -visitor Expr.g4
    /** Visitor "calculator" */
    public static class EvalVisitor extends LExprBaseVisitor<Integer> {
        public Integer visitMult(LExprParser.MultContext ctx) {
            return visit(ctx.e(0)) * visit(ctx.e(1));
        }

        public Integer visitAdd(LExprParser.AddContext ctx) {
            return visit(ctx.e(0)) + visit(ctx.e(1));
        }

        public Integer visitInt(LExprParser.IntContext ctx) {
            return Integer.valueOf(ctx.INT().getText());
        }
    }

    public static void main(String[] args) throws Exception {
        boolean showLexerErrors = true;
        boolean showParserErrors = true;

        String inputFile = null;
        if ( args.length>0 ) inputFile = args[0];
        InputStream is = System.in;
        try {
            if (inputFile != null) is = new FileInputStream(inputFile);
            CharStream input = CharStreams.fromStream(is);
            /*
            LExprLexer lexer = new LExprLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LExprParser parser = new LExprParser(tokens);
            ParseTree tree = parser.s();
             */

            //
            // add my own error listener
            //
            MyErrorListener errorListener = new MyErrorListener(showLexerErrors, showParserErrors);
            LExprLexer lexer = new LExprLexer(input);
            lexer.removeErrorListeners();
            lexer.addErrorListener( errorListener );
            CommonTokenStream tokens = new CommonTokenStream( lexer );
            LExprParser parser = new LExprParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener( errorListener );
            ParseTree tree = parser.s();
            if( errorListener.getNumLexerErrors() > 0 ) {
                System.out.println("Input has lexical errors");
                return;
            }
            if( errorListener.getNumParsingErrors() > 0 ) {
                System.out.println("Input has parsing errors");
                return;
            }

            EvalVisitor evalVisitor = new EvalVisitor();
            int result = evalVisitor.visit(tree);
            System.out.println("visitor result = " + result);
        }
        catch (java.io.IOException e) {
            System.out.println(e);
        }
    }
}
