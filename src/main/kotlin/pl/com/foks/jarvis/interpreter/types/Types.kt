package pl.com.foks.jarvis.interpreter.types

interface JRType<out T : JRType<T>> : JRNamespace {
    abstract override fun toString(): String
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
}

interface JRConsumable<T: JRConsumable<T>> : JRType<T> {
    fun consume(arguments: List<JRType<*>>): JRType<*>
}