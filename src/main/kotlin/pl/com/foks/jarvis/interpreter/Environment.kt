package pl.com.foks.jarvis.interpreter

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

    internal fun setMutable(mutable: Boolean) {
        this.mutable = mutable
    }

    fun isMutable(): Boolean {
        return mutable
    }

    fun copy(mutable: Boolean): Environment {
        val newEnv = Environment(parent, mutable)
        newEnv.variables.putAll(variables)
        return newEnv
    }

    fun get(name: String): Any? {
        return if (global.variables.containsKey(name)) {
            global.variables[name]
        } else if (variables.containsKey(name)) {
            variables[name]
        } else if (parent != null) {
            parent.get(name)
        } else {
            throw IllegalArgumentException("Variable $name not found")
        }
    }

    fun assign(name: String, value: Any?) {
        if (global.variables.containsKey(name)) {
            throw IllegalArgumentException("Cannot assign to global variable $name")
        } else if (mutable) {
            variables[name] = value
        } else if (parent != null) {
            parent.assign(name, value)
        } else {
            throw IllegalArgumentException("Cannot assign to immutable variable $name")
        }
    }

    companion object {
        private val global = Environment(null, false)

        init {
            global.variables["print"] = Consumable { arguments ->
                val result = arguments.joinToString(", ") { it.toString() }
                println(result)
                return@Consumable Tuple()
            }
        }
    }
}