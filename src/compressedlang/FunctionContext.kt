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
            function.consumesList && elements.last().usesNewContext() -> true
            function.consumesList && elements.last() is InnerFunction -> true // inner functions produces lists, for now
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
                is Number -> "N"
                is StringLiteral -> "S"
                is Nilad -> "_"
                is Monad<*, *> -> "M"
                is Dyad<*, *, *> -> "D"
                is InnerFunction -> "(${functions[functions.size - 1 - it.index].diagnosticsString()})"
            }
        }
    }

    fun execute(): Du81List<*> {
        val target = produceList(listProvider)

        val result = mutableListOf<Any>()
        for ((i, v) in target.list.withIndex()) {

            val internalElements = List(elements.size) { elements[it] to false}

//            TODO
//            internalElements.
        }

            return "".toListDu81List()
    }

    private fun produceList(provider: Nilad): Du81List<*> {
        return when (provider.contextKey) {
            ContextKey.CURRENT_LIST -> targets[0]
            else -> throw DeveloperError("This is not a list producer")
        }
    }
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