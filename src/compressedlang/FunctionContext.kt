package compressedlang

import compressedlang.fncs.*
import compressedlang.fncs.Function

class FunctionContext(
    private val targets: List<Du81List>,
    firstFunction: Function,
    private val functionDepth: Int = 0
) {

    private val isInnerFunction: Boolean
        get() = functionDepth != 0

    private val elements: MutableList<Function> = mutableListOf(firstFunction)
    private val functions: MutableList<FunctionContext> = mutableListOf()
    internal var isBuilt = false

    private val listProvider
        get() = elements[0] as Nilad

    private val contextCreator
        get() = elements[1]

    fun put(function: Function) {
        if (!willAccept(function)) {
            throw DeveloperError("Adding unacceptable function to function context")
        }

        when {// TODO correctly make inner functions
            functions.isEmpty() && function.usesNewContext() -> {
                elements.add(InnerFunction(functions.size))
                functions.add(0, FunctionContext(targets, function, functionDepth + 1))
            }
            functions.isNotEmpty() && functions[0].willAccept(function) -> {
                functions[0].put(function)
            }
            else -> {
                elements.add(function)
            }
        }
    }

    fun willAccept(function: Function): Boolean {
        return when {
            isInnerFunction && isComplete() -> false
            !function.consumesList -> true
            elements.last().usesNewContext() -> true
            elements.last() is InnerFunction -> true // inner functions produces lists, for now
            isInnerFunction -> throw SyntaxError("Faulty syntax")
            else -> false
        }
    }

    private fun isComplete(): Boolean {

        if (elements.size <= 1) {
            // First element always a list provider which can't complete a context on its own, for now
            // probably extract the list provider to its own variable rather than keeping in list
            return false
        }

        val elementTypeRequirements = TypeRequirements.createFromElements(elements)
        val simplifiedRequirements = elementTypeRequirements.simplifyFully()
        return simplifiedRequirements.areAllFulfilled()
    }

    fun build() {
        isBuilt = true
    }

    fun diagnosticsString(): String {
        return elements.joinToString("") {
            when (it) {
                is InnerFunction -> "(${functions[functions.size - 1 - it.index].diagnosticsString()})"
                else -> Du81ProgramEnvironment.getDiagnosticsString(it)
            }
        }
    }

    fun execute(): Du81List {
        val target: Du81List = produceList(listProvider)

        val valuesProvidedByContext = mutableListOf<List<ResolvedFunction>>()
        val contextInputSize = contextCreator.inputs.size - 1

        for (indexOfData in target.list.indices) {
            var commands = elements.drop(2)
            var indexOfFunc: Int? = -1
            while (indexOfFunc != null) {
                indexOfFunc = commands.getIndexOfNextExecution()
                if (indexOfFunc != null) commands = executeAt(commands, indexOfFunc, target, indexOfData)
            }

            if (commands.any { !it.isResolved() }) throw DeveloperError("Unresolved function ${commands.joinToString()}")
            if (commands.size != contextInputSize) throw SyntaxError("Disallowed resolution of tokens: ${commands.joinToString()}")

            valuesProvidedByContext.add(commands.map { (it as ResolvedFunction) })
        }
        val finishedCalculations = CalculatedValuesOfContext(valuesProvidedByContext)

        // TODO Temporary
        @Suppress("UNCHECKED_CAST")
        return (contextCreator as ContextDyad<*, *>).let { dyad ->
            val result: Du81List = dyad.executeFromContext(target, finishedCalculations)
            val postProcessed = processResultList(result)
            postProcessed
        }
    }

    private fun processResultList(result: Du81List): Du81List {
        // Since the functions operate on double, they also convert to doubles
        // This post process step is to return to the integer domain if possible
        return if (result.list.isNotEmpty() && result.list.all { it.value is Double && it.value % 1 == 0.0 }) {
            Du81List(TYPE.NUMBER, result.list.map { Du81value(it.type, (it.value as Double).toInt()) })
        } else {
            result
        }
    }

    private fun produceList(provider: Nilad): Du81List {
        return when (provider.contextKey) {
            ContextKey.CURRENT_LIST -> targets[0]
            else -> throw DeveloperError("This is not a list producer")
        }
    }

    private fun produceNiladValue(
        provider: Nilad,
        data: Du81List,
        index: Int,
        requiredType: TYPE?
    ): Du81value<Any> {
        return when (provider.contextKey) {
            ContextKey.CURRENT_LIST -> Du81value(TYPE.LIST_TYPE, data)
            ContextKey.LENGTH -> Du81value(TYPE.NUMBER, data.list.size)
            ContextKey.VALUE_THEN_INDEX -> Du81value(
                TYPE.NUMBER,
                if (data.innerType.isSubtypeOf(requiredType)) data[index].value else index
            )
            ContextKey.VALUE -> Du81value(data.innerType, data[index].value)
            ContextKey.INDEX -> Du81value(TYPE.NUMBER, index)
            ContextKey.CONSTANT_0 -> Du81value(TYPE.NUMBER, 0)
        }
    }

    private fun executeAt(
        funcs: List<Function>,
        indexOfFunc: Int,
        data: Du81List,
        indexOfData: Int
    ): List<Function> {

        val function = funcs[indexOfFunc]

        val consumeList = funcs.getInputsForwardOfFunctionAtIndex(indexOfFunc)
            .map {
                when (it) {
                    is ResolvedFunction -> it.value
                    else -> throw DeveloperError("Unresolved function: $it")
                }
            }

        val consumablePrevious = getPreviousIfConsumableByFunctionAtIndex(funcs, indexOfFunc)?.value
        val requiredTypeOfNilad = if (function.inputs.isNotEmpty()) function.inputs[0] else null
        val firstInput = consumablePrevious ?: produceNiladValue(
            function.defaultImplicitInput,
            data,
            indexOfData,
            requiredTypeOfNilad
        )

        val output: ResolvedFunction = function.exec(firstInput, consumeList)

        return funcs.mapIndexed { i, f -> if (i == indexOfFunc) output else f }
            .filterIndexed { i, _ ->
                val consumePrevious = i == indexOfFunc - 1 && consumablePrevious != null
                val consumeForward = i in consumeList.indices.map { it + indexOfFunc + 1 }
                !(consumePrevious || consumeForward)
            }
    }

    private fun getPreviousIfConsumableByFunctionAtIndex(funcs: List<Function>, indexOfFunc: Int, ): ResolvedFunction? {
        if (indexOfFunc > 0) {
            val function = funcs[indexOfFunc]
            val previousFunc = funcs[indexOfFunc - 1]
            if (previousFunc.isResolved() && previousFunc.output.isSubtypeOf(function.inputs[0])) {
                return previousFunc as ResolvedFunction
            }
        }
        return null
    }
}

private fun List<Function>.getIndexOfNextExecution(): Int? {
    for (precedence in Precedence.values().reversed()) {
        val target = this
            .withIndex()
            .filter { it.value.precedence == precedence }
            .firstOrNull { (i, f) -> f.isExecutable() && this.inputsOfFunctionAtIndexAreResolvedValues(i) }
        if (target != null) return target.index
    }
    return null
}

private fun List<Function>.inputsOfFunctionAtIndexAreResolvedValues(index: Int): Boolean {
    return this.getInputsForwardOfFunctionAtIndex(index).all { it.isResolved() }
}

private fun List<Function>.getInputsForwardOfFunctionAtIndex(index: Int): List<Function> {
    // Nilads and Monads do not have any forward inputs
    if (this[index].inputs.size < 2) return listOf()

    val startIndex = index + 1
    val inputsForward = startIndex + this[index].inputs.size - 1
    return this.subList(startIndex, inputsForward)
}


// F>i
// Mi
// F>i*2*3*4
// F>_F=3L=L
// (F>(_F=(_F>0)L)*7L)


// F>iF="hej"F<424.12
// 22022  0  22 0
// Fi>iF="hej"F<424.12
// MiFi
// 2020
// F>i*2*3*4F="hej"F<424.12
// 22020202022   0 22 0
// FF>i*2*3*4F="hej"F<424.12

// TLi12