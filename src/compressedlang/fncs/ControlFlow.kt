package compressedlang.fncs

const val START_INNER_FUNCTION = "StartInnerFunction"
const val END_INNER_FUNCTION = "EndInnerFunction"

val startInnerFunctionControlFlow = ControlFlow(START_INNER_FUNCTION)
val endInnerFunctionControlFlow = ControlFlow(END_INNER_FUNCTION)

fun Function.isStartInnerFunction() = this is ControlFlow && this.value == START_INNER_FUNCTION

fun Function.isEndInnerFunction() = this is ControlFlow && this.value == END_INNER_FUNCTION