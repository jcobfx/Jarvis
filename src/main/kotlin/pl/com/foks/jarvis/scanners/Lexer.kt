package pl.com.foks.jarvis.scanners

import pl.com.foks.jarvis.exceptions.UnexpectedEndOfInputException
import pl.com.foks.jarvis.exceptions.UnknownCharacterException
import pl.com.foks.jarvis.exceptions.UnterminatedStringLiteralException

class Lexer {
    private val keywords = mapOf(
        "true" to TokenType.TRUE,
        "false" to TokenType.FALSE,
        "return" to TokenType.RETURN,
        "class" to TokenType.CLASS,
        "none" to TokenType.NONE,
        "this" to TokenType.THIS,
    )

    private var tokens = mutableListOf<Token>()
    private var chars = charArrayOf()
    private var current = 0
    private var line = 1
    private var column = 0

    fun tokenize(input: String): List<Token> {
        tokens.clear()
        chars = input.toCharArray()
        current = 0
        line = 1
        column = 0

        if (chars.isEmpty()) {
            tokens.add(Token(TokenType.EOF, line, column))
            return tokens
        }

        while (true) {
            when (consume()) {
                '=' -> {
                    if (peek() == '=') {
                        consume()
                        addToken(TokenType.EQUALS_EQUALS)
                    } else if (peek() == '>') {
                        consume()
                        addToken(TokenType.FEED)
                    }
                    else addToken(TokenType.EQUALS)
                }

                '+' -> addToken(TokenType.PLUS)
                '-' -> addToken(TokenType.MINUS)
                '*' -> addToken(TokenType.MULTIPLY)
                '/' -> addToken(TokenType.DIVIDE)

                '<' -> {
                    if (peek() == '=') {
                        consume()
                        addToken(TokenType.LESS_THAN_EQUALS)
                    } else addToken(TokenType.LESS_THAN)
                }

                '>' -> {
                    if (peek() == '=') {
                        consume()
                        addToken(TokenType.GREATER_THAN_EQUALS)
                    } else addToken(TokenType.GREATER_THAN)
                }

                '(' -> addToken(TokenType.PARENTHESES_OPEN)
                ')' -> addToken(TokenType.PARENTHESES_CLOSE)
                '{' -> addToken(TokenType.BRACES_OPEN)
                '}' -> addToken(TokenType.BRACES_CLOSE)
                '[' -> addToken(TokenType.BRACKETS_OPEN)
                ']' -> addToken(TokenType.BRACKETS_CLOSE)

                '!' -> {
                    if (peek() == '=') {
                        consume()
                        addToken(TokenType.NOT_EQUALS)
                    } else addToken(TokenType.NOT)
                }

                '&' -> addToken(TokenType.AND)
                '|' -> addToken(TokenType.OR)
                '^' -> addToken(TokenType.XOR)

                '#' -> {
                    while (current < chars.size && peek() != '\n') {
                        consume()
                    }
                }

                ',' -> addToken(TokenType.COMMA)
                '.' -> addToken(TokenType.DOT)

                '\n' -> {
                    addToken(TokenType.EOL)
                    line++
                    column = 1
                    if (current >= chars.size) {
                        addToken(TokenType.EOF)
                        break
                    }
                }

                ' ', '\t', '\r' -> {
                    // Ignore whitespace
                }

                '"' -> {
                    val start = current
                    while (current < chars.size && peek() != '"') {
                        if (peek() == '\n') {
                            throw UnterminatedStringLiteralException(line, current)
                        }
                        consume()
                    }
                    if (current >= chars.size) {
                        throw UnterminatedStringLiteralException(line, current)
                    }
                    consume() // Skip closing quote
                    val literal = String(chars, start, current - start - 1)
                    addToken(TokenType.LITERAL, literal)
                }

                in '0'..'9' -> {
                    val start = current - 1
                    while (current < chars.size && peek().isDigit()) {
                        consume()
                    }
                    val number = String(chars, start, current - start)
                    addToken(TokenType.NUMBER, number)
                }

                in 'a'..'z', in 'A'..'Z', '_' -> {
                    val start = current - 1
                    while (current < chars.size && (peek().isLetterOrDigit() || peek() == '_')) {
                        consume()
                    }
                    val identifier = String(chars, start, current - start)
                    if (keywords.containsKey(identifier)) {
                        addToken(keywords[identifier]!!)
                    } else {
                        addToken(TokenType.IDENTIFIER, identifier)
                    }
                }

                else -> {
                    throw UnknownCharacterException(chars[current - 1], line, column)
                }
            }
        }

        return tokens
    }

    private fun consume(): Char {
        if (current >= chars.size) {
            throw UnexpectedEndOfInputException(line, column)
        }
        current++
        column++
        return chars[current - 1]
    }

    private fun addToken(tokenType: TokenType) {
        if (current > chars.size) {
            throw UnexpectedEndOfInputException(line, column)
        }
        val token = Token(tokenType, line, column)
        tokens.add(token)
    }

    private fun addToken(tokenType: TokenType, value: String) {
        if (current > chars.size) {
            throw UnexpectedEndOfInputException(line, column)
        }
        val token = Token(tokenType, value, line, column)
        tokens.add(token)
    }

    private fun peek(): Char {
        return chars[current]
    }
}

enum class TokenType {
    EQUALS,
    PLUS, MINUS, MULTIPLY, DIVIDE,
    NOT, AND, OR, XOR,

    EQUALS_EQUALS, NOT_EQUALS, LESS_THAN, GREATER_THAN,
    LESS_THAN_EQUALS, GREATER_THAN_EQUALS,

    PARENTHESES_OPEN, PARENTHESES_CLOSE,
    BRACES_OPEN, BRACES_CLOSE,
    BRACKETS_OPEN, BRACKETS_CLOSE,

    IDENTIFIER, LITERAL, NUMBER,
    TRUE, FALSE,
    NONE,

    FEED,
    COMMA, DOT,

    RETURN, CLASS, THIS,

    EOL, EOF
}

class Token(val type: TokenType, val value: String, val line: Int, val column: Int) {
    override fun toString(): String {
        return "Token(type=$type, value='$value', line=$line, column=$column)"
    }

    constructor(type: TokenType, line: Int, column: Int) : this(type, type.name, line, column)
}