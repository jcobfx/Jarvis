package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.exceptions.InvalidArgumentCountException
import pl.com.foks.jarvis.interpreter.Environment
import pl.com.foks.jarvis.interpreter.Interpreter
import pl.com.foks.jarvis.models.Statement

class JRClass(environment: Environment, parameters: List<String>, statements: List<Statement>) :
    JRConsumer(environment, parameters, statements) {

    internal fun init() {
        val wasMutable = this.environment.isMutable()
        this.environment.setMutable(true)
        Interpreter(this.environment).interpret(statements)
        if (wasMutable.not()) this.environment.setMutable(false)
    }

    override fun consume(arguments: List<JRType<*>>): JRType<*> {
        if (arguments.size != parameters.size) {
            throw InvalidArgumentCountException(parameters.size,arguments.size)
        }
        val clazz = JRClass(Environment(this.environment.getParent()),
            this.parameters, this.statements)
        for (i in parameters.indices) {
            clazz.environment.assign(parameters[i], arguments[i])
        }
        clazz.init()
        return clazz
    }

    override fun toString(): String {
        return "<<Class>>"
    }
}