@file:Suppress(
    "UNCHECKED_CAST",
    "CascadeIf"/* I think it is too aggressive, some ifs look better than bracketed when's*/
)

package compressedlang.fncs

import collectionlib.growEntries
import compressedlang.Precedence
import compressedlang.SyntaxError
import compressedlang.TYPE
import kotlin.math.absoluteValue

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
        throw SyntaxError("Grow entries given illegal argument: [${data.joinToString()}]. Lists of mixed strings and numbers are not supported")
    }
}

val ifBranchTriad = Triad(
    defaultImplicitInput = valueNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.ANY, TYPE.ANY, TYPE.ANY),
    output = TYPE.ANY,
) { condition: Any, branch1: Any, branch2: Any ->
    if (toBool(condition)) {
        branch1
    } else {
        branch2
    }
}

/* If supplied negative length then pad from the end*/
val padTriad = Triad<List<Any>, Int, Int, List<Any>>(
    defaultImplicitInput = valueNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.ANY, TYPE.NUMBER),
    output = TYPE.ANY,
) { list: List<Any>, value: Any, length: Int ->
    val additionalElements = length.absoluteValue - list.size
    val padding = List(additionalElements) { value }
    if (length < 0) list + padding else padding + list
}