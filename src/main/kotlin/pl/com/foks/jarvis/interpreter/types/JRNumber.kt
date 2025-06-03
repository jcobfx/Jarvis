package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.exceptions.IllegalOperationException
import pl.com.foks.jarvis.interpreter.Environment
import pl.com.foks.jarvis.scanners.TokenType

class JRNumber(val value: Double) : JRType<JRNumber>, JRComparable, JRPlus<JRNumber>, JRMinus<JRNumber>,
    JRTimes<JRNumber>, JRDiv<JRNumber>, JRRem<JRNumber>, JRUnaryMinus<JRNumber> {

    override fun plus(other: JRPlus<*>): JRNumber {
        if (other is JRNumber) {
            return JRNumber(this.value + other.value)
        }
        throw IllegalOperationException(TokenType.PLUS)
    }

    override fun minus(other: JRMinus<*>): JRNumber {
        if (other is JRNumber) {
            return JRNumber(this.value - other.value)
        }
        throw IllegalOperationException(TokenType.MINUS)
    }

    override fun times(other: JRTimes<*>): JRNumber {
        if (other is JRNumber) {
            return JRNumber(this.value * other.value)
        }
        throw IllegalOperationException(TokenType.MULTIPLY)
    }

    override fun div(other: JRDiv<*>): JRNumber {
        if (other is JRNumber) {
            if (other.value == 0.0) {
                throw ArithmeticException("Division by zero")
            }
            return JRNumber(this.value / other.value)
        }
        throw IllegalOperationException(TokenType.DIVIDE)
    }

    override fun rem(other: JRRem<*>): JRNumber {
        if (other is JRNumber) {
            if (other.value == 0.0) {
                throw ArithmeticException("Division by zero")
            }
            return JRNumber(this.value % other.value)
        }
        throw IllegalOperationException(TokenType.REMAINDER)
    }

    override fun unaryMinus(): JRNumber {
        return JRNumber(-this.value)
    }

    override fun compareTo(other: JRComparable): Int {
        if (other is JRNumber) {
            return this.value.compareTo(other.value)
        }
        throw IllegalArgumentException("Cannot compare ${this::class.simpleName} with ${other.javaClass.simpleName}")
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

    override fun get(name: String): JRType<*> {
        TODO("Not yet implemented")
    }

    override fun env(): Environment {
        TODO("Not yet implemented")
    }
}