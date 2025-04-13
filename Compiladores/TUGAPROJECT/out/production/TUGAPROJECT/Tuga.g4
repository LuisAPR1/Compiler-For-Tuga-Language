grammar Tuga;

// The entry point
program : (instruction)+ EOF;

// High-level instruction
instruction : ESCREVE expression ';' ;

// Expression handling different precedence levels
expression     : orExpr ;

orExpr         : andExpr ('ou' andExpr)* ;
andExpr        : equalityExpr ('e' equalityExpr)* ;
equalityExpr   : relationalExpr (('igual' | 'diferente') relationalExpr)* ;
relationalExpr : addSub (('<' | '>' | '<=' | '>=') addSub)* ;
addSub         : mulDivMod (('+' | '-') mulDivMod)* ;
mulDivMod      : unary (('*' | '/' | '%') unary)* ;

unary          : ('nao' | '-') unary
               | primary ;

primary        : literal
               | '(' expression ')' ;

// Terminal and lexer rules
literal
    : INT            # IntLiteral
    | REAL           # RealLiteral
    | STRING         # StringLiteral
    | 'verdadeiro'   # TrueLiteral
    | 'falso'        # FalseLiteral
    ;

ESCREVE : 'escreve' ;
INT     : [0-9]+ ;
REAL    : [0-9]+ '.' [0-9]+ ;
STRING  : '"' ( ~["\\] | '\\' . )* '"' ;
WS      : [ \t\r\n]+ -> skip ;
SL_COMMENT : '//' ~[\r\n]* -> skip ;
ML_COMMENT : '/*' .*? '*/' -> skip ;
