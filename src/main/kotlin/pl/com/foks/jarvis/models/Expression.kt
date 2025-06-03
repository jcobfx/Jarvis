package pl.com.foks.jarvis.models

import pl.com.foks.jarvis.scanners.TokenType

abstract class Expression {
    abstract fun <T> accept(visitor: ExpressionVisitor<T>): T
}

class QuestionExpression(val condition: Expression, val trueBranch: Expression, val falseBranch: Expression?) :
    Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitQuestionExpression(this)
    }

    override fun toString(): String {
        return "$condition ? $trueBranch ${falseBranch?.let { ": $it" } ?: ""}"
    }
}

class LogicalExpression(val left: Expression, val operator: TokenType, val right: Expression) : Expression() {

    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitLogicalExpression(this)
    }
    override fun toString(): String {
        return "$left $operator $right"
    }
}

class BinaryExpression(val left: Expression, val operator: TokenType, val right: Expression) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitBinaryExpression(this)
    }

    override fun toString(): String {
        return "$left $operator $right"
    }
}

class UnaryExpression(val operator: TokenType, val operand: Expression) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitUnaryExpression(this)
    }

    override fun toString(): String {
        return "$operator$operand"
    }
}

class GetExpression(val namespace: Expression, val property: Expression) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitGetExpression(this)
    }

    override fun toString(): String {
        return "$namespace.$property"
    }
}

class TupleExpression(val elements: List<Expression>) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitTupleExpression(this)
    }

    override fun toString(): String {
        return "(${elements.joinToString(", ")})"
    }
}

class FeedExpression(val food: Expression, val consumer: Expression) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitFeedExpression(this)
    }

    override fun toString(): String {
        return "$food => $consumer"
    }
}

class PrimaryExpression(val value: String, val type: TokenType) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitPrimaryExpression(this)
    }

    override fun toString(): String {
        return when (type) {
            TokenType.LITERAL -> "\"$value\""
            TokenType.NUMBER -> value.toDouble().toString()
            else -> value
        }
    }
}

interface ExpressionVisitor<T> {
    fun visitQuestionExpression(expression: QuestionExpression): T
    fun visitLogicalExpression(expression: LogicalExpression): T
    fun visitBinaryExpression(expression: BinaryExpression): T
    fun visitUnaryExpression(expression: UnaryExpression): T
    fun visitGetExpression(expression: GetExpression): T
    fun visitTupleExpression(expression: TupleExpression): T
    fun visitFeedExpression(expression: FeedExpression): T
    fun visitPrimaryExpression(expression: PrimaryExpression): T
}