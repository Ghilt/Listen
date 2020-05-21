package compressedlang.fncs

import compressedlang.DeveloperError
import compressedlang.FunctionToken

class FunctionRepository {

    val defaultContextCreator: ContextFunction = createListOfValueDyad

    fun get(ft: FunctionToken) = repo[ft.token]

    private val repo: Map<Char, Function> = mapOf(
        // Control flow
        '#' to innerFunctionControlFlow, // TODO might have single value producing inner function?

        // Nilads
        '_' to currentListNilad,
        'i' to indexNilad,
        'v' to valueNilad,
        ',' to no_opNilad,

        // Monads
        'l' to lengthMonad,
        '$' to listByIndexMonad,

        // Dyads
        '<' to smallerThanDyad,
        '>' to largerThanDyad,
        '+' to additionDyad,
        '-' to subtractionDyad,
        '*' to multiplicationDyad,
        '=' to equalToDyad,

        'e' to elementByIndexDyad,

        // Context creators
        '|' to pipeMonad,

        'F' to filterDyad,
        'M' to mapDyad,
        'P' to flatMapDyad,
        '@' to createListOfValueDyad,
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

