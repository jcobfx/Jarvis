package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.exceptions.IllegalOperationException
import pl.com.foks.jarvis.exceptions.InvalidArgumentsException
import pl.com.foks.jarvis.interpreter.Environment

class JRTuple : JRType<JRTuple> {
    val elements: Array<JRType<*>>

    constructor(vararg elements: JRType<*>) :
            super(Environment(null).apply {
                val get = object : JRConsumer(this, listOf("index"), emptyList()) {
                    override fun consume(arguments: List<JRType<*>>): JRType<*> {
                        if (arguments[0] is JRNumber) {
                            val index = (arguments[0] as JRNumber).value.toInt()
                            return if (index in elements.indices) {
                                elements[index]
                            } else {
                                JRTuple()
                            }
                        }
                        throw InvalidArgumentsException("get", listOf("index"), arguments.map { it.toString() })
                    }
                }
                assign("get", get)
                setMutable(false)
            }) {
        this.elements = elements as Array<JRType<*>>
    }

    constructor(list: List<JRType<*>>) : this(*list.toTypedArray())

    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }

    fun size(): Int {
        return elements.size
    }

    fun get(index: Int): JRType<*> {
        return if (index in elements.indices) {
            elements[index]
        } else {
            throw IndexOutOfBoundsException("Index $index out of bounds for length ${elements.size}")
        }
    }

    fun get(): JRType<*> {
        return if (elements.size == 1) {
            elements[0]
        } else {
            throw IllegalOperationException("get", this::class.simpleName ?: "JRTuple")
        }
    }

    override fun plus(other: JRType<*>): JRTuple {
        throw IllegalOperationException("plus", this::class.simpleName ?: "JRTuple")
    }

    override fun minus(other: JRType<*>): JRTuple {
        throw IllegalOperationException("minus", this::class.simpleName ?: "JRTuple")
    }

    override fun times(other: JRType<*>): JRTuple {
        throw IllegalOperationException("times", this::class.simpleName ?: "JRTuple")
    }

    override fun div(other: JRType<*>): JRTuple {
        throw IllegalOperationException("div", this::class.simpleName ?: "JRTuple")
    }

    override fun rem(other: JRType<*>): JRTuple {
        throw IllegalOperationException("rem", this::class.simpleName ?: "JRTuple")
    }

    override fun unaryMinus(): JRTuple {
        throw IllegalOperationException("unaryMinus", this::class.simpleName ?: "JRTuple")
    }

    override fun not(): JRBool {
        return this.toBool().not()
    }

    override fun compareTo(other: JRType<*>): Int {
        if (other !is JRTuple) {
            throw IllegalOperationException("compareTo", this::class.simpleName ?: "JRTuple")
        }

        if (this.isEmpty() && other.isEmpty()) return 0
        if (this.isEmpty()) return -1
        if (other.isEmpty()) return 1

        val thisSize = this.elements.size
        val otherSize = other.elements.size

        return when {
            thisSize < otherSize -> -1
            thisSize > otherSize -> 1
            else -> 0
        }
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
        return if (!isEmpty() || elements.any { it == JRNone.NONE }) JRBool.FALSE else JRBool.TRUE
    }

    override fun toString(): String {
        return "(" +
                elements.joinToString(", ") { it.toString() } +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JRTuple

        return elements.contentEquals(other.elements)
    }

    override fun hashCode(): Int {
        return elements.contentHashCode()
    }
}