package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.interpreter.Environment

interface JRFeat

interface JRPlus<out T: JRType<T>>: JRFeat {
    operator fun plus(other: JRPlus<*>): T
}

interface JRMinus<out T: JRType<T>>: JRFeat {
    operator fun minus(other: JRMinus<*>): T
}

interface JRTimes<out T: JRType<T>>: JRFeat {
    operator fun times(other: JRTimes<*>): T
}

interface JRDiv<out T: JRType<T>>: JRFeat {
    operator fun div(other: JRDiv<*>): T
}

interface JRRem<out T: JRType<T>>: JRFeat {
    operator fun rem(other: JRRem<*>): T
}

interface JRUnaryMinus<out T: JRType<T>>: JRFeat {
    operator fun unaryMinus(): T
}

interface JRComparable: JRFeat {
    operator fun compareTo(other: JRComparable): Int
}

interface JRLogic<out T: JRType<T>>: JRFeat {
    fun value(): Boolean
    operator fun not(): T
    infix fun or(other: JRLogic<*>): T
    infix fun and(other: JRLogic<*>): T
    infix fun xor(other: JRLogic<*>): T
}

interface JRNamespace {
    fun get(name: String): JRType<*>
    fun env(): Environment
}