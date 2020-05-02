package compressedlang

import compressedlang.fncs.Function
import compressedlang.fncs.currentListNilad

class Context(input: Du81List) {

    constructor(stringInput: String) : this(stringInput.toDu81List())
    constructor(intListInput: List<Int>) : this(intListInput.toDu81List())

    private val targets: MutableList<Du81List> = mutableListOf(input)
    private val functionContext = mutableListOf(FunctionContext(targets, currentListNilad))
    private val currentFunctionContext
        get() = functionContext[0]

    private val currentTarget
        get() = targets[0]

    fun prepareFor(function: Function): Boolean {
        return if (currentFunctionContext.willAccept(function)) {
            currentFunctionContext.put(function)
            true
        } else {
            currentFunctionContext.build()
            false
        }
    }

    fun isReadyForExecution() = currentFunctionContext.isBuilt

    fun endOfProgramReached() {
        currentFunctionContext.build()
        execute()
    }

    fun execute() {
        targets.add(0, currentFunctionContext.execute())
        functionContext.add(0, FunctionContext(targets, currentListNilad))
    }

    fun getResult(): List<Du81List> = targets.toList()
}

// F>iF="hej"F<424