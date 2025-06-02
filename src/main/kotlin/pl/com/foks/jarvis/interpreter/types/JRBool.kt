package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.interpreter.Environment

class JRBool : JRType<JRBool>, JRComparable, JRLogic<JRBool> {
    val value: Boolean

    private constructor(value: Boolean) {
        this.value = value
    }

    override fun value(): Boolean {
        return value
    }

    override fun not(): JRBool {
        return if (this.value) FALSE else TRUE
    }

    override fun or(other: JRLogic<*>): JRBool {
        return if (this.value || other.value()) TRUE else FALSE
    }

    override fun and(other: JRLogic<*>): JRBool {
        return if (this.value && other.value()) TRUE else FALSE
    }

    override fun xor(other: JRLogic<*>): JRBool {
        return if (this.value != other.value()) TRUE else FALSE
    }

    override fun compareTo(other: JRComparable): Int {
        if (other !is JRBool) {
            throw IllegalArgumentException("Cannot compare JRBool with ${other::class.simpleName}")
        }
        return this.value.compareTo(other.value)
    }

    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JRBool

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun get(name: String): JRType<*> {
        TODO("Not yet implemented")
    }

    override fun env(): Environment {
        TODO("Not yet implemented")
    }

    companion object {
        val TRUE = JRBool(true)
        val FALSE = JRBool(false)
    }
}