package compressedlang.fncs

import compressedlang.ContextKey
import compressedlang.Precedence
import compressedlang.TYPE

// this could be thought of as a nilad
val notMonad = Monad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.ANY),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHESTER
) { value: Any -> if (toBool(value)) 0 else 1 }

val lengthMonad = Monad(
    defaultImplicitInput = currentListNilad,
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
    defaultImplicitInput = currentListNilad,
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHEST
) { list: List<Double> -> list.reduce { e0, e1 -> e0 + e1 } }

val reverseListMonad = Monad(
    defaultImplicitInput = currentListNilad,
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.HIGHEST
) { list: List<Double> -> list.reversed() }