package pl.com.foks.jarvis.exceptions

import pl.com.foks.jarvis.scanners.TokenType

abstract class InterpreterException(message: String) : RuntimeException(message) {
    override fun toString(): String {
        throw NotImplementedError("toString() is not implemented")
    }
}

class UnknownOperatorException(
    val operator: TokenType
) : InterpreterException("Unknown operator '$operator'") {
    override fun toString(): String {
        return "UnknownOperatorException(operator='$operator')"
    }
}

class VariableNotFoundException(
    val variableName: String
) : InterpreterException("Variable '$variableName' not found") {
    override fun toString(): String {
        return "VariableNotFoundException(variableName='$variableName')"
    }
}

class AssignmentException(
    val variableName: String,
    val value: Any?
) : InterpreterException("Cannot assign to variable '$variableName' with value '$value'") {
    override fun toString(): String {
        return "AssignmentException(variableName='$variableName', value='$value')"
    }
}

class InvalidArgumentCountException(
    val expected: Int,
    val actual: Int
) : InterpreterException("Invalid argument count: expected $expected, got $actual") {
    override fun toString(): String {
        return "InvalidArgumentCountException(expected=$expected, actual=$actual)"
    }
}