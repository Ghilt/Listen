package compressedlang

class FunctionRepository {

    fun get(ft: FunctionToken) = repo[ft.token]

    private val repo: Map<Char, Function> = mapOf(
        // Nilads
        '_' to currentListNilad,
        'i' to indexNilad,

        // Monads
        'l' to lengthMonad,

        // DYADS - LISTS
        'F' to filterDyad,

        // DYADS - SINGLE VALUE
        '<' to smallerThanDyad,
        '>' to largerThanDyad,
        '+' to additionDyad,
        '-' to subtractionDyad,
        '*' to multiplicationDyad,
        '=' to equalToDyad
    )

    fun getDiagnosticsString(function: Function): String {
        return "${repo.entries.filter { it.value == function }[0].key}"
    }
}

