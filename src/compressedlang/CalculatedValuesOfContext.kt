package compressedlang

import compressedlang.fncs.ResolvedFunction

class CalculatedValuesOfContext(val list: List<List<ResolvedFunction>>){
    // TODO revert this and make contextCreators more robust
    fun conformToDyad(): List<Any> = list.map { it.firstOrNull()?.value ?: listOf<Any>() }
}