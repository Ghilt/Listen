package compressedlang

enum class Token(val source: Char) {
    FILTER('F'),
    MAP('M');

    companion object {
        val SOURCE_MAP: Map<Char, Token> = values().map { it.source to it }.toMap()
    }
}
