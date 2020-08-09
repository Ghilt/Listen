package tests.compressedlang

import compressedlang.Du81Program
import compressedlang.Du81ProgramEnvironment
import compressedlang.lex
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tests.getCommaSeparatedResult

internal class Du81ProgramMathFunctionsTest {

    @BeforeEach
    fun initiateEnvironment() {
        Du81ProgramEnvironment.initialize()
    }

    @AfterEach
    fun makeStaticSingletonTestable() {
        Du81ProgramEnvironment.for_test_only_resetEnvironment()
    }

    @Test
    fun `filters doubles larger than 0,001`() {
        val source = "Fv>0.001"
        val input = listOf(-1.5, -1.4, 1.3, -2.1, 2.1, -3.2, -5.3, 0.1, 0.0, 8.2)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals(listOf(1.3, 2.1, 0.1, 8.2), program.getResult()[0])
    }

    @Test
    fun `filters numbers larger than 0`() {
        val source = "Fv>0"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals(listOf(1, 2, 8), program.getResult()[0])
    }

    @Test
    fun `filters numbers larger than 0 with nilad fetching value`() {
        val source = "Fv>0"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals(listOf(1, 2, 8), program.getResult()[0])
    }

    @Test
    fun `filters away negative numbers`() {
        val source = "F>-1"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals(listOf(1, 2, 0, 8), program.getResult()[0])
    }

    @Test
    fun `maps to one higher`() {
        val source = "M+1"
        val input = listOf(-1, 0, 1, 2, 3)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals(listOf(0, 1, 2, 3, 4), program.getResult()[0])
    }

    @Test
    fun `maps to one lower`() {
        val source = "Mv-1" // Does not use implicit values with (-) sign as it is a dyad in all cases. Zero is its implicit input
        val input = listOf(-1, 0, 1, 2, 3)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals(listOf(-2, -1, 0, 1, 2), program.getResult()[0])
    }

    @Test
    fun `division dyad`() {
        val source = "M/2"
        val input = listOf(10, 7)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("5, 3.5", program.getCommaSeparatedResult())
    }

    @Test
    fun `division dyad with a non int`() {
        val source = "M/2.5"
        val input = listOf(0.8, 10, 7)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("0.32, 4, 2.8", program.getCommaSeparatedResult())
    }

    @Test
    fun `whole number division dyad`() {
        val source = "M¤2"
        val input = listOf(-3, 10, 7)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("-1, 5, 3", program.getCommaSeparatedResult())
    }

    @Test
    fun `whole number division dyad a non int in input should still only return integers`() {
        val source = "M¤2.3"
        val input = listOf(-1, 10, 7)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("0, 4, 3", program.getCommaSeparatedResult())
    }

    @Test
    fun `modulo dyad`() {
        val source = "M%3"
        val input = listOf(-2, -1, 10, 7)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("-2, -1, 1, 1", program.getCommaSeparatedResult())
    }

    @Test
    fun `modulo dyad with non integers`() {
        val source = "M%3.5"
        val input = listOf(-5.75, -1, 7.5, 7)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("-2.25, -1, 0.5, 0", program.getCommaSeparatedResult())
    }

    @Test
    fun `modulo dyad with negative value`() {
        val source = "M%-3"
        val input = listOf(-2, -1, 10, 7)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("-2, -1, 1, 1", program.getCommaSeparatedResult())
    }


    @Test
    fun `mathematical modulo dyad giving no negative numbers in output`() {
        val source = "M£3"
        val input = listOf(-10002, -2, -1, 10, 7)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("0, 1, 2, 1, 1", program.getCommaSeparatedResult())
    }

    @Test
    fun `mathematical modulo dyad with negative input giving no positive numbers in output`() {
        val source = "M£-3"
        val input = listOf(-4, -3, -2, -1, 0, 1, 2, 3, 4)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("-1, 0, -2, -1, 0, -2, -1, 0, -2", program.getCommaSeparatedResult())
    }

    @Test
    fun `mathematical modulo dyad with non integers giving no negative numbers in output`() {
        val source = "M£3.5"
        val input = listOf(-5.75, -1, 7.5, 7)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("1.25, 2.5, 0.5, 0", program.getCommaSeparatedResult())
    }

    @Test
    fun `power dyad`() {
        val source = "M^3"
        val input = listOf(-2, -1.10166, 10.5, 7)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("-8, -1.3370348980542963, 1157.625, 343", program.getCommaSeparatedResult())
    }


    @Test
    fun `power dyad to take square root`() {
        val source = "M^0.5"
        val input = listOf(4, 16, -2)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("2, 4, NaN", program.getCommaSeparatedResult())
    }
}
