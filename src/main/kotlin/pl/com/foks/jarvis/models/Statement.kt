package pl.com.foks.jarvis.models

abstract class Statement {
    abstract fun <T> accept(visitor: StatementVisitor<T>): T
}

class ExpressionStatement(val expression: Expression) : Statement() {
    override fun <T> accept(visitor: StatementVisitor<T>): T {
        return visitor.visitExpressionStatement(this)
    }

    override fun toString(): String {
        return "ExpressionStatement(expression=$expression)"
    }
}

class ConsumerAssignmentStatement(val identifier: String, val parameters: List<String>, val statements: List<Statement>) : Statement() {
    override fun <T> accept(visitor: StatementVisitor<T>): T {
        return visitor.visitConsumerAssignmentStatement(this)
    }

    override fun toString(): String {
        return "ConsumerAssignmentStatement(identifier='$identifier', statements=$statements)"
    }
}

class AssignmentStatement(val identifier: String, val expression: Expression) : Statement() {
    override fun <T> accept(visitor: StatementVisitor<T>): T {
        return visitor.visitAssignmentStatement(this)
    }

    override fun toString(): String {
        return "AssignmentStatement(identifier='$identifier', expression=$expression)"
    }
}

class ReturnStatement(val expression: Expression?) : Statement() {
    override fun <T> accept(visitor: StatementVisitor<T>): T {
        return visitor.visitReturnStatement(this)
    }

    override fun toString(): String {
        return "ReturnStatement(expression=$expression)"
    }
}

interface StatementVisitor<T> {
    fun visitExpressionStatement(statement: ExpressionStatement): T
    fun visitConsumerAssignmentStatement(statement: ConsumerAssignmentStatement): T
    fun visitAssignmentStatement(statement: AssignmentStatement): T
    fun visitReturnStatement(statement: ReturnStatement): T
}