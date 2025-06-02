package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.exceptions.IllegalOperationException
import pl.com.foks.jarvis.exceptions.InvalidArgumentsException
import pl.com.foks.jarvis.exceptions.OutOfBoundsException
import pl.com.foks.jarvis.interpreter.Environment
import pl.com.foks.jarvis.scanners.TokenType

class JRTuple : JRType<JRTuple>, JRComparable {
    val environment: Environment
    val elements: Array<JRType<*>>

    constructor(vararg elements: JRType<*>) {
        environment = Environment(null).apply {
            val get = object : JRConsumer(this, listOf("index"), emptyList()) {
                override fun consume(arguments: List<JRType<*>>): JRType<*> {
                    if (arguments[0] is JRNumber) {
                        val index = (arguments[0] as JRNumber).value.toInt()
                        return if (index in elements.indices) {
                            elements[index]
                        } else {
                            JRNone.NONE
                        }
                    }
                    throw InvalidArgumentsException("get", listOf("index"), arguments.map { it.toString() })
                }
            }
            assign("get", get)
            setMutable(false)
        }
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
            throw OutOfBoundsException(index, elements.size)
        }
    }

    fun get(): JRType<*> {
        return if (elements.size == 1) {
            elements[0]
        } else {
            throw OutOfBoundsException(1, elements.size)
        }
    }

    override fun compareTo(other: JRComparable): Int {
        if (other !is JRTuple) {
            throw IllegalOperationException(TokenType.EQUALS_EQUALS)
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

    override fun get(name: String): JRType<*> {
        return environment.get(name)
    }

    override fun env(): Environment {
        return environment
    }
}