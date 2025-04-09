grammar Tuga;

program : (instruction)+ EOF;

instruction : ESCREVE expression ';' ;

expression
    : expression op=('*' | '/' | '%') expression     # MulDivMod
    | expression op=('+' | '-') expression           # AddSub
    | expression op=('<' | '>' | '<=' | '>=') expression # Relational
    | expression op=('igual' | 'diferente') expression # Equality
    | expression op=('e' | 'ou') expression            # Logical
    | '-' expression                                   # Negate
    | 'nao' expression                                 # Not
    | '(' expression ')'                               # Parens
    | literal                                          # LiteralExpr
    ;

literal
    : INT     # IntLiteral
    | REAL    # RealLiteral
    | STRING  # StringLiteral
    | 'verdadeiro'  # TrueLiteral
    | 'falso'       # FalseLiteral
    ;

ESCREVE : 'escreve' ;
INT     : [0-9]+ ;
REAL    : [0-9]+ '.' [0-9]+ ;
STRING  : '"' ( ~["\\] | '\\' . )* '"' ;
WS      : [ \t\r\n]+ -> skip ;
SL_COMMENT : '//' ~[\r\n]* -> skip ;
ML_COMMENT : '/*' .*? '*/' -> skip ;
