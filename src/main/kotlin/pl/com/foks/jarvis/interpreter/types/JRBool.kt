package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.exceptions.IllegalOperationException

class JRBool : JRType<JRBool> {
    val value: Boolean

    private constructor(value: Boolean) : super(null) {
        this.value = value
    }

    fun value(): Boolean {
        return value
    }

    override fun plus(other: JRType<*>): JRBool {
        throw IllegalOperationException("plus", this::class.simpleName ?: "JRBool")
    }

    override fun minus(other: JRType<*>): JRBool {
        throw IllegalOperationException("minus", this::class.simpleName ?: "JRBool")
    }

    override fun times(other: JRType<*>): JRBool {
        throw IllegalOperationException("times", this::class.simpleName ?: "JRBool")
    }

    override fun div(other: JRType<*>): JRBool {
        throw IllegalOperationException("div", this::class.simpleName ?: "JRBool")
    }

    override fun rem(other: JRType<*>): JRBool {
        throw IllegalOperationException("rem", this::class.simpleName ?: "JRBool")
    }

    override fun unaryMinus(): JRBool {
        throw IllegalOperationException("unaryMinus", this::class.simpleName ?: "JRBool")
    }

    override fun not(): JRBool {
        return if (this.value) FALSE else TRUE
    }

    override fun compareTo(other: JRType<*>): Int {
        return value.compareTo(other.toBool().value)
    }

    override fun or(other: JRType<*>): JRBool {
        return if (this.value || other.toBool().value) TRUE else FALSE
    }

    override fun and(other: JRType<*>): JRBool {
        return if (this.value && other.toBool().value) TRUE else FALSE
    }

    override fun xor(other: JRType<*>): JRBool {
        return if (this.value != other.toBool().value) TRUE else FALSE
    }

    override fun toBool(): JRBool {
        return this
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

    companion object {
        val TRUE = JRBool(true)
        val FALSE = JRBool(false)
    }
}