@file:Suppress("UNCHECKED_CAST")

package compressedlang

import compressedlang.ContextKey.CURRENT_LIST
import compressedlang.Precedence.LOW
import compressedlang.Precedence.LOWEST
import compressedlang.TYPE.*

sealed class Function(val consumesList: Boolean = false) {
    abstract val inputs: List<TYPE>
    abstract val output: TYPE
    abstract val precedence: Precedence

    fun isExecutable() = inputs.isNotEmpty()
    fun isResolved() = this is ResolvedFunction
}

data class ResolvedFunction(
    val value: Any,
    override val output: TYPE
) : Function() {
    override val inputs: List<TYPE>
        get() = listOf()
    override val precedence: Precedence
        get() = LOWEST
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

data class Monad<I: Any, O: Any>(
    val default: Nilad,
    override val precedence: Precedence = LOWEST,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    val f: (I) -> O
) : Function() {
    fun exec(a: Any) = f(a as I)
}

data class Dyad<I: Any, I2: Any, O: Any>(
    val default: Nilad,
    val dyadConsume: Boolean = false,
    override val precedence: Precedence = LOWEST,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    private val f: (I, I2) -> O
) : Function(dyadConsume) {
    fun exec(a: Any, b: Any) = f(a as I, b as I2)
}

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

fun Number.toResolvedFunction() = ResolvedFunction(this, NUMBER)
