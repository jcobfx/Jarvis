package pl.com.foks.jarvis.types

class Tuple(vararg val elements: Any?) {
    override fun toString(): String {
        return "Tuple(" +
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
}