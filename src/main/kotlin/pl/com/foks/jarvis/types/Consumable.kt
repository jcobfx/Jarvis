package pl.com.foks.jarvis.types

import pl.com.foks.jarvis.interpreter.Environment
import pl.com.foks.jarvis.interpreter.Interpreter
import pl.com.foks.jarvis.models.Statement

fun interface Consumable {
    fun consume(arguments: List<Any?>): Any
}

open class Consumer : Consumable {
    protected val environment: Environment
    protected val parameters: List<String>
    protected val statements: List<Statement>

    constructor(environment: Environment, parameters: List<String>, statements: List<Statement>) {
        this.environment = environment
        this.parameters = parameters
        this.statements = statements
    }

    override fun consume(arguments: List<Any?>): Any {
        if (arguments.size != parameters.size) {
            throw IllegalArgumentException("Invalid number of arguments: expected ${parameters.size}, got ${arguments.size}")
        }
        for (i in parameters.indices) {
            environment.assign(parameters[i], arguments[i])
        }
        return Interpreter(environment).interpret(statements)
    }

    override fun toString(): String {
        return "Consumer(parameters=$parameters)"
    }
}