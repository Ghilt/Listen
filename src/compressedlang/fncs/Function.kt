@file:Suppress("UNCHECKED_CAST")

package compressedlang.fncs

import compressedlang.*
import compressedlang.ContextKey.CURRENT_LIST
import compressedlang.Precedence.*
import compressedlang.TYPE.LIST_TYPE
import compressedlang.TYPE.NUMBER

sealed class Function(
    val consumesList: Boolean = false,
) {
    abstract val defaultImplicitInput: Nilad
    abstract val inputs: List<TYPE>
    abstract val output: TYPE
    abstract val precedence: Precedence

    fun isExecutable() = this !is ResolvedFunction
    fun isResolved() = this is ResolvedFunction

    abstract fun exec(
        values: List<Du81value<Any>>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Du81value<Any>>) -> Du81value<Any>
    ): ResolvedFunction
}

data class ResolvedFunction(
    val value: Du81value<Any>,
) : Function() {

    constructor(value: Any, type: TYPE) : this(Du81value(type, value))

    val actualValue = value.value

    override val defaultImplicitInput: Nilad
        get() = throw DeveloperError("ResolvedFunction: Not supported $value")
    override val inputs: List<TYPE>
        get() = listOf()
    override val output = value.type
    override val precedence: Precedence
        get() = LOWEST

    override fun exec(
        values: List<Du81value<Any>>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Du81value<Any>>) -> Du81value<Any>
    ) = this
}

data class Nilad(
    val contextKey: ContextKey,
    override val output: TYPE,
    private val outputType: (TYPE) -> TYPE = { it },
) : Function() {

    override val defaultImplicitInput: Nilad
        get() = this
    override val inputs: List<TYPE>
        get() = listOf()
    override val precedence: Precedence
        get() = HIGHEST

    override fun exec(
        values: List<Du81value<Any>>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Du81value<Any>>) -> Du81value<Any>
    ): ResolvedFunction {
        return ResolvedFunction(environmentHook(contextKey, listOf()))
    }
}

data class Monad<I : Any, O : Any>(
    override val precedence: Precedence = LOWEST,
    override val defaultImplicitInput: Nilad,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    private val outputType: (TYPE) -> TYPE = { it },
    val contextKey: ContextKey? = null,
    val f: (I) -> O
) : Function() {

    override fun exec(
        values: List<Du81value<Any>>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Du81value<Any>>) -> Du81value<Any>
    ): ResolvedFunction {

        val input = if (contextKey != null) {
            environmentHook(contextKey, values)
        } else {
            values[0]
        }

        val value = f(input.value as I)
        val type = outputType(input.type)
        return ResolvedFunction(value, type)
    }
}

data class Dyad<I : Any, I2 : Any, O : Any>(
    val createContext: Boolean = false,
    override val precedence: Precedence = LOWEST,
    override val defaultImplicitInput: Nilad,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    private val outputType: (TYPE, TYPE) -> TYPE = { v1, _ -> v1 },
    private val f: (I, I2) -> O
) : Function(createContext) {
    override fun exec(
        values: List<Du81value<Any>>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Du81value<Any>>) -> Du81value<Any>
    ): ResolvedFunction {
        val value = f(values[0].value as I, values[1].value as I2)
        val type = outputType(values[0].type, values[1].type)
        return ResolvedFunction(value, type)
    }
}

data class ContextDyad<I : Any, I2 : Any>(
    val createContext: Boolean = false,
    override val precedence: Precedence = LOWEST,
    override val defaultImplicitInput: Nilad,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    private val outputType: (TYPE, TYPE) -> TYPE,
    private val f: (List<I>, List<I2>) -> List<Any>
) : Function(createContext) {
    override fun exec(
        values: List<Du81value<Any>>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Du81value<Any>>) -> Du81value<Any>
    ) = throw DeveloperError("Executing context function not supported")

    fun executeFromContext(a: Du81List, b: CalculatedValuesOfContext): Du81List {
        val newType = outputType(a.innerType, b.calculateSingleType())
        return f(a.list as List<I>, b.conformToDyad() as List<I2>).toDu81List(newType)
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
        values: List<Du81value<Any>>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Du81value<Any>>) -> Du81value<Any>
    ) = throw DeveloperError("Executing inner function not supported")
}

fun Function.usesNewContext(): Boolean {
    return this is Nilad && contextKey == CURRENT_LIST
}

fun Number.toResolvedFunction() = ResolvedFunction(this, NUMBER)
