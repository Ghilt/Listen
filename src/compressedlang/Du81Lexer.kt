package compressedlang

import toGroupedStringList
import java.nio.file.Files
import java.nio.file.Paths

class Du81Lexer(private val inputRawOrPath: String, isPath: Boolean = true) {

    val source: String
    val tokens: List<ParsedElement>

    init {
        source = if (isPath) readInputFromPath(inputRawOrPath) else inputRawOrPath

        val sourceElements: List<ParsedElement> = source
            .toList()
            .toGroupedStringList(true) { x -> x == '"' }
            .toGroupedStringList { x -> x.toIntOrNull() != null }
            .map { it.toParsedElement() }

        tokens = sourceElements
    }

    private fun readInputFromPath(filePath: String): String {
        val stream = Files.newInputStream(Paths.get(filePath))
        var input = ""
        stream.buffered().reader().use { reader ->
            input = reader.readText()
        }

        return input
    }

    fun printDiagnostics() {
        if (source != inputRawOrPath) println(inputRawOrPath)
        println(source)
    }
}

private fun String.toParsedElement(): ParsedElement {
    return when {
        this[0].isDigit() && this.contains('.') -> ParsedNumber(this.toDouble())
        this[0].isDigit() -> ParsedNumber(this.toInt())
        this[0] == '"' -> ParsedStringLiteral(this)
        else -> FunctionToken(this[0])
    }
}
