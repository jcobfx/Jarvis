package pl.com.foks.jarvis.interpreter

import pl.com.foks.jarvis.models.AssignmentStatement
import pl.com.foks.jarvis.models.BinaryExpression
import pl.com.foks.jarvis.models.ExpressionStatement
import pl.com.foks.jarvis.models.ExpressionVisitor
import pl.com.foks.jarvis.models.FeedExpression
import pl.com.foks.jarvis.models.ConsumerAssignmentStatement
import pl.com.foks.jarvis.models.LogicalExpression
import pl.com.foks.jarvis.models.PrimaryExpression
import pl.com.foks.jarvis.models.PrimaryType
import pl.com.foks.jarvis.models.ReturnStatement
import pl.com.foks.jarvis.models.Statement
import pl.com.foks.jarvis.models.StatementVisitor
import pl.com.foks.jarvis.models.TupleExpression
import pl.com.foks.jarvis.models.UnaryExpression
import pl.com.foks.jarvis.scanners.TokenType
import pl.com.foks.jarvis.util.Tuple

class Interpreter(private val environment: Environment = Environment(null)) : ExpressionVisitor<Any?>,
    StatementVisitor<Any?> {
    fun interpret(statements: List<Statement>): Tuple {
        statements.forEach { statement ->
            if (statement is ReturnStatement) {
                return (statement.expression?.accept(this) ?: Tuple()) as Tuple
            }
            statement.accept(this)
        }
        return Tuple()
    }

    override fun visitLogicalExpression(expression: LogicalExpression): Any? {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        return when (expression.operator) {
            TokenType.AND -> left as Boolean && right as Boolean
            TokenType.OR -> left as Boolean || right as Boolean
            else -> throw IllegalArgumentException("Unknown operator: ${expression.operator}")
        }
    }

    override fun visitBinaryExpression(expression: BinaryExpression): Any? {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        return when (expression.operator) {
            TokenType.PLUS -> (left as Double) + (right as Double)
            TokenType.MINUS -> (left as Double) - (right as Double)
            TokenType.MULTIPLY -> (left as Double) * (right as Double)
            TokenType.DIVIDE -> (left as Double) / (right as Double)
            TokenType.EQUALS_EQUALS -> left == right
            TokenType.NOT_EQUALS -> left != right
            TokenType.LESS_THAN -> (left as Double) < (right as Double)
            TokenType.LESS_THAN_EQUALS -> (left as Double) <= (right as Double)
            TokenType.GREATER_THAN -> (left as Double) > (right as Double)
            TokenType.GREATER_THAN_EQUALS -> (left as Double) >= (right as Double)
            else -> throw IllegalArgumentException("Unknown operator: ${expression.operator}")
        }
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Any? {
        val operand = expression.operand.accept(this)
        return when (expression.operator) {
            TokenType.MINUS -> -(operand as Double)
            TokenType.NOT -> !(operand as Boolean)
            else -> throw IllegalArgumentException("Unknown operator: ${expression.operator}")
        }
    }

    override fun visitTupleExpression(expression: TupleExpression): Tuple {
        val elements = expression.elements.map { it.accept(this) }
        return Tuple(elements)
    }

    override fun visitFeedExpression(expression: FeedExpression): Tuple {
        val food = expression.food.map { it.accept(this) }
        val consumer = environment.get(expression.consumer) as Consumable
        return consumer.consume(food)
    }

    override fun visitPrimaryExpression(expression: PrimaryExpression): Any? {
        return when (expression.type) {
            PrimaryType.IDENTIFIER -> environment.get(expression.value)
            PrimaryType.NUMBER -> expression.value.toDouble()
            PrimaryType.LITERAL -> expression.value
        }
    }

    override fun visitExpressionStatement(statement: ExpressionStatement): Any? {
        statement.expression.accept(this)
        return null
    }

    override fun visitConsumerAssignmentStatement(statement: ConsumerAssignmentStatement): Any? {
        val consumer = Consumer(
            Environment(environment),
            statement.parameters,
            statement.statements
        )
        environment.assign(statement.identifier, consumer)
        return null
    }

    override fun visitAssignmentStatement(statement: AssignmentStatement): Any? {
        val value = statement.expression.accept(this)
        environment.assign(statement.identifier, value)
        return null
    }

    override fun visitReturnStatement(statement: ReturnStatement): Tuple {
        val value = statement.expression?.accept(this)
        return Tuple(value)
    }
}