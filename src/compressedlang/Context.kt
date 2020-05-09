package compressedlang

import compressedlang.fncs.Function
import compressedlang.fncs.currentListNilad

class Context(input: List<Any>) {

    private val targets: MutableList<List<Any>> = mutableListOf(input)
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

    fun getResult(): List<List<Any>> = targets.toList()
}

// F>iF="hej"F<424