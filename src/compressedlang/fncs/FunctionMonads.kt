package compressedlang.fncs

import collectionlib.reduceConsecutive
import compressedlang.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.sign

// this could be thought of as a nilad
val notMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.ANY),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHESTER
) { value: Any -> if (toBool(value)) 0 else 1 }

val lengthMonad = Monad(
    defaultImplicitInput = valueThenCurrentListNilad,
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHEST
) { list: List<*> -> list.size }

val listByIndexMonad = Monad(
    defaultImplicitInput = valueThenIndexNilad,
    inputs = listOf(TYPE.NUMBER),
    output = TYPE.LIST_TYPE,
    contextKey = ContextKey.LIST_BY_INDEX, // TODO possible refactor that this monad do not deal with the actual index
    precedence = Precedence.HIGH // TODO need to fix precedences -> Int
) { list: List<*> -> list }

val sumMonad = Monad(
    defaultImplicitInput = valueThenCurrentListNilad,
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHEST
) { list: List<Double> -> list.reduce { e0, e1 -> e0 + e1 } }

val reverseListMonad = Monad(
    defaultImplicitInput = valueThenCurrentListNilad,
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.HIGHEST
) { list: List<Double> -> list.reversed() }

val distinctMonad = Monad(
    defaultImplicitInput = valueThenCurrentListNilad,
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.HIGHEST
) { list: List<*> -> list.distinct() }

val removeDistinctMonad = Monad(
    defaultImplicitInput = valueThenCurrentListNilad,
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.HIGHEST
) { list: List<*> ->
    list.filterNot { list.count { element -> it == element } == 1 }
}

val stringToListMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.STRING),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.HIGHEST
) { text: String -> text.toList().map { "$it" } }

val createListOfValueMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.ANY),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.HIGHEST
) { v: Any -> listOf(v) }

val removeConsecutiveElementsMonad = Monad(
    defaultImplicitInput = valueThenCurrentListNilad,
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.HIGHEST
) { list: List<*> -> list.reduceConsecutive() }

val isPrimeMonad = Monad(
    defaultImplicitInput = valueThenIndexNilad,
    inputs = listOf(TYPE.NUMBER),
    output = TYPE.BOOL,
    precedence = Precedence.HIGHEST
) { v: Int -> isPrime(v) }

val absoluteValueMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.NUMBER),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHEST
) { v: Double -> v.absoluteValue }

val signMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.NUMBER),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHEST
) { v: Double -> v.sign }

val floorMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.NUMBER),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHEST
) { v: Double -> v.toInt() }

val roundMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.NUMBER),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHEST
) { v: Double -> v.roundToInt() }

val toUpperCaseMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.STRING),
    output = TYPE.STRING,
    precedence = Precedence.HIGHEST
) { v: String -> v.toUpperCase() }

val toLowerCaseMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.STRING),
    output = TYPE.STRING,
    precedence = Precedence.HIGHEST
) { v: String -> v.toLowerCase() }

val isUpperCaseMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.STRING),
    output = TYPE.BOOL,
    precedence = Precedence.HIGHEST
) { v: String -> v.all { it.isUpperCase() } }

val storeOnStaticStackMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.ANY),
    output = TYPE.ANY,
    precedence = Precedence.HIGHESTER
) { v: Any ->
    StaticStorageHelper.storeStack(v)
    v
}