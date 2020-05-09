package compressedlang.fncs

import compressedlang.Precedence
import compressedlang.TYPE

val createListOfValueDyad = ContextDyad(
    createContext = true,
    precedence = Precedence.LOWEST,
    defaultImplicitInput = currentListNilad,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.ANY),
    output = TYPE.LIST_TYPE,
) { _: List<Any>, b: List<Any> -> listOf(b.first())}

val filterDyad = ContextDyad(
    createContext = true,
    precedence = Precedence.LOWEST,
    defaultImplicitInput = currentListNilad,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.BOOL),
    output = TYPE.LIST_TYPE,
) { a: List<Any>, b: List<Boolean> -> a.filterIndexed { i, _ -> b[i] } }

val mapDyad = ContextDyad(
    createContext = true,
    precedence = Precedence.LOWEST,
    defaultImplicitInput = currentListNilad,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.ANY),
    output = TYPE.LIST_TYPE,
) { a: List<Any>, b: List<Any> -> a.mapIndexed { i, _ -> b[i] } }

val flatMapDyad = ContextDyad(
    createContext = true,
    precedence = Precedence.LOWEST,
    defaultImplicitInput = currentListNilad,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
) { a: List<Any>, b: List<List<Any>> -> a.withIndex().flatMap { (i, _) -> b[i] } }