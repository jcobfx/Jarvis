package pl.com.foks.jarvis.interpreter

import pl.com.foks.jarvis.config.GlobalEnvironmentConfiguration
import pl.com.foks.jarvis.exceptions.AssignmentException
import pl.com.foks.jarvis.exceptions.VariableNotFoundException
import pl.com.foks.jarvis.interpreter.types.JRNone
import pl.com.foks.jarvis.interpreter.types.JRType

class Environment {
    private val parent: Environment?
    private var mutable: Boolean
    private val variables: MutableMap<String, JRType<*>> = mutableMapOf()

    constructor(parent: Environment?, mutable: Boolean = true, defaults: Map<String, JRType<*>> = emptyMap()) {
        this.parent = parent
        this.mutable = mutable
        variables.putAll(defaults)
    }

    fun getParent(): Environment? {
        return parent
    }

    internal fun setMutable(mutable: Boolean) {
        this.mutable = mutable
    }

    fun isMutable(): Boolean {
        return mutable
    }

    fun get(name: String): JRType<*> {
        return if (globals.containsKey(name)) {
            globals[name] ?: JRNone.NONE
        } else if (variables.containsKey(name)) {
            variables[name] ?: JRNone.NONE
        } else parent?.get(name) ?: throw VariableNotFoundException(name)
    }

    fun assign(name: String, value: JRType<*>) {
        if (globals.containsKey(name)) {
            throw AssignmentException(name, value)
        } else if (mutable && !parentContains(name)) {
            variables[name] = value
        } else if (parent != null) {
            parent.assign(name, value)
        } else {
            throw AssignmentException(name, value)
        }
    }

    private fun contains(name: String): Boolean {
        return globals.containsKey(name) || variables.containsKey(name) || (parent?.contains(name) ?: false)
    }

    private fun parentContains(name: String): Boolean {
        return parent?.contains(name) ?: false
    }

    override fun toString(): String {
        return "Environment(mutable=$mutable, variables=$variables)"
    }

    companion object {
        private val globals = GlobalEnvironmentConfiguration.getGlobals()
    }
}