package pl.com.foks.jarvis.scanners

import pl.com.foks.jarvis.exceptions.UnexpectedEndOfInputException
import pl.com.foks.jarvis.exceptions.UnexpectedTokenException
import pl.com.foks.jarvis.models.AssignmentStatement
import pl.com.foks.jarvis.models.BinaryExpression
import pl.com.foks.jarvis.models.ClassAssignmentStatement
import pl.com.foks.jarvis.models.Expression
import pl.com.foks.jarvis.models.ExpressionStatement
import pl.com.foks.jarvis.models.FeedExpression
import pl.com.foks.jarvis.models.ConsumerAssignmentStatement
import pl.com.foks.jarvis.models.EmptyStatement
import pl.com.foks.jarvis.models.GetExpression
import pl.com.foks.jarvis.models.LogicalExpression
import pl.com.foks.jarvis.models.PrimaryExpression
import pl.com.foks.jarvis.models.PrimaryType
import pl.com.foks.jarvis.models.ReturnStatement
import pl.com.foks.jarvis.models.Statement
import pl.com.foks.jarvis.models.TupleExpression
import pl.com.foks.jarvis.models.UnaryExpression

class Parser {
    private var tokens = mutableListOf<Token>()
    private var current = 0

    fun parse(tokens: List<Token>): List<Statement> {
        this.tokens = tokens.toMutableList()
        current = 0
        return parseProgram()
    }

    private fun parseProgram(): List<Statement> {
        val statements = mutableListOf<Statement>()
        while (isAtEnd().not()) {
            statements.add(parseStatement())
        }
        statements.removeIf { it is EmptyStatement }
        return statements
    }

    private fun parseStatement(): Statement {
        val token = consume()
        val statement = when (token.type) {
            TokenType.EOF -> throw UnexpectedEndOfInputException()
            TokenType.EOL -> return EmptyStatement()
            TokenType.IDENTIFIER -> {
                if (peek().type == TokenType.EQUALS) {
                    consume()
                    parseAssignmentStatement(token)
                } else if (check(TokenType.FEED)) {
                    consume()
                    parseConsumerAssignmentStatement(token)
                } else {
                    parseExpressionStatement(token)
                }
            }

            TokenType.NUMBER, TokenType.LITERAL, TokenType.PARENTHESES_OPEN -> parseExpressionStatement(token)
            else -> throw UnexpectedTokenException(
                arrayOf(
                    TokenType.NUMBER,
                    TokenType.IDENTIFIER,
                    TokenType.LITERAL
                ),
                token
            )
        }
        consumeExpected(TokenType.EOL, TokenType.EOF)
        return statement
    }

    private fun parseAssignmentStatement(token: Token): Statement {
        val expression = parseLogicalExpression(consume())
        return AssignmentStatement(token.value, expression)
    }

    private fun parseConsumerAssignmentStatement(token: Token): Statement {
        val parameters = mutableListOf<String>()
        consumeExpected(TokenType.PARENTHESES_OPEN)
        while (check(TokenType.IDENTIFIER)) {
            parameters.add((parsePrimaryExpression(consume()) as PrimaryExpression).value)
            if (check(TokenType.COMMA)) {
                consume()
            }
        }
        consumeExpected(TokenType.PARENTHESES_CLOSE)
        consumeExpected(TokenType.EOL)
        val body = mutableListOf<Statement>()
        while (!check(TokenType.RETURN)) {
            body.add(parseStatement())
        }
        consumeExpected(TokenType.RETURN)
        if (check(TokenType.CLASS)) {
            consume()
            return ClassAssignmentStatement(token.value, parameters, body)
        } else if (check(TokenType.EOL, TokenType.EOF)) {
            body.add(parseReturnStatement(null))
        } else {
            body.add(parseReturnStatement(consume()))
        }
        return ConsumerAssignmentStatement(token.value, parameters, body)
    }

    private fun isAtEnd(): Boolean {
        if (current >= tokens.size) {
            throw UnexpectedEndOfInputException()
        }
        return tokens[current].type == TokenType.EOF
    }

    private fun peek(): Token {
        if (isAtEnd()) {
            throw UnexpectedEndOfInputException()
        }
        return tokens[current]
    }

    private fun consume(): Token {
        return tokens[current++]
    }

    private fun consumeExpected(vararg tokenTypes: TokenType): Token {
        if (isAtEnd()) {
            throw UnexpectedEndOfInputException()
        }
        val token = tokens[current]
        if (tokenTypes.none { it == token.type }) {
            throw UnexpectedTokenException(
                tokenTypes,
                token
            )
        }
        return consume()
    }

    private fun check(vararg tokenTypes: TokenType): Boolean {
        if (isAtEnd()) {
            return false
        }
        return tokenTypes.any { it == tokens[current].type }
    }

    private fun checkPrevious(vararg tokenTypes: TokenType): Boolean {
        if (isAtEnd()) {
            return false
        }
        return tokenTypes.any { it == tokens[current - 1].type }
    }

    private fun parseReturnStatement(token: Token?): Statement {
        val expression =
            if (token == null) null
            else parseLogicalExpression(token)
        return ReturnStatement(expression)
    }

    private fun parseExpressionStatement(token: Token): Statement {
        val expression = parseLogicalExpression(token)
        return ExpressionStatement(expression)
    }

    private fun parseLogicalExpression(token: Token): Expression {
        val left = parseBinaryExpression(token)
        if (check(TokenType.AND, TokenType.OR)) {
            val operator = consume()
            val right = parseLogicalExpression(consume())
            return LogicalExpression(left, operator.type, right)
        }
        return left
    }

    private fun parseBinaryExpression(token: Token): Expression {
        val left = parseUnaryExpression(token)
        if (check(
                TokenType.PLUS, TokenType.MINUS, TokenType.MULTIPLY, TokenType.DIVIDE,
                TokenType.EQUALS_EQUALS, TokenType.NOT_EQUALS,
                TokenType.LESS_THAN, TokenType.GREATER_THAN,
                TokenType.LESS_THAN_EQUALS, TokenType.GREATER_THAN_EQUALS
            )
        ) {
            val operator = consume()
            val right = parseBinaryExpression(consume())
            return BinaryExpression(left, operator.type, right)
        }
        return left
    }

    private fun parseUnaryExpression(token: Token): Expression {
        if (checkPrevious(TokenType.NOT, TokenType.MINUS)) {
            val operator = token
            val operand = parseUnaryExpression(consume())
            return UnaryExpression(operator.type, operand)
        }
        return parseTupleExpression(token)
    }

    private fun parseTupleExpression(token: Token): Expression {
        if (checkPrevious(TokenType.PARENTHESES_OPEN)) {
            val elements = mutableListOf<Expression>()
            while (check(TokenType.NUMBER, TokenType.IDENTIFIER, TokenType.LITERAL, TokenType.PARENTHESES_OPEN)) {
                elements.add(parseLogicalExpression(consume()))
                if (check(TokenType.COMMA)) {
                    consume()
                }
            }
            consumeExpected(TokenType.PARENTHESES_CLOSE)
            if (check(TokenType.FEED)) {
                consume()
                val consumer = parseGetExpression(consumeExpected(TokenType.IDENTIFIER))
                return FeedExpression(elements, consumer)
            }
            return TupleExpression(elements)
        }
        return parseGetExpression(token)
    }

    private fun parseGetExpression(token: Token): Expression {
        if (token.type == TokenType.IDENTIFIER && check(TokenType.DOT)) {
            consume()
            val property = consumeExpected(TokenType.IDENTIFIER)
            return GetExpression(token.value, property.value)
        }
        return parsePrimaryExpression(token)
    }

    private fun parsePrimaryExpression(token: Token): Expression {
        return when (token.type) {
            TokenType.NUMBER, TokenType.IDENTIFIER, TokenType.LITERAL -> {
                val value = token.value
                PrimaryExpression(value, fromTokenTypeToPrimaryType(token.type))
            }

            else -> throw UnexpectedTokenException(
                arrayOf(
                    TokenType.NUMBER,
                    TokenType.IDENTIFIER,
                    TokenType.LITERAL
                ),
                token
            )
        }
    }

    private fun fromTokenTypeToPrimaryType(tokenType: TokenType): PrimaryType {
        return when (tokenType) {
            TokenType.IDENTIFIER -> PrimaryType.IDENTIFIER
            TokenType.LITERAL -> PrimaryType.LITERAL
            TokenType.NUMBER -> PrimaryType.NUMBER
            else -> throw IllegalArgumentException("Invalid token type for primary expression: $tokenType")
        }
    }
}