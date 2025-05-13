package pl.com.foks.jarvis.interpreter

import pl.com.foks.jarvis.exceptions.InterpreterException
import pl.com.foks.jarvis.exceptions.UnknownOperatorException
import pl.com.foks.jarvis.models.AssignmentStatement
import pl.com.foks.jarvis.models.BinaryExpression
import pl.com.foks.jarvis.models.ClassAssignmentStatement
import pl.com.foks.jarvis.models.ExpressionStatement
import pl.com.foks.jarvis.models.ExpressionVisitor
import pl.com.foks.jarvis.models.FeedExpression
import pl.com.foks.jarvis.models.ConsumerAssignmentStatement
import pl.com.foks.jarvis.models.EmptyStatement
import pl.com.foks.jarvis.models.GetExpression
import pl.com.foks.jarvis.models.LogicalExpression
import pl.com.foks.jarvis.models.PrimaryExpression
import pl.com.foks.jarvis.models.PrimaryType
import pl.com.foks.jarvis.models.ReturnStatement
import pl.com.foks.jarvis.models.Statement
import pl.com.foks.jarvis.models.StatementVisitor
import pl.com.foks.jarvis.models.TupleExpression
import pl.com.foks.jarvis.models.UnaryExpression
import pl.com.foks.jarvis.scanners.TokenType
import pl.com.foks.jarvis.types.Class
import pl.com.foks.jarvis.types.Consumable
import pl.com.foks.jarvis.types.Consumer
import pl.com.foks.jarvis.types.Tuple

class Interpreter(private val environment: Environment = Environment(null)) : ExpressionVisitor<Any?>,
    StatementVisitor<Any?> {
    fun interpret(statements: List<Statement>): Tuple {
        for (statement in statements) {
            try {
                if (statement is ReturnStatement) {
                    return statement.accept(this) as Tuple
                }
                statement.accept(this)
            } catch (e: InterpreterException) {
                println("Error: ${e.message}")
                println("In statement: $statement")
                continue
            }
        }
        return Tuple()
    }

    override fun visitLogicalExpression(expression: LogicalExpression): Any? {
        val left = expression.left.accept(this)
        val right = expression.right.accept(this)
        return when (expression.operator) {
            TokenType.AND -> left as Boolean && right as Boolean
            TokenType.OR -> left as Boolean || right as Boolean
            else -> throw UnknownOperatorException(expression.operator)
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
            else -> throw UnknownOperatorException(expression.operator)
        }
    }

    override fun visitUnaryExpression(expression: UnaryExpression): Any? {
        val operand = expression.operand.accept(this)
        return when (expression.operator) {
            TokenType.MINUS -> -(operand as Double)
            TokenType.NOT -> !(operand as Boolean)
            else -> throw UnknownOperatorException(expression.operator)
        }
    }

    override fun visitTupleExpression(expression: TupleExpression): Tuple {
        val elements = expression.elements.map { it.accept(this) }
        return Tuple(elements)
    }

    override fun visitFeedExpression(expression: FeedExpression): Any {
        val food = expression.food.map { it.accept(this) }
        val consumer = expression.consumer.accept(this) as Consumable
        return consumer.consume(food)
    }

    override fun visitGetExpression(expression: GetExpression): Any? {
        return (environment.get(expression.classIdentifier) as Class).get(expression.property)
    }

    override fun visitPrimaryExpression(expression: PrimaryExpression): Any? {
        return when (expression.type) {
            PrimaryType.IDENTIFIER -> environment.get(expression.value)
            PrimaryType.NUMBER -> expression.value.toDouble()
            PrimaryType.LITERAL -> expression.value
        }
    }

    override fun visitEmptyStatement(statement: EmptyStatement): Any? {
        return null
    }

    override fun visitExpressionStatement(statement: ExpressionStatement): Any? {
        statement.expression.accept(this)
        return null
    }

    override fun visitClassAssignmentStatement(statement: ClassAssignmentStatement): Any? {
        val defaults = statement.parameters.associateWith { null }
        val classInstance = Class(
            Environment(null, false, defaults),
            statement.parameters,
            statement.statements
        )
        classInstance.init()
        environment.assign(statement.identifier, classInstance)
        return null
    }

    override fun visitConsumerAssignmentStatement(statement: ConsumerAssignmentStatement): Any? {
        val defaults = statement.parameters.associateWith { null }
        val consumer = Consumer(
            Environment(environment, defaults = defaults),
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
        if (value is Tuple) {
            return value
        } else if (value == null) {
            return Tuple()
        }
        return Tuple(value)
    }
}