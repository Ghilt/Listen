package tests.compressedlang

import compressedlang.ParsedNumber
import compressedlang.ParsedStringLiteral
import compressedlang.lex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Du81LexerTest {

    @Test
    fun `correctly make string token`() {
        val source = "F=\"abc\""
        val tokens = source.lex()

        assertEquals(true, tokens[2] is ParsedStringLiteral)

        val parsedString = tokens[2] as ParsedStringLiteral

        assertEquals("abc", parsedString.source)
    }

    @Test
    fun `correctly make double number token`() {
        val source = "F=0.0708"
        val tokens = source.lex()

        assertEquals(true, tokens[2] is ParsedNumber<*>)

        val parsedNumber = tokens[2] as ParsedNumber<*>

        assertEquals(0.0708, parsedNumber.source)
    }
}