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
    val value: Any?,
    val reason: String = "Cannot assign value to variable"
) : InterpreterException("Cannot assign to variable '$variableName' with value '$value'\nReason: $reason") {
    override fun toString(): String {
        return "AssignmentException(variableName='$variableName', value='$value, reason='$reason')"
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

class OutOfBoundsException(
    val index: Int,
    val size: Int
) : InterpreterException("Index out of bounds: index $index, size $size") {
    override fun toString(): String {
        return "OutOfBoundsException(index=$index, size=$size)"
    }
}

class IllegalOperationException(
    val operation: TokenType,
) : InterpreterException("Illegal operation '$operation'") {
    override fun toString(): String {
        return "IllegalOperationException(operation='$operation')"
    }
}

class InvalidExpressionException(
    val expression: Expression
) : InterpreterException("Invalid expression: $expression") {
    override fun toString(): String {
        return "InvalidExpressionException(expression=$expression)"
    }
}

class IllegalCastException(
    val from: String,
    val to: String,
    val value: Any?
) : InterpreterException("Illegal cast from '$from' to '$to' with value '$value'") {
    override fun toString(): String {
        return "IllegalCastException(from='$from', to='$to')"
    }
}