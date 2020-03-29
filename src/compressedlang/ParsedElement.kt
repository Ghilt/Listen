package compressedlang

sealed class ParsedElement
data class ParsedNumber<T : kotlin.Number>(val source: T) : ParsedElement()
data class ParsedStringLiteral(val source: String) : ParsedElement()
data class FunctionToken(val token: Char) : ParsedElement()
object Nop : ParsedElement()