package compressedlang

class FunctionContext(
    private val targets: List<Du81List<*>>,
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

    fun execute(): Du81List<*> {
        val target = produceList(listProvider)

        val result = mutableListOf<List<Any>>()
        val contextInputSize = contextCreator.inputs.size - 1

        for (indexOfData in target.list.indices) {
            var commands = elements.drop(2)
            while (commands.size > contextInputSize) {
                val indexOfFunc = commands.getIndexOfNextExecution()
                commands = executeAt(commands, indexOfFunc, target, indexOfData)
            }

            if (commands.any { !it.isResolved() }) {
                throw DeveloperError("Unresolved function ${commands.joinToString()}")
            }

            result.add(commands.map { (it as ResolvedFunction).value })
        }

        // TODO Temporary
        @Suppress("UNCHECKED_CAST")
        return (contextCreator as Dyad<*, *, List<Any>>).let {
            val r: List<Any> = it.exec(target.list, result)
            r.toListDu81List(contextCreator.output)
        }
    }

    private fun produceList(provider: Nilad): Du81List<*> {
        return when (provider.contextKey) {
            ContextKey.CURRENT_LIST -> targets[0]
            else -> throw DeveloperError("This is not a list producer")
        }
    }

    private fun produceNiladValue(provider: Nilad, data: Du81List<*>, index: Int, type: TYPE): Any {
        return when (provider.contextKey) {
            ContextKey.CURRENT_LIST -> data
            ContextKey.LENGTH -> data.list.size
            ContextKey.VALUE_THEN_INDEX -> if (type.isSatisfiedBy(data.type)) data[index] else index
            ContextKey.VALUE -> data[index]
            ContextKey.INDEX -> index
            ContextKey.CONSTANT_0 -> 0
        }
    }

    private fun executeAt(
        funcs: List<Function>,
        indexOfFunc: Int,
        data: Du81List<*>,
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

        val consumablePrevious = getPreviousIfConsumableByFunctionAtIndex(funcs, indexOfFunc)

        val output = when (function) {
            is Nilad -> produceNiladValue(function, data, indexOfData, function.output)
            is Monad<*, *> -> function.exec(consumablePrevious ?: produceNiladValue(function.default, data, indexOfData, function.inputs[0]))
            is Dyad<*, *, *> -> function.exec(consumablePrevious ?: produceNiladValue(function.default, data, indexOfData, function.inputs[0]), consumeList[0])
            else -> throw DeveloperError("Non executable: This function should be called safely")
        }

        return funcs.mapIndexed { i, f -> if (i == indexOfFunc) ResolvedFunction(output, function.output) else f }
            .filterIndexed { i, _ ->
                val consumePrevious = i == indexOfFunc - 1 && consumablePrevious != null
                val consumeForward = i in consumeList.indices.map { it + indexOfFunc + 1 }
                !(consumePrevious || consumeForward)
            }
    }

    private fun getPreviousIfConsumableByFunctionAtIndex(funcs: List<Function>, indexOfFunc: Int, ): Function? {
        if (indexOfFunc > 0) {
            val function = funcs[indexOfFunc]
            val previousFunc = funcs[indexOfFunc - 1]
            if (previousFunc.isResolved() && previousFunc.output == function.inputs[0]) {
                return previousFunc
            }
        }
        return null
    }
}

private fun List<Function>.getIndexOfNextExecution(): Int {
    for (precedence in Precedence.values().reversed()) {
        val target = this
            .withIndex()
            .filter { it.value.precedence == precedence }
            .firstOrNull { (i, f) -> f.isExecutable() && this.inputsOfFunctionAtIndexAreResolvedValues(i) }
        if (target != null) return target.index
    }
    throw DeveloperError("Run out of executables: This function should be called safely: ${this.joinToString {
        Du81ProgramEnvironment.getDiagnosticsString(it)
    }}")
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