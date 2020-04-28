package compressedlang

val filterDyad = ContextDyad(
    currentListNilad,
    createContext = true,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.BOOL),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.LOWEST,
    outputType = { t1, _ -> t1 }
) { a: List<Any>, b: List<Boolean> -> a.filterIndexed { i, _ -> b[i] } }

val mapDyad = ContextDyad(
    currentListNilad,
    createContext = true,
    inputs = listOf(TYPE.LIST_TYPE, TYPE.ANY),
    output = TYPE.LIST_TYPE,
    precedence = Precedence.LOWEST,
    outputType = { _, t2 -> t2 }
) { a: List<Any>, b: List<Any> -> a.mapIndexed { i, _ -> b[i] } }