@file:Suppress("UNCHECKED_CAST")

package compressedlang.fncs

import compressedlang.*
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

    fun execute(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ): ResolvedFunction {
        try {
            return exec(values, environmentHook)
        } catch (e: java.lang.ClassCastException) {
            throw createSyntaxError(e, this, values)
        }
    }

    abstract fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ): ResolvedFunction
}

class ControlFlow(
    val value: Any,
) : Function(false) {

    override val defaultImplicitInput: Nilad
        get() = throw DeveloperError("ControlFlow: Not supported $value")
    override val inputs: List<TYPE>
        get() = throw DeveloperError("ControlFlow: Not supported $value")
    override val output
        get() = throw DeveloperError("ControlFlow: Not supported $value")
    override val precedence: Precedence
        get() = throw DeveloperError("ControlFlow: Not supported $value")

    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ): ResolvedFunction = throw DeveloperError("ControlFlow: Not supported $value")

}

class ResolvedFunction(
    val value: Any,
) : Function() {

    val type = value.typeOfValue()

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

class Nilad(
    private val contextKey: ContextKey,
    override val output: TYPE,
    private val contextValues: List<Any> = listOf()
) : Function() {

    override val defaultImplicitInput: Nilad
        get() = no_opNilad
    override val inputs: List<TYPE>
        get() = listOf()
    override val precedence: Precedence
        get() = HIGHEST

    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ): ResolvedFunction {
        return ResolvedFunction(environmentHook(contextKey, contextValues))
    }
}

class Monad<I : Any, O : Any>(
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

open class Dyad<I : Any, I2 : Any, O : Any>(
    createContext: Boolean = false,
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
        // TODO support environmentHook as monad does
        val value = f(values[0] as I, values[1] as I2)
        return ResolvedFunction(value)
    }
}

abstract class ContextFunction(
    createContext: Boolean = true,
    override val precedence: Precedence = LOWEST,
    override val defaultImplicitInput: Nilad = valueThenCurrentListNilad,
    open val defaultConfigurationValues: List<Any> = listOf()
) : Function(createContext) {
    fun executeFromContext(inputFromContext: CalculatedValuesOfContext): List<Any> {
        try {
            inputFromContext.injectDefaultConfigValues(defaultConfigurationValues)
            return execFromContext(inputFromContext.listToOperateOn, inputFromContext)
        } catch (e: java.lang.ClassCastException) {
            throw createSyntaxError(e, this, inputFromContext.listToOperateOn)
        }
    }

    abstract fun execFromContext(values: List<Any>, inputFromContext: CalculatedValuesOfContext): List<Any>
}

class ContextMonad<I : Any>(
    override val inputs: List<TYPE>,
    override val output: TYPE,
    override val defaultConfigurationValues: List<Any> = listOf(),
    private val f: (List<I>, configValues: ConfigValues) -> List<Any>
) : ContextFunction() {
    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ) = throw DeveloperError("Executing context function not supported. This has an ugliness value of infinite.")

    override fun execFromContext(values: List<Any>, inputFromContext: CalculatedValuesOfContext): List<Any> {
        return f(values as List<I>, inputFromContext.getConfigValues())
    }
}

class ContextDyad<I : Any, I2 : Any>(
    override val inputs: List<TYPE>,
    override val output: TYPE,
    override val defaultConfigurationValues: List<Any> = listOf(),
    private val f: (List<I>, List<I2>) -> List<Any>
) : ContextFunction() {
    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ) = throw DeveloperError("Executing context function not supported")

    override fun execFromContext(values: List<Any>, inputFromContext: CalculatedValuesOfContext): List<Any> {
        return f(values as List<I>, inputFromContext.conformToDyad() as List<I2>)
    }
}

class InnerFunction(
    val index: Int // TODO remove, inner functions should not need to keep track of their own index in a program
) : Function() {
    override val defaultImplicitInput: Nilad
        get() = throw DeveloperError("InnerFunction: Not supported $index")
    override val inputs: List<TYPE>
        get() = listOf(LIST_TYPE)
    override val output: TYPE
        get() = LIST_TYPE
    override val precedence: Precedence
        get() = HIGHESTER

    override fun exec(
        values: List<Any>,
        environmentHook: (contextKey: ContextKey, contextValues: List<Any>) -> Any
    ) = throw DeveloperError("Executing inner function not supported")
}

fun Number.toResolvedFunction() = ResolvedFunction(this)

fun createSyntaxError(exception: Exception, f: Function, values: List<Any>): SyntaxError {

    return SyntaxError(
        "Error with [ ${Du81ProgramEnvironment.getDiagnosticsString(f)} ] function, input arguments not matching its requirements. " +
                "\nRequired: ${f.inputs} but got: $values\n" +
                "\n$exception\n\n${exception.stackTrace.joinToString("\n")}"
    )
}