package pl.com.foks.jarvis.interpreter

import pl.com.foks.jarvis.exceptions.InterpreterException
import pl.com.foks.jarvis.exceptions.InvalidExpressionException
import pl.com.foks.jarvis.exceptions.UnknownOperatorException
import pl.com.foks.jarvis.interpreter.types.*
import pl.com.foks.jarvis.models.*
import pl.com.foks.jarvis.scanners.TokenType

class Interpreter(private val environment: Environment? = Environment(null)) : ExpressionVisitor<JRType<*>>,
    StatementVisitor<JRType<*>> {
    fun interpret(statements: List<Statement>): JRType<*> {
        for (statement in statements) {
            try {
                if (statement is ReturnStatement) {
                    return statement.accept(this)
                }
                statement.accept(this)
            } catch (e: InterpreterException) {
                println("Error: ${e.message}")
                println("In statement: $statement")
                continue
            }
        }
        return JRTuple()
    }

    override fun visitLogicalExpression(expression: LogicalExpression): JRBool {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        return when (expression.operator) {
            TokenType.AND -> left.and(right)
            TokenType.OR -> left.or(right)
            TokenType.XOR -> left.xor(right)
            else -> throw UnknownOperatorException(expression.operator)
        }
    }

    override fun visitBinaryExpression(expression: BinaryExpression): JRType<*> {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        return when (expression.operator) {
            TokenType.PLUS -> left.plus(right)
            TokenType.MINUS -> left.minus(right)
            TokenType.MULTIPLY -> left.times(right)
            TokenType.DIVIDE -> left.div(right)
            TokenType.EQUALS_EQUALS -> if (left == right) JRBool.TRUE else JRBool.FALSE
            TokenType.NOT_EQUALS -> if (left != right) JRBool.TRUE else JRBool.FALSE
            TokenType.LESS_THAN -> if (left < right) JRBool.TRUE else JRBool.FALSE
            TokenType.LESS_THAN_EQUALS -> if (left <= right) JRBool.TRUE else JRBool.FALSE
            TokenType.GREATER_THAN -> if (left > right) JRBool.TRUE else JRBool.FALSE
            TokenType.GREATER_THAN_EQUALS -> if (left >= right) JRBool.TRUE else JRBool.FALSE
            else -> throw UnknownOperatorException(expression.operator)
        }
    }

    override fun visitUnaryExpression(expression: UnaryExpression): JRType<*> {
        val operand = expression.operand.accept(this)
        return when (expression.operator) {
            TokenType.MINUS -> -operand
            TokenType.NOT -> !operand
            else -> throw UnknownOperatorException(expression.operator)
        }
    }

    override fun visitGetExpression(expression: GetExpression): JRType<*> {
        return when (expression.property) {
            is PrimaryExpression -> expression.namespace.accept(this).get(expression.property.value)
            is GetExpression -> expression.property.accept(
                Interpreter(expression.namespace.accept(this).environment)
            )

            else -> throw InvalidExpressionException(expression.property)
        }
    }

    override fun visitTupleExpression(expression: TupleExpression): JRTuple {
        val elements = expression.elements.map { it.accept(this) }
        return JRTuple(elements)
    }

    override fun visitFeedExpression(expression: FeedExpression): JRType<*> {
        val food = if (expression.food is TupleExpression) {
            expression.food.elements.map { it.accept(this) }
        } else {
            listOf(expression.food.accept(this))
        }
        val consumer = expression.consumer.accept(this)
        if (consumer !is JRConsumer) {
            throw InvalidExpressionException(expression.consumer)
        }
        return consumer.consume(food)
    }

    override fun visitPrimaryExpression(expression: PrimaryExpression): JRType<*> {
        return when (expression.type) {
            TokenType.IDENTIFIER -> environment?.get(expression.value) ?: JRNone.NONE
            TokenType.NUMBER -> JRNumber(expression.value.toDouble())
            TokenType.LITERAL -> JRLiteral(expression.value)
            TokenType.TRUE -> JRBool.TRUE
            TokenType.FALSE -> JRBool.FALSE
            TokenType.NONE -> JRNone.NONE
            else -> throw IllegalArgumentException("Unknown primary type: ${expression.type}")
        }
    }

    override fun visitEmptyStatement(statement: EmptyStatement): JRType<*> {
        return JRNone.NONE
    }

    override fun visitExpressionStatement(statement: ExpressionStatement): JRType<*> {
        statement.expression.accept(this)
        return JRNone.NONE
    }

    override fun visitClassAssignmentStatement(statement: ClassAssignmentStatement): JRType<*> {
        val defaults = statement.parameters.associateWith { JRNone.NONE }
        val clazz = JRClass(
            Environment(null, false, defaults),
            statement.parameters,
            statement.statements
        )
        clazz.init()
        environment?.assign(statement.identifier, clazz)
        return JRNone.NONE
    }

    override fun visitConsumerAssignmentStatement(statement: ConsumerAssignmentStatement): JRType<*> {
        val defaults = statement.parameters.associateWith { JRNone.NONE }
        val consumer = JRConsumer(
            Environment(environment, defaults = defaults),
            statement.parameters,
            statement.statements
        )
        environment?.assign(statement.identifier, consumer)
        return JRNone.NONE
    }

    override fun visitAssignmentStatement(statement: AssignmentStatement): JRType<*> {
        val value = statement.expression.accept(this)
        environment?.assign(statement.identifier, value)
        return JRNone.NONE
    }

    override fun visitReturnStatement(statement: ReturnStatement): JRType<*> {
        val value = statement.expression?.accept(this)
        if (value == null) {
            return JRNone.NONE
        }
        return value
    }
}