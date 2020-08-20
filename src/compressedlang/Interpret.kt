package compressedlang

import tests.getCommaSeparatedResult
import tests.getResultAsFirstElement
import tests.getResultAsLastElement
import tests.getResultAsString
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {

//    val path = "D:\\Files\\Code\\IntelliJ\\Kollektion\\testFiles\\hello.du8"

    val arguments = InterpreterFlagManager(args)
    val programCode = arguments.program ?: throw IllegalArgumentException("Program was null for $args")

    val (source, tokens) = du81Lex(programCode, false)

    val program = Du81Program(source, tokens, arguments.inputs)
    program.runForInput()

    println(getOutput(program, arguments.outputMode))
}

fun getOutput(program: Du81Program, mode: OutputMode): String{
    return when (mode){
        OutputMode.COMMA_SEPARATED_LIST -> program.getCommaSeparatedResult()
        OutputMode.STRING -> program.getResultAsString()
        OutputMode.FIRST_ELEMENT -> program.getResultAsFirstElement()
        OutputMode.LAST_ELEMENT -> program.getResultAsLastElement()
    }
}

