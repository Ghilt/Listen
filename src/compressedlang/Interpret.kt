package compressedlang

fun main(args: Array<String>) {

    val path = "D:\\Files\\Code\\IntelliJ\\Kollektion\\testFiles\\hello.du8"
    val input = listOf(1, 2, 3, 4, 5)

    val v = Du81Lexer(path)
    val program = Du81Program(v.source, v.tokens, input)
    program.runForInput()
    v.printDiagnostics()
}

