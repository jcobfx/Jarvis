package pl.com.foks.jarvis.config

import pl.com.foks.jarvis.interpreter.Environment
import pl.com.foks.jarvis.interpreter.types.JRConsumer
import pl.com.foks.jarvis.interpreter.types.JRLiteral
import pl.com.foks.jarvis.interpreter.types.JRNone
import pl.com.foks.jarvis.interpreter.types.JRType

class GlobalEnvironmentConfiguration {
    companion object {
        fun getGlobals(): Map<String, JRType<*>> {
            return mapOf(
                "print" to object : JRConsumer(Environment(null, false), emptyList(), emptyList()) {
                    override fun consume(arguments: List<JRType<*>>): JRType<*> {
                        if (arguments.isEmpty()) {
                            println()
                        } else if (arguments[0] is JRLiteral && arguments[0].toString().contains("{}")) {
                            println(formatString(arguments[0].toString(), arguments.subList(1, arguments.size)))
                        } else {
                            println(arguments.joinToString(", "))
                        }
                        return JRNone.NONE
                    }
                },
                "input" to object : JRConsumer(Environment(null, false), emptyList(), emptyList()) {
                    override fun consume(arguments: List<JRType<*>>): JRType<*> {
                        return JRLiteral(readLine() ?: "")
                    }
                }
            )
        }

        private fun formatString(string: String, arguments: List<Any?>): String {
            var formattedString = string
            for (i in arguments.indices) {
                formattedString = formattedString.replaceFirst("{}", arguments[i].toString())
            }
            return formattedString
        }
    }
}