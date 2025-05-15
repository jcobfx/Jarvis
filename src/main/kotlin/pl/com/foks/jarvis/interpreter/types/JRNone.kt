package pl.com.foks.jarvis.interpreter.types

class JRNone : JRType<JRNone> {
    private constructor() : super(null)

    override fun plus(other: JRType<*>): JRNone {
        return this
    }

    override fun minus(other: JRType<*>): JRNone {
        return this
    }

    override fun times(other: JRType<*>): JRNone {
        return this
    }

    override fun div(other: JRType<*>): JRNone {
        return this
    }

    override fun rem(other: JRType<*>): JRNone {
        return this
    }

    override fun unaryMinus(): JRNone {
        return this
    }

    override fun not(): JRBool {
        return JRBool.FALSE
    }

    override fun compareTo(other: JRType<*>): Int {
        return 0
    }

    override fun or(other: JRType<*>): JRBool {
        return JRBool.FALSE
    }

    override fun and(other: JRType<*>): JRBool {
        return JRBool.FALSE
    }

    override fun xor(other: JRType<*>): JRBool {
        return JRBool.FALSE
    }

    override fun toBool(): JRBool {
        return JRBool.FALSE
    }

    override fun toString(): String {
        return "None"
    }

    override fun equals(other: Any?): Boolean {
        return other is JRNone
    }

    override fun hashCode(): Int {
        return 0
    }

    companion object {
        val NONE = JRNone()
    }
}