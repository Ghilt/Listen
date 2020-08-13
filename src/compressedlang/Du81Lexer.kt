package compressedlang

import collectionlib.joinNeighbors
import collectionlib.toGroupedStringList
import java.nio.file.Files
import java.nio.file.Paths


fun du81Lex(inputRawOrPath: String, isPath: Boolean = true): Pair<String, List<ParsedElement>> {

    val source = if (isPath) readInputFromPath(inputRawOrPath) else inputRawOrPath

    fun String.isInt() = this.toIntOrNull() != null

    return source to source
        .toList()
        .toGroupedStringList(true) { x -> x == '"' }
        .toGroupedStringList { x -> x.isInt() }
        .joinNeighbors({ a, b, c -> a.isInt() && b == "." && c.isInt() }) { a, b, c -> "$a$b$c" } // TODO this step could be removed(/or kept alongside with) if '.' was made an Int-append dyad, could maybe be cool to append ints for some reason?
        .map { it.toParsedElement() }
}

private fun readInputFromPath(filePath: String): String {
    val stream = Files.newInputStream(Paths.get(filePath))
    var input: String
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

fun String.lex(path: Boolean = false) = du81Lex(this.filter { !it.isWhitespace() }, path).second