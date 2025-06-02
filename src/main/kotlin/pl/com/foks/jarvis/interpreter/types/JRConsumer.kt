package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.exceptions.InvalidArgumentCountException
import pl.com.foks.jarvis.interpreter.Environment
import pl.com.foks.jarvis.interpreter.Interpreter
import pl.com.foks.jarvis.models.Statement

open class JRConsumer(
    protected val environment: Environment, protected val parameters: List<String>,
    protected val statements: List<Statement>
) : JRConsumable<JRConsumer> {

    override fun consume(arguments: List<JRType<*>>): JRType<*> {
        if (arguments.size != parameters.size) {
            throw InvalidArgumentCountException(parameters.size, arguments.size)
        }
        for (i in parameters.indices) {
            environment.assign(parameters[i], arguments[i])
        }
        return Interpreter(environment).interpret(statements)
    }

    override fun toString(): String {
        return "<<Consumer>>"
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

    override fun get(name: String): JRType<*> {
        return environment.get(name)
    }

    override fun env(): Environment {
        return environment
    }
}