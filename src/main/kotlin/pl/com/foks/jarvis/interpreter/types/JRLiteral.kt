package pl.com.foks.jarvis.interpreter.types

import pl.com.foks.jarvis.exceptions.IllegalCastException
import pl.com.foks.jarvis.exceptions.IllegalOperationException
import pl.com.foks.jarvis.exceptions.InvalidArgumentsException
import pl.com.foks.jarvis.interpreter.Environment
import pl.com.foks.jarvis.scanners.TokenType

class JRLiteral : JRType<JRLiteral>, JRComparable, JRPlus<JRLiteral>, JRNamespace {
    val environment: Environment
    val value: String

    constructor(value: String) {
        environment = Environment(null).apply {
            val toNumber = object : JRConsumer(this, emptyList(), emptyList()) {
                override fun consume(arguments: List<JRType<*>>): JRType<*> {
                    if (arguments.isEmpty()) {
                        return JRNumber(
                            value.toDoubleOrNull() ?: throw IllegalCastException(
                                "String",
                                "Number",
                                value
                            )
                        )
                    }
                    throw InvalidArgumentsException(
                        "toNumber", emptyList(),
                        arguments.map { it.toString() })
                }
            }
            assign("toNumber", toNumber)
            setMutable(false)
        }
        this.value = value
    }

    override fun plus(other: JRPlus<*>): JRLiteral {
        if (other is JRLiteral) {
            return JRLiteral(this.value + other.value)
        }
        if (other is JRNumber) {
            return JRLiteral(this.value + other.toString())
        }
        throw IllegalOperationException(TokenType.PLUS)
    }

    override fun get(name: String): JRType<*> {
        return this.environment.get(name)
    }

    override fun env(): Environment {
        return this.environment
    }

    override fun compareTo(other: JRComparable): Int {
        if (other is JRLiteral) {
            return this.value.compareTo(other.value)
        }
        if (other is JRNumber) {
            return this.value.compareTo(other.toString())
        }
        throw IllegalOperationException(TokenType.EQUALS_EQUALS)
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