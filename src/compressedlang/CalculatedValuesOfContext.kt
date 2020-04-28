package compressedlang

class CalculatedValuesOfContext(val list: List<List<ResolvedFunction>>){
    fun conformToDyad(): List<Any> = list.map { it.first().value }
    fun calculateSingleType(): TYPE = list.firstOrNull()?.firstOrNull()?.output ?: TYPE.ANY
}