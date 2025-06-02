package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.interpreter.Environment

class JRNone : JRType<JRNone> {
    private constructor()

    override fun toString(): String {
        return "None"
    }

    override fun equals(other: Any?): Boolean {
        return other is JRNone
    }

    override fun hashCode(): Int {
        return 0
    }

    override fun get(name: String): JRType<*> {
        TODO("Not yet implemented")
    }

    override fun env(): Environment {
        TODO("Not yet implemented")
    }

    companion object {
        val NONE = JRNone()
    }
}