grammar Tuga;

/* ───────────────────── Programa ─────────────────────
 *  prog  ::= decl* fdecl+ EOF
 */
prog   : decl* fdecl+ EOF ;

/* ─────────────────── Declarações globais ─────────────────── */
decl   : ID (',' ID)* ':' tipo ';' ;

/* ─────────────────── Declarações de função ────────────────── */
fdecl  : 'funcao' ID '(' paramList? ')' (':' tipo)? block ;

paramList : param (',' param)* ;
param     : ID ':' tipo ;

/* ─────────────────── Blocos com variáveis locais ─────────────────── */
block  : 'inicio' decl* stat* 'fim' ;

/* ─────────────────── Instruções ─────────────────── */
stat
    : 'escreve' expr ';'                       #Write
    | ID '<-' expr ';'                         #Assign
    | ID '(' argList? ')' ';'                  #CallStat
    | 'retorna' expr? ';'                      #Return
    | block                                    #StatBlock
    | 'enquanto' '(' expr ')' stat             #While
    | 'se' '(' expr ')' stat ('senao' stat)?   #IfElse
    | ';'                                      #Empty
    ;

/* ─────────────────── Expressões ─────────────────── */
expr
    : '(' expr ')'                               #Parens
    | op=('-'|'nao') expr                        #Uminus
    | expr op=('*'|'/'|'%') expr                 #MulDivMod
    | expr op=('+'|'-') expr                     #AddSub
    | expr op=('<'|'>'|'<='|'>=') expr           #Relational
    | expr op=('igual'|'diferente') expr         #EqDif
    | expr 'e'  expr                             #Elogic
    | expr 'ou' expr                             #ORlogic
    | ID '(' argList? ')'                        #FuncCall
    | ID                                         #Var
    | INT                                        #Int
    | REAL                                       #Real
    | STRING                                     #String
    | BOOLEAN                                    #Boolean
    ;

argList : expr (',' expr)* ;

/* ─────────────────── Tipos ─────────────────── */
tipo  : 'inteiro'
      | 'real'
      | 'booleano'
      | 'string'
      ;

/* ─────────────────── Léxico ─────────────────── */
BOOLEAN   : 'verdadeiro' | 'falso' ;
ID        : [_a-zA-Z] [_a-zA-Z0-9]* ;
INT       : DIGIT+ ;
REAL      : DIGIT* '.' DIGIT+ ;
STRING    : '"' ( ~["\\] | ESCAPE_SEQUENCE )* '"' ;

WS        : [ \t\r\n]+          -> skip ;
SL_COMMENT: '//' .*? (EOF|'\n') -> skip ;
ML_COMMENT: '/*' .*? '*/'       -> skip ;

fragment ESCAPE_SEQUENCE : '\\' . ;
fragment DIGIT           : [0-9] ;
