package tests.compressedlang

import compressedlang.*
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

    @Test
    fun `flag manager handles code from commandline`() {
        val args = listOf("-c", "Mv", "1,2,3,4").toTypedArray()

        val manager = InterpreterFlagManager(args)

        assertEquals("Mv", manager.program)
        assertEquals(1, manager.inputs.size)
        assertEquals(listOf(1, 2, 3, 4), manager.inputs[0])
    }

    @Test
    fun `flag manager handles output mode comma separated`() {
        val args = listOf("-c", "Mv", "1,2", "-l").toTypedArray()

        val manager = InterpreterFlagManager(args)

        assertEquals(OutputMode.COMMA_SEPARATED_LIST, manager.outputMode)
    }

    @Test
    fun `flag manager handles output mode first element`() {
        val args = listOf("-c", "Mv", "1,2", "-a").toTypedArray()

        val manager = InterpreterFlagManager(args)

        assertEquals(OutputMode.FIRST_ELEMENT, manager.outputMode)
    }

    @Test
    fun `flag manager handles output mode last element`() {
        val args = listOf("-c", "Mv", "1,2", "-o").toTypedArray()

        val manager = InterpreterFlagManager(args)

        assertEquals(OutputMode.LAST_ELEMENT, manager.outputMode)
    }
    @Test
    fun `flag managers prepares multiple inputs for program`() {
        val args = listOf("-c", "Mv", "1,2,3", "9,9,9.13").toTypedArray()

        val manager = InterpreterFlagManager(args)

        assertEquals("Mv", manager.program)
        assertEquals(2, manager.inputs.size)
        assertEquals(listOf(1, 2, 3), manager.inputs[0])
        assertEquals(listOf(9.0, 9.0, 9.13), manager.inputs[1])
    }

    @Test
    fun `read program from file per default`() {
        val args =
            listOf("testFiles\\automatic_test_001.du81", "198").toTypedArray()

        val manager = InterpreterFlagManager(args)

        assertEquals("Mv+1", manager.program)
        assertEquals(1, manager.inputs.size)
        assertEquals(listOf(198), manager.inputs[0])
    }

    @Test
    fun `flag for different separators`() {
        val args = listOf("-c", "Mv", "1,2,3,4", "-s", " ", "a b c", "e f g").toTypedArray()

        val manager = InterpreterFlagManager(args)

        assertEquals("Mv", manager.program)
        assertEquals(3, manager.inputs.size)
        assertEquals(listOf(1, 2, 3, 4), manager.inputs[0])
        assertEquals(listOf("a", "b", "c"), manager.inputs[1])
        assertEquals(listOf("e", "f", "g"), manager.inputs[2])
    }
}