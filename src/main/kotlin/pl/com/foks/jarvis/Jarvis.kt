package pl.com.foks.jarvis

import pl.com.foks.jarvis.interpreter.Interpreter
import pl.com.foks.jarvis.scanners.Lexer
import pl.com.foks.jarvis.scanners.Parser
import pl.com.foks.jarvis.types.Tuple
import kotlin.system.exitProcess

class Jarvis {
    private val sourceCode: String

    private constructor(sourceCode: String) {
        this.sourceCode = sourceCode
    }

    fun start(): Tuple {
        val tokens = Lexer().tokenize(sourceCode)
        val parsed = Parser().parse(tokens)
        return Interpreter().interpret(parsed)
    }

    companion object {
        fun init(filepath: String): Jarvis {
            val sourceCode = Jarvis::class.java.classLoader.getResourceAsStream(filepath)
                ?.bufferedReader()
                ?.use { it.readText() }
                ?: throw IllegalArgumentException("File not found: $filepath")
            return Jarvis(sourceCode)
        }
    }
}

fun main(args: Array<String>) {
    val jarvis = if (args.isNotEmpty()) {
        Jarvis.init(args[0])
    } else {
        Jarvis.init("example.jr")
    }
    exitProcess(if (jarvis.start().isEmpty()) 0 else 1)
}