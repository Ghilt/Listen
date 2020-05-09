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
    consumablePrevious: Du81value<Any>?,
    consumeList: List<Du81value<Any>>
){
    val func = funcs[indexOfFunc]

    println("Func: ${Du.getDiagnosticsString(func)} " +
            "at i: $indexOfFunc " +
            "in [${funcs.joinToString(" ") { Du.getDiagnosticsString(it) }}] " +
            "outputs ${output.value.type}: ${output.value.value} as [" +
            (if (consumablePrevious == null) "impl " else "${consumablePrevious.value} ") +
            Du.getDiagnosticsString(func) +
            (if (consumeList.isEmpty()) "]" else " ${consumeList.joinToString("") { it.value.toString() }}]")

    )
}
