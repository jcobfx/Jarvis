package pl.com.foks.jarvis.exceptions

import pl.com.foks.jarvis.scanners.Token
import pl.com.foks.jarvis.scanners.TokenType

class UnexpectedTokenException(
    expected: Array<out TokenType>,
    actual: TokenType,
    line: Int,
    column: Int
) : Exception(
    "Unexpected token at line $line, column $column: expected " +
            expected.map { it.toString() }.reduce { t1, t2 -> "$t1 $t2" } +
            " but found $actual"
) {
    constructor(expected: Array<out TokenType>, actual: Token) : this(expected, actual.type, actual.line, actual.column)
}