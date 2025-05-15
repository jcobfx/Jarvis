package pl.com.foks.jarvis.exceptions

import pl.com.foks.jarvis.models.Expression
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

class InvalidArgumentsException(
    val functionName: String,
    val expected: List<String>,
    val actual: List<String>
) : InterpreterException("Invalid arguments for function '$functionName': expected $expected, got $actual") {
    override fun toString(): String {
        return "InvalidArgumentsException(functionName='$functionName', expected=$expected, actual=$actual)"
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

class InconvertibleTypeException(
    val from: String,
    val to: String
) : InterpreterException("Cannot convert from '$from' to '$to'") {
    override fun toString(): String {
        return "InconvertibleTypeException(from='$from', to='$to')"
    }
}

class IllegalOperationException(
    val operation: String,
    val type: String
) : InterpreterException("Illegal operation '$operation' on type '$type'") {
    override fun toString(): String {
        return "IllegalOperationException(operation='$operation', type='$type')"
    }
}

class NullValueException(
    val variableName: String,
    val expression: Expression
) : InterpreterException("Variable '$variableName' is null in expression $expression") {
    override fun toString(): String {
        return "NullValueException(variableName='$variableName', expression=$expression)"
    }
}

class InvalidExpressionException(
    val expression: Expression
) : InterpreterException("Invalid expression: $expression") {
    override fun toString(): String {
        return "InvalidExpressionException(expression=$expression)"
    }
}