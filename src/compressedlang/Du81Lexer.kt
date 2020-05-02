package compressedlang

import toGroupedStringList
import java.nio.file.Files
import java.nio.file.Paths


fun du81Lex(inputRawOrPath: String, isPath: Boolean = true): Pair<String, List<ParsedElement>> {

    val source = if (isPath) readInputFromPath(inputRawOrPath) else inputRawOrPath

    return source to source
        .toList()
        .toGroupedStringList(true) { x -> x == '"' }
        .toGroupedStringList { x -> x.toIntOrNull() != null }
        .map { it.toParsedElement() }
}

private fun readInputFromPath(filePath: String): String {
    val stream = Files.newInputStream(Paths.get(filePath))
    var input = ""
    stream.buffered().reader().use { reader ->
        input = reader.readText()
    }

    return input
}


private fun String.toParsedElement(): ParsedElement {
    return when {
        this[0].isDigit() && this.contains('.') -> ParsedNumber(this.toDouble())
        this[0].isDigit() -> ParsedNumber(this.toInt())
        this[0] == '"' -> ParsedStringLiteral(this.substring(1, this.length - 1))
        else -> FunctionToken(this[0])
    }
}

fun String.lex(path: Boolean = false) = du81Lex(this, path).second