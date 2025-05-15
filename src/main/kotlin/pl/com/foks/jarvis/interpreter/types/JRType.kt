package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.interpreter.Environment

abstract class JRType<T: JRType<T>>(val environment: Environment?) {

    abstract operator fun plus(other: JRType<*>): T
    abstract operator fun minus(other: JRType<*>): T
    abstract operator fun times(other: JRType<*>): T
    abstract operator fun div(other: JRType<*>): T
    abstract operator fun rem(other: JRType<*>): T
    abstract operator fun unaryMinus(): T
    abstract operator fun not(): JRBool
    abstract operator fun compareTo(other: JRType<*>): Int

    abstract infix fun or(other: JRType<*>): JRBool
    abstract infix fun and(other: JRType<*>): JRBool
    abstract infix fun xor(other: JRType<*>): JRBool

    abstract fun toBool(): JRBool

    fun get(name: String): JRType<*> {
        return environment?.get(name) ?: JRNone.NONE
    }

    abstract override fun toString(): String
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
}