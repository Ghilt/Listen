package tests.compressedlang

import compressedlang.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Du81LexerTest {

    @Test
    fun `filter all a characters`() {
        val source = "F=\"abc\""
        val lexed = Du81Lexer(source, false)

        assertEquals(true, lexed.tokens[2] is ParsedStringLiteral)

        val parsedString = lexed.tokens[2] as ParsedStringLiteral

        assertEquals("abc", parsedString.source)
    }
}