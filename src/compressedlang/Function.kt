package compressedlang

import compressedlang.ContextKey.INDEX
import compressedlang.ContextKey.CURRENT_LIST
import compressedlang.Precedence.*
import compressedlang.TYPE.*

sealed class Function(val consumesList: Boolean = false) {
    abstract val inputs: List<TYPE>
    abstract val output: TYPE
    abstract val precedence: Precedence
}

data class Number(
    val number: kotlin.Number
) : Function() {
    override val inputs: List<TYPE>
        get() = listOf()
    override val output: TYPE
        get() = NUMBER
    override val precedence: Precedence
        get() = LOW
}

data class StringLiteral(
    val literal: String
) : Function() {
    override val inputs: List<TYPE>
        get() = listOf()
    override val output: TYPE
        get() = STRING
    override val precedence: Precedence
        get() = LOW
}

data class Nilad(
    val contextKey: ContextKey,
    override val output: TYPE
) : Function() {
    override val inputs: List<TYPE>
        get() = listOf()
    override val precedence: Precedence
        get() = LOW
}

data class Monad<I, O>(
    val default: Nilad,
    override val precedence: Precedence = LOWEST,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    val f: (I) -> O
) : Function()

data class Dyad<I, I2, O>(
    val default: Nilad,
    val dyadConsume: Boolean = false,
    override val precedence: Precedence = LOWEST,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    val f: (I, I2) -> O
) : Function(dyadConsume)

data class InnerFunction(
    val index: Int
) : Function() {
    override val inputs: List<TYPE>
        get() = listOf(LIST_TYPE)
    override val output: TYPE
        get() = LIST_TYPE
    override val precedence: Precedence
        get() = LOW
}

fun Function.usesNewContext(): Boolean {
    return this is Nilad && contextKey == CURRENT_LIST
}