@file:Suppress("UNCHECKED_CAST")

package compressedlang

import compressedlang.ContextKey.CURRENT_LIST
import compressedlang.Precedence.LOW
import compressedlang.Precedence.LOWEST
import compressedlang.TYPE.LIST_TYPE
import compressedlang.TYPE.NUMBER

sealed class Function(
    val consumesList: Boolean = false,
) {
    abstract val inputs: List<TYPE>
    abstract val output: TYPE
    abstract val precedence: Precedence

    fun isExecutable() = this !is ResolvedFunction
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

data class Monad<I : Any, O : Any>(
    val default: Nilad,
    override val precedence: Precedence = LOWEST,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    val f: (I) -> O
) : Function() {
    fun exec(a: Any) = f(a as I)
}

data class Dyad<I : Any, I2 : Any, O : Any>(
    val default: Nilad,
    val createContext: Boolean = false,
    override val precedence: Precedence = LOWEST,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    private val f: (I, I2) -> O
) : Function(createContext) {
    fun exec(a: Any, b: Any) = f(a as I, b as I2)
}

data class ContextDyad<I : Any, I2 : Any>(
    val default: Nilad,
    val createContext: Boolean = false,
    override val precedence: Precedence = LOWEST,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    private val outputType: (TYPE, TYPE) -> TYPE,
    private val f: (List<I>, List<I2>) -> List<Any>
) : Function(createContext) {
    fun exec(a: Du81List, b: CalculatedValuesOfContext): Du81List {
        val newType = outputType(a.innerType, b.calculateSingleType())
        return f(a.list as List<I>, b.conformToDyad() as List<I2>).toDu81List(newType)
    }
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
