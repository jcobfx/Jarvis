package pl.com.foks.jarvis

import pl.com.foks.jarvis.interpreter.Interpreter
import pl.com.foks.jarvis.scanners.Lexer
import pl.com.foks.jarvis.scanners.Parser
import pl.com.foks.jarvis.interpreter.types.JRType
import kotlin.system.exitProcess

class Jarvis {
    private val sourceCode: String
    private val debug: Boolean

    private constructor(sourceCode: String, debug: Boolean = false) {
        this.sourceCode = sourceCode
        this.debug = debug
    }

    fun start(): JRType<*> {
        val tokens = Lexer().tokenize(sourceCode)
        if (debug) {
            println("Tokens:")
            tokens.forEach { println(it) }
        }

        val parsed = Parser().parse(tokens)
        if (debug) {
            println("Parsed:")
            parsed.forEach { println(it) }
        }

        return Interpreter().interpret(parsed)
    }

    companion object {
        fun init(filepath: String, debug: Boolean): Jarvis {
            val sourceCode = Jarvis::class.java.classLoader.getResourceAsStream(filepath)
                ?.bufferedReader()
                ?.use { it.readText() }
                ?: throw IllegalArgumentException("File not found: $filepath")
            return Jarvis(sourceCode, debug)
        }
    }
}

fun main(vararg args: String) {
    if (args.isEmpty()) {
        println("Usage: jarvis <source_file> [-options]")
        exitProcess(1)
    }

    if (args[0] == "-h" || args[0] == "--help") {
        println("Usage: jarvis <source_file> [-options]")
        println("Options:")
        println("  -h, --help    Show this help message")
        println("  -d, --debug   Enable debug mode")
        exitProcess(0)
    }

    val debug = args.contains("-d") || args.contains("--debug")

    val jarvis = Jarvis.init(args[0], debug)
    exitProcess(if (jarvis.start().toBool().value()) 0 else 1)
}