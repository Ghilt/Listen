package compressedlang

class Du81Program(
    private val source: String,
    private val tokens: List<ParsedElement>,
    private val input: List<Int>
) {
    private val context = Context(input)
    private val functions = FunctionRepository()
    private val tokensExecuted: MutableList<Boolean> = MutableList(tokens.size) { false }


    fun runForInput() {
        var instructionPointer = 0
        while (instructionPointer in tokens.indices) {
            tokensExecuted[instructionPointer] = true
            instructionPointer = execute(instructionPointer)
        }
    }

    private fun execute(instructionPointer: Int): Int {
        when (val parsedElement = tokens[instructionPointer]) {
            is FunctionToken -> {
                val f = functions.get(parsedElement)
                if (f == null) {
                    throw SyntaxError("Function not found for token: ${parsedElement.token}")
                } else {
                    context.prepareFor(f)
                }
            }
            is ParsedNumber<*> -> context.prepareFor(Number(parsedElement.source))
            is ParsedStringLiteral -> context.prepareFor(StringLiteral(parsedElement.source))
        }
        return instructionPointer + 1
    }
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