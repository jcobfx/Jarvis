package pl.com.foks.jarvis.types

class Tuple(vararg val elements: Any?) {
    fun isEmpty(): Boolean {
        return elements.isEmpty()
    }

    fun get(index: Int): Any? {
        return if (index in elements.indices) {
            elements[index]
        } else {
            throw IndexOutOfBoundsException("Index $index out of bounds for length ${elements.size}")
        }
    }

    override fun toString(): String {
        return "(" +
                elements.joinToString(", ") { it.toString() } +
                ")"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Tuple) return false

        if (!elements.contentEquals(other.elements)) return false

        return true
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }

    companion object {
        fun empty(): Tuple {
            return Tuple()
        }
    }
}