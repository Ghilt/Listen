package compressedlang

class Context(input: Du81List<*>) {

    constructor(stringInput: String) : this(stringInput.toListDu81List())
    constructor(intListInput: List<Int>) : this(intListInput.toListDu81List())

    private val targets: List<Du81List<*>> = listOf(input)
    private val functionContext = FunctionContext(targets, currentListNilad)

    fun prepareFor(function: Function) {
        if (functionContext.willAccept(function)) {
            functionContext.put(function)
        } else {
            functionContext.build()
        }
    }
}

// F>iF="hej"F<424