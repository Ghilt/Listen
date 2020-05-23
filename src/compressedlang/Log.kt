package compressedlang

import compressedlang.fncs.Function
import compressedlang.fncs.ResolvedFunction

fun log(message: String){
    println(message)
}

fun log(
    funcs: List<Function>,
    indexOfFunc: Int,
    output: ResolvedFunction,
    consumablePrevious: Any?,
    firstInput: Any?,
    consumeList: List<Any>,
    functionDepth: Int
){
    val func = funcs[indexOfFunc]

    println("Func($functionDepth): ${Du.getDiagnosticsString(func)} " +
            "at i: $indexOfFunc " +
            "in [${funcs.joinToString(" ") { Du.getDiagnosticsString(it) }}] " +
            "outputs ${output.value.typeOfValue()}: ${output.value} as [" +
            (if (consumablePrevious == null) "<$firstInput> " else "$consumablePrevious ") +
            Du.getDiagnosticsString(func) +
            (if (consumeList.isEmpty()) "]" else " ${consumeList.joinToString("") { it.toString() }}]")

    )
}
