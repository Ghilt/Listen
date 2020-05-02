package compressedlang

import compressedlang.fncs.ResolvedFunction

class CalculatedValuesOfContext(val list: List<List<ResolvedFunction>>){
    fun conformToDyad(): List<Any> = list.map { it.first().actualValue }
    fun calculateSingleType(): TYPE = list.firstOrNull()?.firstOrNull()?.output ?: TYPE.ANY
}