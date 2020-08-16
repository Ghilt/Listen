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

val notEqualToDyad = Dyad<Any, Any, Boolean>(
    defaultImplicitInput = valueNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.BOOL,
) { a, b -> a != b }

val andDyad = Dyad<Any, Any, Boolean>(
    defaultImplicitInput = valueNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.BOOL,
) { a, b -> toBool(a) && toBool(b) }

val orDyad = Dyad<Any, Any, Boolean>(
    defaultImplicitInput = valueNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.BOOL,
) { a, b -> toBool(a) || toBool(b) }

val elementByIndexDyad = Dyad<List<Any>, Int, Any>(
    defaultImplicitInput = valueThenCurrentListNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.NUMBER),
    output = TYPE.ANY,
) { a, b -> a[b] }

val appendToStringDyad = Dyad<Any, Any, String>(
    defaultImplicitInput = valueNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.STRING,
) { a, b -> a.toString() + b.toString() }

val zipDyad = Dyad<List<Any>, List<Any>, List<Any>>(
    defaultImplicitInput = currentListNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
) { a, b -> a.zip(b).map { listOf(it.first, it.second) } }

val alphabetGenerationDyad = Dyad(
    defaultImplicitInput = constantZeroNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.LIST_TYPE,
) { whichAlphabet: Int, length: Int ->
    val alphabet = alphabets[whichAlphabet]
    alphabet.repeat(1 + length / alphabet.length).take(length).toList()
}

val oeisGenerationDyad = Dyad(
    defaultImplicitInput = constantZeroNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.LIST_TYPE,
) { sequenceId: Int, length: Int ->
    val sequence = readOeisSequence(sequenceId)
    if (length > sequence.size) {
        throw IllegalArgumentException("Length($length) requested of oeis sequence $sequenceId requested is longer than available data(${sequence.size}). For more info visit https://oeis.org, It is a great resource.")
    }
    sequence.take(length)
}

val takeDyad = Dyad<List<Any>, Int, List<Any>>(
    defaultImplicitInput = valueThenCurrentListNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.NUMBER),
    output = TYPE.LIST_TYPE,
) { a, b -> a.take(b) }

val dropDyad = Dyad<List<Any>, Int, List<Any>>(
    defaultImplicitInput = valueThenCurrentListNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.NUMBER),
    output = TYPE.LIST_TYPE,
) { a, b -> a.drop(b) }

val joinToStringDyad = Dyad<List<Any>, String, String>(
    defaultImplicitInput = valueThenCurrentListNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.STRING),
    output = TYPE.STRING,
) { a, b -> a.joinToString(b) }

val obliterateDyad = Dyad<Any, Any, Any>(
    defaultImplicitInput = valueThenCurrentListNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.ANY,
) { _, b -> b }

val appendToListDyad = Dyad<Any, Any, List<Any>>(
    defaultImplicitInput = valueNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.LIST_TYPE,
) { a, b ->
    when (a) {
        is List<*> -> a.filterNotNull() + listOf(b)
        else -> listOf(a) + listOf(b)
    }
}

val appendListDyad = Dyad<List<Any>, List<Any>, List<Any>>(
    defaultImplicitInput = valueNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.LIST_TYPE,
) { a, b ->
    a + b
}

val storeOnStaticMapDyad = Dyad(
    defaultImplicitInput = valueNilad,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.ANY,
    precedence = Precedence.HIGHESTER
) { value: Any, key: Any ->
    StaticStorageHelper.storeMap(key, value)
}