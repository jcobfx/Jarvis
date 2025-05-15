package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.exceptions.IllegalOperationException

class JRLiteral(val value: String) : JRType<JRLiteral>(null) {
    override fun plus(other: JRType<*>): JRLiteral {
        if (other is JRLiteral) {
            return JRLiteral(this.value + other.value)
        }
        if (other is JRNumber) {
            return JRLiteral(this.value + other.toString())
        }
        throw IllegalOperationException("plus", this::class.simpleName ?: "JRLiteral")
    }

    override fun minus(other: JRType<*>): JRLiteral {
        throw IllegalOperationException("minus", this::class.simpleName ?: "JRLiteral")
    }

    override fun times(other: JRType<*>): JRLiteral {
        throw IllegalOperationException("times", this::class.simpleName ?: "JRLiteral")
    }

    override fun div(other: JRType<*>): JRLiteral {
        throw IllegalOperationException("div", this::class.simpleName ?: "JRLiteral")
    }

    override fun rem(other: JRType<*>): JRLiteral {
        throw IllegalOperationException("rem", this::class.simpleName ?: "JRLiteral")
    }

    override fun unaryMinus(): JRLiteral {
        throw IllegalOperationException("unaryMinus", this::class.simpleName ?: "JRLiteral")
    }

    override fun not(): JRBool {
        return this.toBool().not()
    }

    override fun compareTo(other: JRType<*>): Int {
        if (other is JRLiteral) {
            return this.value.compareTo(other.value)
        }
        if (other is JRNumber) {
            return this.value.compareTo(other.toString())
        }
        throw IllegalOperationException("compareTo", this::class.simpleName ?: "JRLiteral")
    }

    override fun or(other: JRType<*>): JRBool {
        return this.toBool().or(other.toBool())
    }

    override fun and(other: JRType<*>): JRBool {
        return this.toBool().and(other.toBool())
    }

    override fun xor(other: JRType<*>): JRBool {
        return this.toBool().xor(other.toBool())
    }

    override fun toBool(): JRBool {
        return if (value == "true") JRBool.TRUE else JRBool.FALSE
    }

    override fun toString(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JRLiteral

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}