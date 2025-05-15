package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.exceptions.IllegalOperationException
import pl.com.foks.jarvis.interpreter.Environment

class JRNumber(val value: Double) : JRType<JRNumber>(null) {
    override fun plus(other: JRType<*>): JRNumber {
        if (other is JRNumber) {
            return JRNumber(this.value + other.value)
        }
        throw IllegalOperationException("plus", this::class.simpleName ?: "JRNumber")
    }

    override fun minus(other: JRType<*>): JRNumber {
        if (other is JRNumber) {
            return JRNumber(this.value - other.value)
        }
        throw IllegalOperationException("minus", this::class.simpleName ?: "JRNumber")
    }

    override fun times(other: JRType<*>): JRNumber {
        if (other is JRNumber) {
            return JRNumber(this.value * other.value)
        }
        throw IllegalOperationException("times", this::class.simpleName ?: "JRNumber")
    }

    override fun div(other: JRType<*>): JRNumber {
        if (other is JRNumber) {
            if (other.value == 0.0) {
                throw ArithmeticException("Division by zero")
            }
            return JRNumber(this.value / other.value)
        }
        throw IllegalOperationException("div", this::class.simpleName ?: "JRNumber")
    }

    override fun rem(other: JRType<*>): JRNumber {
        if (other is JRNumber) {
            if (other.value == 0.0) {
                throw ArithmeticException("Division by zero")
            }
            return JRNumber(this.value % other.value)
        }
        throw IllegalOperationException("rem", this::class.simpleName ?: "JRNumber")
    }

    override fun unaryMinus(): JRNumber {
        return JRNumber(-this.value)
    }

    override fun not(): JRBool {
        return this.toBool().not()
    }

    override fun compareTo(other: JRType<*>): Int {
        if (other is JRNumber) {
            return this.value.compareTo(other.value)
        }
        throw IllegalArgumentException("Cannot compare ${this::class.simpleName} with ${other.javaClass.simpleName}")
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
        return if (value == 0.0) JRBool.FALSE else JRBool.TRUE
    }

    override fun toString(): String {
        return value.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JRNumber

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}