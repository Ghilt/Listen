@file:Suppress(
    "UNCHECKED_CAST",
    "CascadeIf"/* I think it is too aggressive, some ifs look better than bracketed when's*/
)

package compressedlang.fncs

import collectionlib.growEntries
import compressedlang.Precedence
import compressedlang.SyntaxError
import compressedlang.TYPE

val growEntriesTriad = Triad<List<Any>, Int, Int, List<Any>>(
    defaultImplicitInput = valueThenCurrentListNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.LIST_TYPE,
) { data: List<Any>, length: Int, step: Int ->

    if (isFilledWithNumbers(data)) {
        val castToNumbers = data as List<Number>
        castToNumbers.growEntries(length, step)
    } else if (isFilledWithStrings(data)) {
        val castToStrings = data as List<String>
        castToStrings.map { it.growEntries(length, step) }
    } else {
        throw SyntaxError("Grow entries given illegal argument: [${data.joinToString() }]. Lists of mixed strings and numbers are not supported")
    }
}