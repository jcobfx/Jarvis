package pl.com.foks.jarvis.scanners

import pl.com.foks.jarvis.exceptions.UnexpectedEndOfInputException
import pl.com.foks.jarvis.exceptions.UnexpectedTokenException
import pl.com.foks.jarvis.models.*

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
            TokenType.IDENTIFIER, TokenType.THIS -> {
                var token = token
                var internal = false
                if (token.type == TokenType.THIS) {
                    consumeExpected(TokenType.DOT)
                    token = consumeExpected(TokenType.IDENTIFIER)
                    internal = true
                }

                if (peek().type == TokenType.EQUALS) {
                    consume()
                    parseAssignmentStatement(token, internal)
                } else if (check(TokenType.FEED)) {
                    consume()
                    parseConsumerAssignmentStatement(token, internal)
                } else {
                    parseExpressionStatement(token)
                }
            }

            TokenType.NUMBER, TokenType.LITERAL, TokenType.TRUE, TokenType.FALSE,
            TokenType.PARENTHESES_OPEN -> parseExpressionStatement(token)

            else -> throw UnexpectedTokenException(
                arrayOf(
                    TokenType.NUMBER,
                    TokenType.IDENTIFIER,
                    TokenType.LITERAL,
                    TokenType.TRUE,
                    TokenType.FALSE,
                    TokenType.PARENTHESES_OPEN,
                    TokenType.EOL,
                    TokenType.THIS,
                ),
                token
            )
        }
        consumeExpected(TokenType.EOL, TokenType.EOF)
        return statement
    }

    private fun parseAssignmentStatement(token: Token, internal: Boolean): Statement {
        val expression = parseQuestionExpression(consume())
        return AssignmentStatement(token.value, expression, internal)
    }

    private fun parseConsumerAssignmentStatement(token: Token, internal: Boolean): Statement {
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
            return ClassAssignmentStatement(token.value, parameters, body, internal)
        } else if (check(TokenType.EOL, TokenType.EOF)) {
            body.add(parseReturnStatement(null))
        } else {
            body.add(parseReturnStatement(consume()))
        }
        return ConsumerAssignmentStatement(token.value, parameters, body, internal)
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
        val expression = parseQuestionExpression(token)
        return ExpressionStatement(expression)
    }

    private fun parseQuestionExpression(token: Token): Expression {
        val condition = parseLogicalExpression(token)
        if (check(TokenType.QUESTION)) {
            consume()
            val trueBranch = parseQuestionExpression(consume())
            var falseBranch: Expression? = null
            if (check(TokenType.COLON)) {
                consume()
                falseBranch = parseQuestionExpression(consume())
            }
            return QuestionExpression(condition, trueBranch, falseBranch)
        }
        return condition
    }

    private fun parseLogicalExpression(token: Token): Expression {
        val left = parseBinaryExpression(token)
        if (check(TokenType.AND, TokenType.OR, TokenType.XOR)) {
            val operator = consume()
            val right = parseLogicalExpression(consume())
            return LogicalExpression(left, operator.type, right)
        }
        return left
    }

    private fun parseBinaryExpression(token: Token): Expression {
        val left = parseUnaryExpression(token)
        if (check(
                TokenType.PLUS, TokenType.MINUS, TokenType.MULTIPLY,
                TokenType.DIVIDE, TokenType.REMAINDER,
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
        return parseGetExpression(token)
    }

    private fun parseGetExpression(token: Token): Expression {
        val expression = parseTupleExpression(token)
        if (check(TokenType.DOT)) {
            consume()
            return GetExpression(expression, parseGetExpression(consume()))
        } else if (check(TokenType.FEED)) {
            consume()
            val consumer = parseGetExpression(consume())
            return FeedExpression(expression, consumer)
        }
        return expression
    }

    private fun parseTupleExpression(token: Token): Expression {
        if (checkPrevious(TokenType.PARENTHESES_OPEN)) {
            val elements = mutableListOf<Expression>()
            while (check(
                    TokenType.NUMBER, TokenType.IDENTIFIER, TokenType.LITERAL,
                    TokenType.TRUE, TokenType.FALSE, TokenType.PARENTHESES_OPEN
                )
            ) {
                elements.add(parseQuestionExpression(consume()))
                if (check(TokenType.COMMA)) {
                    consume()
                }
            }
            consumeExpected(TokenType.PARENTHESES_CLOSE)
            return TupleExpression(elements)
        }
        return parsePrimaryExpression(token)
    }

    private fun parsePrimaryExpression(token: Token): Expression {
        return when (token.type) {
            TokenType.NUMBER, TokenType.IDENTIFIER, TokenType.LITERAL, TokenType.TRUE, TokenType.FALSE,
            TokenType.NONE -> {
                val value = token.value
                PrimaryExpression(value, token.type)
            }

            else -> throw UnexpectedTokenException(
                arrayOf(
                    TokenType.NUMBER,
                    TokenType.IDENTIFIER,
                    TokenType.LITERAL,
                    TokenType.TRUE,
                    TokenType.FALSE,
                    TokenType.NONE,
                ),
                token
            )
        }
    }
}