package tests.compressedlang

import compressedlang.ParsedStringLiteral
import compressedlang.lex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Du81LexerTest {

    @Test
    fun `filter all a characters`() {
        val source = "F=\"abc\""
        val tokens = source.lex()

        assertEquals(true, tokens[2] is ParsedStringLiteral)

        val parsedString = tokens[2] as ParsedStringLiteral

        assertEquals("abc", parsedString.source)
    }
}