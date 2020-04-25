package compressedlang

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