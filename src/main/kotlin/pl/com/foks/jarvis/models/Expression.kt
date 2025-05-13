package pl.com.foks.jarvis.models

import pl.com.foks.jarvis.scanners.TokenType

abstract class Expression {
    abstract fun <T> accept(visitor: ExpressionVisitor<T>): T
}

class LogicalExpression(val left: Expression, val operator: TokenType, val right: Expression) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitLogicalExpression(this)
    }

    override fun toString(): String {
        return "LogicalExpression(left=$left, operator='$operator', right=$right)"
    }
}

class BinaryExpression(val left: Expression, val operator: TokenType, val right: Expression) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitBinaryExpression(this)
    }

    override fun toString(): String {
        return "BinaryExpression(left=$left, operator='$operator', right=$right)"
    }
}

class UnaryExpression(val operator: TokenType, val operand: Expression) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitUnaryExpression(this)
    }

    override fun toString(): String {
        return "UnaryExpression(operator='$operator', operand=$operand)"
    }
}

class TupleExpression(val elements: List<Expression>) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitTupleExpression(this)
    }

    override fun toString(): String {
        return "TupleExpression(elements=$elements)"
    }
}

class FeedExpression(val food: List<Expression>, val consumer: Expression) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitFeedExpression(this)
    }

    override fun toString(): String {
        return "FeedExpression(food=$food, consumer='$consumer')"
    }
}

class GetExpression(val classIdentifier: String, val property: String) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitGetExpression(this)
    }

    override fun toString(): String {
        return "GetExpression(classIdentifier='$classIdentifier', property='$property')"
    }
}

class PrimaryExpression(val value: String, val type: PrimaryType) : Expression() {
    override fun <T> accept(visitor: ExpressionVisitor<T>): T {
        return visitor.visitPrimaryExpression(this)
    }

    override fun toString(): String {
        return "PrimaryExpression(value='$value', type=$type)"
    }
}

interface ExpressionVisitor<T> {
    fun visitLogicalExpression(expression: LogicalExpression): T
    fun visitBinaryExpression(expression: BinaryExpression): T
    fun visitUnaryExpression(expression: UnaryExpression): T
    fun visitTupleExpression(expression: TupleExpression): T
    fun visitFeedExpression(expression: FeedExpression): T
    fun visitGetExpression(expression: GetExpression): T
    fun visitPrimaryExpression(expression: PrimaryExpression): T
}

enum class PrimaryType {
    IDENTIFIER,
    LITERAL,
    NUMBER
}