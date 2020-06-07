package compressedlang.fncs

const val START_INNER_FUNCTION = "StartInnerFunction"
const val END_INNER_FUNCTION = "EndInnerFunction"
const val END_OUTER_FUNCTION = "EndOuterFunction"

// TODO Major Refactor -> These do really have no business being functions at all, might remain as a bit ugly but convenient

val startInnerFunctionControlFlow = ControlFlow(START_INNER_FUNCTION)
val endInnerFunctionControlFlow = ControlFlow(END_INNER_FUNCTION)
val endOuterFunction = ControlFlow(END_OUTER_FUNCTION)

fun Function.isStartInnerFunction() = this is ControlFlow && this.value == START_INNER_FUNCTION

fun Function.isEndInnerFunction() = this is ControlFlow && this.value == END_INNER_FUNCTION

fun Function.isEnOuterFunction() = this is ControlFlow && this.value == END_OUTER_FUNCTION