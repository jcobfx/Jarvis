package pl.com.foks.jarvis.interpreter

import pl.com.foks.jarvis.models.Statement
import pl.com.foks.jarvis.util.Tuple

fun interface Consumable {
    fun consume(arguments: List<Any?>): Tuple
}

class Consumer : Consumable {
    private val environment: Environment
    private val parameters: List<String>
    private val statements: List<Statement>

    constructor(environment: Environment, parameters: List<String>, statements: List<Statement>) {
        this.environment = environment
        for (parameter in parameters) {
            this.environment.assign(parameter, null)
        }
        this.parameters = parameters
        this.statements = statements
    }

    override fun consume(arguments: List<Any?>): Tuple {
        if (arguments.size != parameters.size) {
            throw IllegalArgumentException("Invalid number of arguments: expected ${parameters.size}, got ${arguments.size}")
        }
        for (i in parameters.indices) {
            environment.assign(parameters[i], arguments[i])
        }
        return Interpreter(environment).interpret(statements)
    }
}