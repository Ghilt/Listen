package compressedlang

import compressedlang.fncs.ResolvedFunction

class CalculatedValuesOfContext(
    val listToOperateOn: List<Any>,
    val configValuesForFunction: List<ResolvedFunction>,
    val calculatedValuesOfContext: List<List<ResolvedFunction>>
){
    fun conformToDyad(): List<Any> = calculatedValuesOfContext.map { it.first().value }
}