package compressedlang.fncs

import compressedlang.DeveloperError
import compressedlang.FunctionToken

class FunctionRepository {

    fun get(ft: FunctionToken) = repo[ft.token]

    private val repo: Map<Char, Function> = repoOf(
        // Control flow
        '(' to startInnerFunctionControlFlow,
        ')' to endInnerFunctionControlFlow,
        ';' to endOuterFunction,

        // Nilads
        '_' to currentListNilad,
        '~' to secondCurrentListNilad,
        'q' to currentListCount,
        'i' to indexNilad,
        'v' to valueNilad,
        ',' to no_opNilad,

        // Monads
        '!' to notMonad,
        'l' to lengthMonad,
        '$' to listByIndexMonad,
        'p' to sumMonad,
        'r' to reverseListMonad,

        // Dyads
        '<' to smallerThanDyad,
        '>' to largerThanDyad,
        '=' to equalToDyad,
        '≠' to notEqualToDyad,
        '&' to andDyad,
        '|' to orDyad,
        '[' to takeDyad,
        ']' to dropDyad,

        'e' to elementByIndexDyad,
        'z' to zipDyad,
        'a' to appendToStringDyad,
        'b' to alphabetGenerationDyad,
        'o' to oeisGenerationDyad,

        // Mathematical Dyads on the form number, number -> number
        '+' to additionDyad,
        '-' to subtractionDyad, // Todo, power dyad please
        '*' to multiplicationDyad,
        '/' to divisionDyad,
        '¤' to wholeDivisionDyad,
        '%' to moduloDyad, /* Programming standard, negative numbers possible in output */
        '£' to moduloMathematicalDyad, /* second input decides sign of output */

        // Triads
        'g' to growEntriesTriad,
        'f' to ifBranchTriad,
        '#' to padTriad,

        // Context creators
        '§' to pipeMonad,
        'C' to chunkMonad,
        'W' to windowMonad,

        'F' to filterDyad,
        'S' to filterSectionedDyad,
        'N' to filterWithNeighborsDyad,

        'A' to allDyad,
        'Ä' to anyDyad,

        'M' to mapDyad,
        'P' to flatMapDyad,
        '@' to createListOfValueDyad,

        'E' to extendEntriesDyad,
        'Z' to zipInsertionDyad

    )

    private fun repoOf(vararg pairs: Pair<Char, Function>, ): Map<Char, Function> {
        val keys = pairs.map { it.first }
        val duplicates = keys.map { key -> key to keys.count { key == it } }.filter { it.second > 1 }
        if (duplicates.isNotEmpty()) throw DeveloperError("Duplicate key in function repo: $duplicates")
        return mapOf(*pairs)
    }

    fun getDiagnosticsString(function: Function?): String {
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

