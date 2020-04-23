package compressedlang

class Context(input: Du81List<*>) {

    constructor(stringInput: String) : this(stringInput.toListDu81List())
    constructor(intListInput: List<Int>) : this(intListInput.toListDu81List())

    private val targets: MutableList<Du81List<*>> = mutableListOf(input)
    private val functionContext = mutableListOf(FunctionContext(targets, currentListNilad))
    private val currentFunctionContext
        get() = functionContext[0]

    private val currentTarget
        get() = targets[0]

    fun prepareFor(function: Function) {
        if (currentFunctionContext.willAccept(function)) {
            currentFunctionContext.put(function)
        } else {
            currentFunctionContext.build()
        }
    }

    fun isReadyForExecution() = currentFunctionContext.isBuilt

    fun endOfProgramReached() {
        currentFunctionContext.build()
        execute()
    }

    fun execute() {
        targets.add(0, currentFunctionContext.execute())
    }

    fun getResult(): List<Du81List<*>> = targets.toList()
}

// F>iF="hej"F<424