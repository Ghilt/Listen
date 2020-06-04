package compressedlang

import compressedlang.fncs.*
import compressedlang.fncs.Function

class FunctionContext(
    private val targets: List<List<Any>>,
    private val functionDepth: Int = 0
) {

    private val canAcceptContextCreator: Boolean
        get() = contextCreator == null
    private val isInnerFunction: Boolean
        get() = functionDepth != 0

    private val contextLessElements: MutableList<Function> = mutableListOf()
    private var contextCreator: ContextFunction? = null
    private val elements: MutableList<Function> = mutableListOf()
    private val functions: MutableList<FunctionContext> = mutableListOf()
    internal var isBuilt = false

    fun put(function: Function) {
        if (!willAccept(function)) {
            throw DeveloperError("Adding unacceptable function to function context: $function")
        }

        when {
            functions.isNotEmpty() && functions[0].willAccept(function) -> {
                functions[0].put(function)
            }
            function.isStartInnerFunction() -> {
                if (contextCreator == null) {
                    contextLessElements.add(InnerFunction(functions.size))
                } else {
                    elements.add(InnerFunction(functions.size))
                }
                functions.add(0, FunctionContext(targets, functionDepth + 1))
            }
            function.isEndInnerFunction() -> {
                if (!isInnerFunction) throw SyntaxError("Cannot find an inner function to end")
                build()
            }
            contextCreator == null && function is ContextFunction -> contextCreator = function
            contextCreator == null -> contextLessElements.add(function)
            else -> {
                elements.add(function)
            }
        }
    }

    fun willAccept(function: Function): Boolean {
        return when {
            isInnerFunction && isBuilt -> false
            function.createsContext && !willAcceptContextCreator() -> false
            else -> true
        }
    }

    private fun willAcceptContextCreator(): Boolean = canAcceptContextCreator ||
            (elements.isNotEmpty() && elements.last() is InnerFunction) &&
            functions[0].willAcceptContextCreator() && !functions[0].isBuilt

    fun build() {
        functions.forEach { it.build() } // TODO remove
        isBuilt = true
    }

    private fun getViewOfFunctionsContext(): List<Function> {
        // This is glue pandering to legacy TypeRequirement implementation which i can't bother to change atm
        val creator = contextCreator
        if (creator == null && elements.isNotEmpty()) throw DeveloperError("Something is wrong with this function, context creator is null but has elements depending on context: $elements")
        val maybeContextCreator = mutableListOf<Function>().apply { if (creator != null) add(creator) }
        return listOf(currentListNilad) + maybeContextCreator + elements
    }

    private fun getViewOfWholeContext(): List<Function> {
        val creator = contextCreator
        if (creator == null && elements.isNotEmpty()) throw DeveloperError("Something is wrong with this function, context creator is null but has elements depending on context: $elements")
        val maybeContextCreator = mutableListOf<Function>().apply { if (creator != null) add(creator) }
        return contextLessElements + maybeContextCreator + elements
    }

    fun diagnosticsString(): String {
        return getViewOfWholeContext().joinToString("") {
            when (it) {
                is InnerFunction -> "(${functions[functions.size - 1 - it.index].diagnosticsString()})"
                else -> Du81ProgramEnvironment.getDiagnosticsString(it)
            }
        }
    }

    fun execute(outerFunctionIndexOfData: Int): ResolvedFunction {
        // TODO refactor-extract fun on this little duplicated loop thing
        var contextLessCommands = contextLessElements.toList()
        var indexOfContextLessFunc: Int? = -1
        while (indexOfContextLessFunc != null) {
            indexOfContextLessFunc = contextLessCommands.getIndexOfNextExecution()
            if (indexOfContextLessFunc != null) contextLessCommands = executeAt(contextLessCommands, indexOfContextLessFunc, targets[0], outerFunctionIndexOfData)
        }

        val resolvedContextLess = contextLessCommands[0] as ResolvedFunction

        if (contextCreator == null) {
            return resolvedContextLess
        }

        val listToOperateOn: List<Any> = if (resolvedContextLess.output != TYPE.LIST_TYPE) {
            // the contextLess command returns a single value, wrap it in a list to let the creator operate on that
            listOf(resolvedContextLess.value)
        } else {
            @Suppress("UNCHECKED_CAST")
            resolvedContextLess.value as List<Any>
        }

        val contextInputSize = (contextCreator?.inputs?.size ?: 0) - 1

        val valuesProvidedByContext = mutableListOf<List<ResolvedFunction>>()
        for (indexOfData in listToOperateOn.indices) {
            var commands = elements.toList()
            var indexOfFunc: Int? = -1
            while (indexOfFunc != null) {
                indexOfFunc = commands.getIndexOfNextExecution()
                if (indexOfFunc != null) commands = executeAt(commands, indexOfFunc, listToOperateOn, indexOfData)
            }

            if (commands.any { !it.isResolved() }) throw DeveloperError("Unresolved function ${commands.joinToString()}")
            if (commands.size != contextInputSize) throw SyntaxError("Disallowed resolution of tokens: ${commands.joinToString()}")

            valuesProvidedByContext.add(commands.map { (it as ResolvedFunction) })
        }
        val finishedCalculations = CalculatedValuesOfContext(valuesProvidedByContext)

        // TODO Temporary
        return (contextCreator as ContextFunction).let { dyad ->
            val result: List<Any> = dyad.executeFromContext(listToOperateOn, finishedCalculations)
            val postProcessed = processResultList(result)
            ResolvedFunction(postProcessed)
        }
    }

    private fun processResultList(result: List<Any>): List<Any> {
        // Since the functions operate on double, they also convert to doubles
        // This post process step is to return to the integer domain if possible
        return if (result.isNotEmpty() && result.all { it.isDoubleButCouldBeRepresentedByInt() }) {
            result.map { (it as Double).toInt() }
        } else {
            result
        }
    }

    private fun processFunctionResult(result: ResolvedFunction): ResolvedFunction {
        // Since the functions operate on double, they also convert to doubles
        // This post process step is to return to the integer domain if possible
        return if (result.output == TYPE.NUMBER && result.value.isDoubleButCouldBeRepresentedByInt() ) {
            ResolvedFunction((result.value as Double).toInt())
        } else {
            result
        }
    }

    private fun Any.isDoubleButCouldBeRepresentedByInt() = this is Double && this % 1 == 0.0

    private fun getContextValueProducer(
        data: List<Any>,
        index: Int,
        requiredType: TYPE?
    ): (contextKey: ContextKey, contextValues: List<Any>) -> Any {
        return { contextKey: ContextKey, contextValues: List<Any> ->
            when (contextKey) {
                ContextKey.CURRENT_LIST -> data
                ContextKey.LIST_BY_INDEX -> {
                    val indexOfRequestedList = contextValues[0] as Int
                    if (indexOfRequestedList >= targets.size) throw Du81AttemptingToFetchNonExistingListError(targets.size, indexOfRequestedList)
                    targets[indexOfRequestedList]
                }
                ContextKey.LENGTH -> data.size
                ContextKey.VALUE_THEN_INDEX -> if (data.typeOfList().isSubtypeOf(requiredType)) data[index] else index
                ContextKey.VALUE -> data[index]
                ContextKey.INDEX -> index
                ContextKey.CONSTANT_0 -> 0
                ContextKey.VALUE_THEN_CURRENT_LIST -> if (data.typeOfList().isSubtypeOf(TYPE.LIST_TYPE)) data[index] else data
                ContextKey.NOP -> ContextKey.NOP // This is treated specially down the line
                ContextKey.CURRENT_LIST_COUNT -> targets.size
            }
        }
    }

    private fun executeAt(
        funcs: List<Function>,
        indexOfFunc: Int,
        data: List<Any>,
        indexOfData: Int
    ): List<Function> {

        val function = funcs[indexOfFunc]

        if (function is InnerFunction) {
            // TODO Remove this if supporting functions creating functions
            log("Du81, inner function ready for execution: ${functions[function.index].diagnosticsString()}")
            val innerFuncResult = functions[function.index].execute(indexOfData)
            return reduceByCalculatedFunction(funcs, indexOfFunc, innerFuncResult, null, null, listOf())
        }

        val consumeList = funcs.getInputsForwardOfFunctionAtIndex(indexOfFunc)
            .map {
                when (it) {
                    is ResolvedFunction -> it.value
                    else -> throw DeveloperError("Unresolved function: $it")
                }
            }

        val consumablePrevious = getPreviousIfConsumableByFunctionAtIndex(funcs, indexOfFunc)?.value
        val requiredTypeOfNilad = if (function.inputs.isNotEmpty()) function.inputs[0] else null
        val environmentHook = getContextValueProducer(data, indexOfData, requiredTypeOfNilad)
        val firstInput = consumablePrevious ?: environmentHook(function.defaultImplicitInput.contextKey, consumeList)
        val inputsToFunction = listOf(firstInput) + consumeList

        val output: ResolvedFunction = function.execute(inputsToFunction, environmentHook)

        val postProcessed = processFunctionResult(output)

        return reduceByCalculatedFunction(funcs, indexOfFunc, postProcessed, consumablePrevious, firstInput, consumeList)
    }

    private fun reduceByCalculatedFunction(
        funcs: List<Function>,
        indexOfFunc: Int,
        output: ResolvedFunction,
        consumablePrevious: Any?,
        firstInput: Any?,
        consumeList: List<Any>
    ): List<Function> {
        log(funcs, indexOfFunc, output, consumablePrevious, firstInput, consumeList, functionDepth)

        if (output.isNoOperation) {
            return funcs.filterIndexed { i, _ -> i != indexOfFunc }
        }

        return funcs.mapIndexed { i, f -> if (i == indexOfFunc) output else f }
            .filterIndexed { i, _ ->
                val consumePrevious = i == indexOfFunc - 1 && consumablePrevious != null
                val consumeForward = i in consumeList.indices.map { it + indexOfFunc + 1 }
                !(consumePrevious || consumeForward)
            }
    }

    private fun getPreviousIfConsumableByFunctionAtIndex(funcs: List<Function>, indexOfFunc: Int): ResolvedFunction? {
        if (indexOfFunc > 0) {
            val function = funcs[indexOfFunc]

            if (function is Nilad) {
                return null
            }

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