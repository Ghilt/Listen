package compressedlang

import compressedlang.fncs.ResolvedFunction

class CalculatedValuesOfContext(val list: List<List<ResolvedFunction>>){
    fun conformToDyad(): List<Any> = list.map { it.first().value }
}