package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.exceptions.IllegalOperationException
import pl.com.foks.jarvis.exceptions.InconvertibleTypeException
import pl.com.foks.jarvis.exceptions.InvalidArgumentCountException
import pl.com.foks.jarvis.interpreter.Environment
import pl.com.foks.jarvis.interpreter.Interpreter
import pl.com.foks.jarvis.models.Statement

interface JRConsumable {
    fun consume(arguments: List<JRType<*>>): JRType<*>
}

open class JRConsumer : JRConsumable, JRType<JRConsumer> {
    protected val parameters: List<String>
    protected val statements: List<Statement>

    constructor(environment: Environment, parameters: List<String>, statements: List<Statement>) : super(environment) {
        this.parameters = parameters
        this.statements = statements
    }

    override fun consume(arguments: List<JRType<*>>): JRType<*> {
        if (arguments.size != parameters.size) {
            throw InvalidArgumentCountException(parameters.size,arguments.size)
        }
        for (i in parameters.indices) {
            environment?.assign(parameters[i], arguments[i])
        }
        return Interpreter(environment).interpret(statements)
    }

    override fun plus(other: JRType<*>): JRConsumer {
        throw IllegalOperationException("plus", this::class.simpleName ?: "JRConsumer")
    }

    override fun minus(other: JRType<*>): JRConsumer {
        throw IllegalOperationException("minus", this::class.simpleName ?: "JRConsumer")
    }

    override fun times(other: JRType<*>): JRConsumer {
        throw IllegalOperationException("times", this::class.simpleName ?: "JRConsumer")
    }

    override fun div(other: JRType<*>): JRConsumer {
        throw IllegalOperationException("div", this::class.simpleName ?: "JRConsumer")
    }

    override fun rem(other: JRType<*>): JRConsumer {
        throw IllegalOperationException("rem", this::class.simpleName ?: "JRConsumer")
    }

    override fun unaryMinus(): JRConsumer {
        throw IllegalOperationException("unaryMinus", this::class.simpleName ?: "JRConsumer")
    }

    override fun not(): JRBool {
        throw IllegalOperationException("not", this::class.simpleName ?: "JRConsumer")
    }

    override fun compareTo(other: JRType<*>): Int {
        throw IllegalOperationException("compareTo", this::class.simpleName ?: "JRConsumer")
    }

    override fun or(other: JRType<*>): JRBool {
        throw IllegalOperationException("or", this::class.simpleName ?: "JRConsumer")
    }

    override fun and(other: JRType<*>): JRBool {
        throw IllegalOperationException("and", this::class.simpleName ?: "JRConsumer")
    }

    override fun xor(other: JRType<*>): JRBool {
        throw IllegalOperationException("xor", this::class.simpleName ?: "JRConsumer")
    }

    override fun toBool(): JRBool {
        throw InconvertibleTypeException(JRConsumer::class.toString(), Boolean::class.toString())
    }

    override fun toString(): String {
        return "Consumer(parameters=$parameters)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JRConsumer

        if (environment != other.environment) return false
        if (parameters != other.parameters) return false
        if (statements != other.statements) return false

        return true
    }

    override fun hashCode(): Int {
        var result = environment.hashCode()
        result = 31 * result + parameters.hashCode()
        result = 31 * result + statements.hashCode()
        return result
    }
}