package compressedlang

class FunctionRepository {

    fun get(ft: FunctionToken) = repo[ft.token]

    private val repo: Map<Char, Function> = mapOf(
        // Nilads
        '_' to currentListNilad,
        'i' to indexNilad,
        'v' to valueNilad,

        // Monads
        'l' to lengthMonad,

        // DYADS - LISTS
        'F' to filterDyad,
        'M' to mapDyad,

        // DYADS - SINGLE VALUE
        '<' to smallerThanDyad,
        '>' to largerThanDyad,
        '+' to additionDyad,
        '-' to subtractionDyad,
        '*' to multiplicationDyad,
        '=' to equalToDyad
    )

    fun getDiagnosticsString(function: Function): String {
        val diagnosticsChar = repo.entries.firstOrNull {
            it.value == function
        }?.key ?: when (function) {
            is InnerFunction -> "_i_"
            is ResolvedFunction -> function.value
            else -> throw DeveloperError("Function unaccounted for")
        }
        return "$diagnosticsChar"
    }
}

