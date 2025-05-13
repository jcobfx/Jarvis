package pl.com.foks.jarvis.interpreter

import pl.com.foks.jarvis.exceptions.AssignmentException
import pl.com.foks.jarvis.exceptions.VariableNotFoundException
import pl.com.foks.jarvis.types.Consumable
import pl.com.foks.jarvis.types.Tuple

class Environment {
    private val parent: Environment?
    private var mutable: Boolean
    private val variables: MutableMap<String, Any?> = mutableMapOf()

    constructor(parent: Environment?, mutable: Boolean = true, defaults: Map<String, Any?> = emptyMap()) {
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

    fun get(name: String): Any? {
        return if (global.variables.containsKey(name)) {
            global.variables[name]
        } else if (variables.containsKey(name)) {
            variables[name]
        } else if (parent != null) {
            parent.get(name)
        } else {
            throw VariableNotFoundException("Variable $name not found")
        }
    }

    fun assign(name: String, value: Any?) {
        if (global.variables.containsKey(name)) {
            throw AssignmentException(name, value)
        } else if (mutable && !parentContains(name)) {
            variables[name] = value
        } else if (parent != null && parent.contains(name)) {
            parent.assign(name, value)
        } else {
            throw AssignmentException(name, value)
        }
    }

    private fun contains(name: String): Boolean {
        return global.variables.containsKey(name) || variables.containsKey(name) || (parent?.contains(name) ?: false)
    }

    private fun parentContains(name: String): Boolean {
        return parent?.contains(name) ?: false
    }

    override fun toString(): String {
        return "Environment(mutable=$mutable, variables=$variables)"
    }

    companion object {
        private val global = Environment(null, false)

        init {
            global.variables["print"] = object : Consumable {
                override fun consume(arguments: List<Any?>): Any {
                    println(arguments.joinToString(", ") { it.toString() })
                    return Tuple()
                }
            }
        }
    }
}