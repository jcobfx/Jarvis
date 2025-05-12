package pl.com.foks.jarvis.exceptions

import java.lang.RuntimeException

class UnterminatedStringLiteralException(): RuntimeException("Unterminated string literal") {
    constructor(line: Int, column: Int): this() {
        this.line = line
        this.column = column
    }

    var line: Int = -1
    var column: Int = -1

    override fun toString(): String {
        return "Unterminated string literal at line $line, column $column"
    }
}

class UnexpectedEndOfInputException(): RuntimeException("Unexpected end of input") {
    constructor(line: Int, column: Int): this() {
        this.line = line
        this.column = column
    }

    var line: Int = -1
    var column: Int = -1

    override fun toString(): String {
        return "Unexpected end of input at line $line, column $column"
    }
}

class UnknownCharacterException(
    val character: Char,
    val line: Int,
    val column: Int
) : RuntimeException("Unknown character '$character' at line $line, column $column") {
    override fun toString(): String {
        return "Unknown character '$character' at line $line, column $column"
    }
}