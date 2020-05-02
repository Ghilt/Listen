package compressedlang

fun main(args: Array<String>) {

    val path = "D:\\Files\\Code\\IntelliJ\\Kollektion\\testFiles\\hello.du8"
    val input = listOf(1, 2, 3, 4, 5)

    val (source, tokens) = du81Lex(path)
    val program = Du81Program(source, tokens, input)
    program.runForInput()
}

