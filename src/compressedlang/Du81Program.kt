package compressedlang

class Du81Program(
    private val source: String,
    private val tokens: List<ParsedElement>,
    input: List<Int>,
    functionRepository: FunctionRepository = FunctionRepository()
) {
    private val context = Context(input)

    init {
        Du81ProgramEnvironment.initializeRepo(functionRepository)
    }

    fun runForInput() {
        var instructionPointer = 0
        while (instructionPointer in tokens.indices) {
            instructionPointer = loadToken(instructionPointer)

            if (context.isReadyForExecution()) {
                context.execute()
            }
        }
        context.endOfProgramReached()

    }

    private fun loadToken(instructionPointer: Int): Int {
        val success = when (val parsedElement = tokens[instructionPointer]) {
            is FunctionToken -> {
                val f = Du81ProgramEnvironment.repo.get(parsedElement)
                if (f == null) {
                    throw SyntaxError("Function not found for token: ${parsedElement.token}")
                } else {
                    context.prepareFor(f)
                }
            }
            is ParsedNumber<*> -> context.prepareFor(ResolvedFunction(parsedElement.source, TYPE.NUMBER))
            is ParsedStringLiteral -> context.prepareFor(ResolvedFunction(parsedElement.source, TYPE.STRING))
            Nop -> true
        }
        return instructionPointer + if (success) 1 else 0
    }

    fun getResult() = context.getResult()
}

// F>iF="hej"F<424.12
// 22022  0  22 0
// Fi>iF="hej"F<424.12
// MiFi
// 2020
// F>i*2*3*4F="hej"F<424.12
// 22020202022   0 22 0
// FF>i*2*3*4F="hej"F<424.12

//FzF>0L
// FL