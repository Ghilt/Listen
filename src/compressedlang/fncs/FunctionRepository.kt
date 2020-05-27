package compressedlang.fncs

import compressedlang.DeveloperError
import compressedlang.FunctionToken

class FunctionRepository {

    fun get(ft: FunctionToken) = repo[ft.token]

    private val repo: Map<Char, Function> = mapOf( // TODO I want to crash here if the same key is added twice
        // Control flow
        '(' to startInnerFunctionControlFlow,
        ')' to endInnerFunctionControlFlow,

        // Nilads
        '_' to currentListNilad,
        'q' to currentListCount,
        'i' to indexNilad,
        'v' to valueNilad,
        ',' to no_opNilad,

        // Monads
        'l' to lengthMonad,
        '$' to listByIndexMonad,
        'p' to sumMonad,

        // Dyads
        '<' to smallerThanDyad,
        '>' to largerThanDyad,

        // Mathematical Dyads on the form number, number -> number
        '+' to additionDyad,
        '-' to subtractionDyad,
        '*' to multiplicationDyad,
        '/' to divisionDyad,
        '¤' to wholeDivisionDyad,
        '%' to moduloDyad, /* Programming standard, negative numbers possible in output */
        '£' to moduloMathematicalDyad, /* second input decides sign of output */
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

