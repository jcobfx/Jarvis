package pl.com.foks.jarvis.types

import pl.com.foks.jarvis.interpreter.Environment
import pl.com.foks.jarvis.interpreter.Interpreter
import pl.com.foks.jarvis.models.Statement

class Class(environment: Environment, parameters: List<String>, statements: List<Statement>) :
    Consumer(environment, parameters, statements) {

    internal fun init() {
        val wasMutable = this.environment.isMutable()
        this.environment.setMutable(true)
        Interpreter(this.environment).interpret(statements)
        if (wasMutable.not()) this.environment.setMutable(false)
    }

    fun get(name: String): Any? {
        return environment.get(name)
    }

    override fun consume(arguments: List<Any?>): Any {
        val clazz = Class(this.environment.copy(true),
            this.parameters, this.statements)
        for (i in parameters.indices) {
            clazz.environment.assign(parameters[i], arguments[i])
        }
        clazz.init()
        return clazz
    }

    override fun toString(): String {
        return "Class(parameters=$parameters)"
    }
}