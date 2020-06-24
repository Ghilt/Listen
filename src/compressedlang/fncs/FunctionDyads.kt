package compressedlang.fncs

import compressedlang.*

val largerThanDyad = Dyad<Double, Double, Boolean>(
    defaultImplicitInput = valueThenIndexNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.BOOL,
) { a, b -> a > b }

val smallerThanDyad = Dyad<Double, Double, Boolean>(
    defaultImplicitInput = valueThenIndexNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.BOOL,
) { a, b -> a < b }

val equalToDyad = Dyad<Any, Any, Boolean>(
    defaultImplicitInput = valueNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.BOOL,
) { a, b -> a == b }

val andDyad = Dyad<Any, Any, Boolean>(
    defaultImplicitInput = valueNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.BOOL,
) { a, b -> toBool(a) && toBool(b) }

val elementByIndexDyad = Dyad<List<Any>, Int, Any>(
    defaultImplicitInput = valueThenCurrentListNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.ANY),
    output = TYPE.ANY,
) { a, b -> a[b] }