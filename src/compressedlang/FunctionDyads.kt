package compressedlang

val additionDyad = Dyad<Double, Double, Double>(
    default = indexNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.DOUBLE, TYPE.DOUBLE),
    output = TYPE.DOUBLE
) { a, b -> a + b }

val multiplicationDyad = Dyad<Double, Double, Double>(
    default = indexNilad,
    precedence = Precedence.HIGHEST,
    inputs = listOf(TYPE.DOUBLE, TYPE.DOUBLE),
    output = TYPE.DOUBLE
) { a, b -> a * b }

val largerThanDyad = Dyad<Double, Double, Boolean>(
    default = indexNilad,
    precedence = Precedence.LOWEST,
    inputs = listOf(TYPE.DOUBLE, TYPE.DOUBLE),
    output = TYPE.BOOL,
) { a, b -> a > b }

val smallerThanDyad = Dyad<Double, Double, Boolean>(
    default = indexNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.DOUBLE, TYPE.DOUBLE),
    output = TYPE.BOOL
) { a, b -> a < b }

val equalToDyad = Dyad<Double, Double, Boolean>(
    default = indexNilad,
    precedence = Precedence.MEDIUM,
    inputs = listOf(TYPE.DOUBLE, TYPE.DOUBLE),
    output = TYPE.BOOL
) { a, b -> a == b }

val filterDyad = Dyad<List<Any>, (Any) -> Boolean, List<Any>>(
    currentListNilad,
    dyadConsume = true,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.BOOL),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.LOWEST
) { a, b -> a.filter { b(it) } }