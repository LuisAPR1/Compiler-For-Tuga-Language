grammar Tuga;

program
    : instruction+ EOF
    ;

instruction
    : ESCREVE expression ';'
    ;

expression
    : <assoc=right> '-' expression                        # Negate
    | <assoc=right> 'nao' expression                      # Not
    | expression ('*' | '/' | '%') expression             # MulDivMod
    | expression ('+' | '-') expression                   # AddSub
    | expression ('<' | '>' | '<=' | '>=') expression      # Relational
    | expression ('igual' | 'diferente') expression        # Equality
    | expression 'e' expression                           # LogicalAnd
    | expression 'ou' expression                          # LogicalOr
    | '(' expression ')'                                  # Parens
    | literal                                             # LiteralExpr
    ;

literal
    : INT             # IntLiteral
    | REAL            # RealLiteral
    | STRING          # StringLiteral
    | 'verdadeiro'    # TrueLiteral
    | 'falso'         # FalseLiteral
    ;

// Tokens
ESCREVE : 'escreve';
INT     : [0-9]+;
REAL    : [0-9]+ '.' [0-9]+;
STRING  : '"' ( ~["\\] | '\\' . )* '"';
WS      : [ \t\r\n]+ -> skip;
SL_COMMENT : '//' ~[\r\n]* -> skip;
ML_COMMENT : '/*' .*? '*/' -> skip;
