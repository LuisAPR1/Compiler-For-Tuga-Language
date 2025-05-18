package semantic;

/**
 * Tipos primitivos da linguagem Tuga.
 *  – Para funções «vazias» (void) usamos valor <code>null</code> em vez de
 *    introduzir um novo tipo específico.
 */
public enum TugaType {
    INT,
    REAL,
    BOOLEAN,
    STRING,
    ERROR     // usado internamente pela análise semântica
}
