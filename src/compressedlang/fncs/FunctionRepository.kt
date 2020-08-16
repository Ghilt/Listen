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

        // Environment
        '_' to currentListNilad,
        '~' to secondCurrentListNilad,
        'q' to currentListCountNilad,
        'i' to indexNilad,
        'v' to valueNilad,
        ',' to no_opNilad,

        // Storage
        'p' to peekStaticStackNilad,
        '€' to popStaticStackNilad,

        ':' to storeOnStaticStackMonad,
        '?' to loadFromStaticMapMonad,

        '¨' to storeOnStaticMapDyad,

        // Maths
        'Σ' to sumMonad,
        'Π' to productMonad,
        'm' to isPrimeMonad,
        'h' to absoluteValueMonad,
        'j' to signMonad,
        'u' to floorMonad,
        'ö' to roundMonad,
        '§' to toIntMonad,

        // Mathematical Dyads on the form number, number -> number
        '+' to additionDyad,
        '-' to subtractionDyad,
        '*' to multiplicationDyad,
        '/' to divisionDyad,
        '¤' to wholeDivisionDyad,
        '%' to moduloDyad, /* Programming standard, negative numbers possible in output */
        '£' to moduloMathematicalDyad, /* second input decides sign of output */
        '^' to powerDyad,
        '{' to minDyad,
        '}' to maxDyad,

        // Boolean
        '!' to notMonad,

        '<' to smallerThanDyad,
        '>' to largerThanDyad,
        '=' to equalToDyad,
        '≠' to notEqualToDyad,
        '&' to andDyad,
        '|' to orDyad,

        // List related
        'l' to lengthMonad,
        'd' to distinctMonad,
        'n' to removeDistinctMonad,
        '$' to listByIndexMonad,
        'r' to reverseListMonad,
        'c' to removeConsecutiveElementsMonad,
        '@' to createListOfValueMonad,

        '[' to takeDyad,
        ']' to dropDyad,
        'e' to elementByIndexDyad,
        '\'' to appendToListDyad,
        'γ' to appendListDyad,
        'z' to zipDyad,

        '#' to padTriad,
        'g' to growEntriesTriad,

        // String Monads
        'k' to toUpperCaseMonad,
        'w' to toLowerCaseMonad,
        'y' to isUpperCaseMonad,
        't' to stringToListMonad,

        'a' to appendToStringDyad,
        's' to joinToStringDyad,

        // Generation
        'b' to alphabetGenerationDyad,
        'o' to oeisGenerationDyad,

        // other
        'x' to obliterateDyad,
        'f' to ifBranchTriad,

        // Context creators
        'Ö' to pipeMonad,
        'C' to chunkMonad,
        'W' to windowMonad,

        'F' to filterDyad,
        'S' to filterSectionedDyad,
        'N' to filterWithNeighborsDyad,

        'A' to allDyad,
        'Ä' to anyDyad,

        'M' to mapDyad,
        'P' to flatMapDyad,

        'E' to extendEntriesDyad,
        'G' to groupedStringListDyad,
        'Z' to zipInsertionDyad

    )

    private fun repoOf(vararg pairs: Pair<Char, Function>): Map<Char, Function> {
        val keys = pairs.map { it.first }
        val duplicates = keys.map { key -> key to keys.count { key == it } }.filter { it.second > 1 }
        if (duplicates.isNotEmpty()) throw DeveloperError("Duplicate key in function repo: $duplicates. Free Chars: ${getCharacterUsedDiagnostic(keys.toSet())}")
        return mapOf(*pairs)
    }

    fun getCharacterUsedDiagnostic(usedKeys: Set<Char> = repo.keys): String {
        val all = "!#¤%&/()=?`@£$€{[]}\\^*'¨-.,_:;<>|ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyzåäöÅÄÖ§½~αβγδεζηθικλμνξοπρςτυφχψω"
        return all.filterNot { usedKeys.contains(it) }
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

