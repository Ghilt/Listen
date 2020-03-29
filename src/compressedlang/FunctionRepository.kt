package compressedlang

class FunctionRepository {

    fun get(ft: FunctionToken) = repo[ft.token]

    private val repo: Map<Char, Function> = mapOf(
        // Nilads
        '_' to currentListNilad,
        'i' to indexNilad,

        // Monads
        'i' to lengthMonad,

        // DYADS - LISTS
        'F' to filterDyad,

        // DYADS - SINGLE VALUE
        '<' to smallerThanDyad,
        '>' to largerThanDyad,
        '*' to multiplyDyad,
        '=' to equalToDyad
    )

}