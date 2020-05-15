package compressedlang

import java.lang.IllegalArgumentException

enum class TYPE {
    LIST_TYPE, NUMBER, STRING, BOOL, ANY;

    fun isSubtypeOf(type: TYPE?): Boolean {
        return this == type || typeRelations[this]?.contains(type) ?: throw DeveloperError("Accessing non existing type: $this")
    }
}

// No subtypes currently
private val typeRelations: Map<TYPE, Set<TYPE>> = mapOf(
    TYPE.LIST_TYPE to setOf(TYPE.ANY),
    TYPE.NUMBER to setOf(TYPE.ANY),
    TYPE.STRING to setOf(TYPE.ANY),
    TYPE.BOOL to setOf(TYPE.ANY),
    TYPE.ANY to setOf()
)

fun <E  : Any> List<E>.typeOfList(): TYPE {

    if (this.isEmpty()) {
        return TYPE.ANY
    }
    return this[0].typeOfValue()
}

fun Any.typeOfValue(): TYPE {
    return when (this) {
        is Boolean -> TYPE.BOOL
        is Number -> TYPE.NUMBER
        is String -> TYPE.STRING
        is Char -> TYPE.STRING // Maybe support Chars?
        is List<*> -> TYPE.LIST_TYPE
        is ContextKey -> TYPE.ANY // Handling special cases
        else -> throw IllegalArgumentException("Type not supported: $this")
    }
}
