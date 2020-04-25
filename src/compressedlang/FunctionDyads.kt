package compressedlang

val additionDyad = Dyad<Double, Double, Double>(
    default = valueThenIndexNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.NUMBER
) { a, b -> a + b }

val subtractionDyad = Dyad<Double, Double, Double>(
    default = constantZeroNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.NUMBER
) { a, b -> a - b }

val multiplicationDyad = Dyad<Double, Double, Double>(
    default = valueThenIndexNilad,
    precedence = Precedence.HIGHEST,
    inputs = listOf(TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.NUMBER
) { a, b -> a * b }

val largerThanDyad = Dyad<Double, Double, Boolean>(
    default = valueThenIndexNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.BOOL,
) { a, b -> a > b }

val smallerThanDyad = Dyad<Double, Double, Boolean>(
    default = valueThenIndexNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.BOOL
) { a, b -> a < b }

val equalToDyad = Dyad<Any, Any, Boolean>(
    default = valueNilad,
    precedence = Precedence.LOW,
    inputs = listOf(TYPE.ANY, TYPE.ANY),
    output = TYPE.BOOL
) { a, b -> a == b }

val filterDyad = Dyad<List<Any>, List<List<Boolean>>, List<Any>>(
    currentListNilad,
    dyadConsume = true,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.BOOL),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.LOWEST
) { a, b -> a.filterIndexed { i, _ -> b[i][0] } }

val mapDyad = Dyad<List<Any>, List<List<Any>>, List<Any>>(
    currentListNilad,
    dyadConsume = true,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.ANY),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.LOWEST
) { a, b -> a.mapIndexed { i, _ -> b[i][0] } }