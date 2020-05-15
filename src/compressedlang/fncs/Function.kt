@file:Suppress("UNCHECKED_CAST")

package compressedlang.fncs

import compressedlang.*
import compressedlang.ContextKey.CURRENT_LIST
import compressedlang.Precedence.*
import compressedlang.TYPE.LIST_TYPE

sealed class Function(
    val createsContext: Boolean = false, // TODO likely remove
) {
    abstract val defaultImplicitInput: Nilad
    abstract val inputs: List<TYPE>
    abstract val output: TYPE
    abstract val precedence: Precedence

    fun isExecutable() = this !is ResolvedFunction && this !is ContextFunction
    fun isResolved() = this is ResolvedFunction

    abstract fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ): ResolvedFunction
}

data class ResolvedFunction(
    val value: Any,
) : Function() {

    override val defaultImplicitInput: Nilad
        get() = throw DeveloperError("ResolvedFunction: Not supported $value")
    override val inputs: List<TYPE>
        get() = listOf()
    override val output = value.typeOfValue()
    override val precedence: Precedence
        get() = LOWEST

    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ) = this

    val isNoOperation: Boolean = value == ContextKey.NOP

}

data class Nilad(
    val contextKey: ContextKey,
    override val output: TYPE,
    private val outputType: (TYPE) -> TYPE = { output },
) : Function() {

    override val defaultImplicitInput: Nilad
        get() = this
    override val inputs: List<TYPE>
        get() = listOf()
    override val precedence: Precedence
        get() = HIGHEST

    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ): ResolvedFunction {
        return ResolvedFunction(environmentHook(contextKey, listOf()))
    }
}

data class Monad<I : Any, O : Any>(
    override val precedence: Precedence = LOWEST,
    override val defaultImplicitInput: Nilad,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    val contextKey: ContextKey? = null,
    val f: (I) -> O
) : Function() {

    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ): ResolvedFunction {
        val input = if (contextKey != null) {
            environmentHook(contextKey, values)
        } else {
            values[0]
        }

        val value = f(input as I)
        return ResolvedFunction(value)
    }
}

data class Dyad<I : Any, I2 : Any, O : Any>(
    val createContext: Boolean = false,
    override val precedence: Precedence = LOWEST,
    override val defaultImplicitInput: Nilad,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    private val outputType: (TYPE, TYPE) -> TYPE = { _, _ -> output },
    private val f: (I, I2) -> O
) : Function(createContext) {
    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ): ResolvedFunction {
        // TODO support environmentHook
        val value = f(values[0] as I, values[1] as I2)
        return ResolvedFunction(value)
    }
}

abstract class ContextFunction(
    createContext: Boolean = true,
    override val precedence: Precedence = LOWEST,
    override val defaultImplicitInput: Nilad = valueThenCurrentListNilad
) : Function(createContext) {
    abstract fun executeFromContext(a: List<Any>, inputFromContext: CalculatedValuesOfContext): List<Any>
}

data class ContextMonad<I : Any>(
    override val inputs: List<TYPE>,
    override val output: TYPE,
    private val f: (List<I>) -> List<Any>
) : ContextFunction() {
    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ) = throw DeveloperError("Executing context function not supported")

    override fun executeFromContext(a: List<Any>, inputFromContext: CalculatedValuesOfContext): List<Any> {
        return f(a as List<I>)
    }
}

data class ContextDyad<I : Any, I2 : Any>(
    override val inputs: List<TYPE>,
    override val output: TYPE,
    private val f: (List<I>, List<I2>) -> List<Any>
) : ContextFunction() {
    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ) = throw DeveloperError("Executing context function not supported")

    override fun executeFromContext(a: List<Any>, inputFromContext: CalculatedValuesOfContext): List<Any> {
        return f(a as List<I>, inputFromContext.conformToDyad() as List<I2>)
    }
}

data class InnerFunction(
    val index: Int // TODO remove, inner functions should not need to keep track of their own index in a program
) : Function() {
    override val defaultImplicitInput: Nilad
        get() = throw DeveloperError("InnerFunction: Not supported $index")
    override val inputs: List<TYPE>
        get() = listOf(LIST_TYPE)
    override val output: TYPE
        get() = LIST_TYPE
    override val precedence: Precedence
        get() = LOW

    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ) = throw DeveloperError("Executing inner function not supported")
}

fun Function.usesNewContext(): Boolean {
    return this is Nilad && contextKey == CURRENT_LIST
}

fun Number.toResolvedFunction() = ResolvedFunction(this)
