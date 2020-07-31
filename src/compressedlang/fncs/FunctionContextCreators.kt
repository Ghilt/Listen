package compressedlang.fncs

import collectionlib.extendEntries
import collectionlib.filterSectioned
import collectionlib.filterWithNeighbors
import compressedlang.TYPE

/* MONADS */

val pipeMonad = ContextMonad(
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
) { data: List<Any>, _ -> data }

val chunkMonad = ContextMonad(
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
    defaultConfigurationValues = listOf(3)
) { data: List<Any>, cv -> data.chunked(size = cv[0]) }

val windowMonad = ContextMonad(
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
    defaultConfigurationValues = listOf(3, 1, false)
) { data: List<Any>, cv -> data.windowed(size = cv[0], step = cv[1], partialWindows = cv.getBool(2)) }


/* DYADS */

val createListOfValueDyad = ContextDyad(
    inputs = listOf(TYPE.LIST_TYPE, TYPE.ANY),
    output = TYPE.LIST_TYPE,
) { _: List<Any>, preCalc: List<Any>, _ -> listOf(preCalc.first()) }

val filterDyad = ContextDyad(
    inputs = listOf(TYPE.LIST_TYPE, TYPE.BOOL),
    output = TYPE.LIST_TYPE,
) { data: List<Any>, preCalc: List<Boolean>, _ -> data.filterIndexed { i, _ -> preCalc[i] } }

val filterSectionedDyad = ContextDyad(
    inputs = listOf(TYPE.LIST_TYPE, TYPE.BOOL),
    output = TYPE.LIST_TYPE,
) { data: List<Any>, preCalc: List<Boolean>, _ ->
    data
        .withIndex() // This messes it up a bit, leads to the double map at the end
        .toList()
        .filterSectioned { wi -> preCalc[wi.index] }
        .map { it.map { indexed -> indexed.value } }
}

val filterWithNeighborsDyad = ContextDyad(
    defaultConfigurationValues = listOf(1, 1),
    inputs = listOf(TYPE.LIST_TYPE, TYPE.BOOL),
    output = TYPE.LIST_TYPE,
) { data: List<Any>, preCalc: List<Boolean>, cv: ConfigValues ->
    data
        .withIndex() // This messes it up a bit, leads to the double map at the end
        .toList()
        .filterWithNeighbors(cv[0], cv[1]) { wi -> preCalc[wi.index] }
        .map { indexed -> indexed.value }
}

val mapDyad = ContextDyad(
    inputs = listOf(TYPE.LIST_TYPE, TYPE.ANY),
    output = TYPE.LIST_TYPE,
) { data: List<Any>, preCalc: List<Any>, _ -> data.mapIndexed { i, _ -> preCalc[i] } }

val flatMapDyad = ContextDyad(
    inputs = listOf(TYPE.LIST_TYPE, TYPE.LIST_TYPE),
    output = TYPE.LIST_TYPE,
) { data: List<Any>, preCalc: List<List<Any>>, _ -> data.withIndex().flatMap { (i, _) -> preCalc[i] } }

val extendEntriesDyad = ContextDyad(
    inputs = listOf(TYPE.LIST_TYPE, TYPE.NUMBER),
    output = TYPE.LIST_TYPE,
) { data: List<Any>, preCalc: List<Int>, _ -> data.extendEntries(preCalc) }

val zipInsertionDyad = ContextDyad(
    inputs = listOf(TYPE.LIST_TYPE, TYPE.ANY),
    output = TYPE.LIST_TYPE,
) { data: List<Any>, preCalc: List<Any>, _ -> data.zip(preCalc).map { listOf(it.first, it.second) }  }
