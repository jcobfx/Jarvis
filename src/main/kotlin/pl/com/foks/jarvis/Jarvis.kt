package pl.com.foks.jarvis

import pl.com.foks.jarvis.interpreter.Interpreter
import pl.com.foks.jarvis.interpreter.types.JRTuple
import pl.com.foks.jarvis.scanners.Lexer
import pl.com.foks.jarvis.scanners.Parser
import java.io.File
import kotlin.system.exitProcess

class Jarvis {
    private val sourceCode: String
    private val debug: Boolean

    private constructor(sourceCode: String, debug: Boolean = false) {
        this.sourceCode = sourceCode
        this.debug = debug
    }

    fun start(): JRTuple {
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

        return Interpreter().interpret(parsed) as JRTuple
    }

    companion object {
        fun init(filepath: String, debug: Boolean): Jarvis {
            val file = File(filepath)
            if (!file.exists()) {
                println("File not found: $filepath")
                exitProcess(1)
            }
            val sourceCode = file
                .bufferedReader()
                .use { it.readText() }
            return Jarvis(sourceCode, debug)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            if (args.isEmpty()) {
                println("Usage: jarvis <source_file> [-options]")
                exitProcess(0)
            }

            if (args[0] == "-h" || args[0] == "--help") {
                println("Usage: jarvis <source_file> [-options]")
                println("Options:")
                println("  -h, --help    Show this help message")
                println("  -d, --debug   Enable debug mode")
                exitProcess(0)
            }

            val debug = args.contains("-d") || args.contains("--debug")

            val result = init(args[0], debug).start()
            exitProcess(if (result.isEmpty()) 0 else result.get().toString().toIntOrNull() ?: 1)
        }
    }
}