package compressedlang

import tests.getResultAsString
import java.lang.IllegalArgumentException

fun main(args: Array<String>) {

//    val path = "D:\\Files\\Code\\IntelliJ\\Kollektion\\testFiles\\hello.du8"

    val arguments = InterpreterFlagManager(args)
    val programCode = arguments.program ?: throw IllegalArgumentException("Program was null for $args")

    val (source, tokens) = du81Lex(programCode, false)

    val program = Du81Program(source, tokens, arguments.inputs)
    program.runForInput()

    println(program.getResultAsString())
}

