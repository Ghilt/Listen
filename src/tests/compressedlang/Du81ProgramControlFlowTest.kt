package tests.compressedlang

import compressedlang.Du81Program
import compressedlang.Du81ProgramEnvironment
import compressedlang.SyntaxError
import compressedlang.lex
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tests.expectException
import tests.getCommaSeparatedResult
import tests.getResultAsString

internal class Du81ProgramControlFlowTest {

    @BeforeEach
    fun initiateEnvironment() {
        Du81ProgramEnvironment.initialize()
    }

    @AfterEach
    fun makeStaticSingletonTestable() {
        Du81ProgramEnvironment.for_test_only_resetEnvironment()
    }

    @Test
    fun `chain filters`() {
        val source = "F>100F>100F>200F>300"
        val input = listOf(100, 200, 300, 400)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(400), program.getResult()[0])
    }

    @Test
    fun `chain maps`() {
        val source = "M+1M+1M+1M+1M+1M+1"
        val input = listOf(-1, 0, 1, 2)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(5, 6, 7, 8), program.getResult()[0])
    }

    @Test
    fun `consumes weak inputs`() {
        val source = "M1+1+1"
        val input = listOf(-123, 213123, 12312)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(3, 3, 3), program.getResult()[0])
    }

    @Test
    fun `mixes consuming and not consuming previous input`() {
        val source = "M+1+1"
        val input = listOf(-123, 213123, 12312)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(-121, 213125, 12314), program.getResult()[0])
    }

    @Test
    fun `complex filters with pointless inner function output should be the same as input`() {
        val source = "F(_F=\"a\")e0=\"a\""
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)
        program.runForInput()

        assertEquals("abc", program.getResultAsString())
    }

    @Test
    fun `throw syntax error if function have too many resolved values`() {
        val source = "F1,1,1,1"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)


        val exception = expectException { program.runForInput() }

        assertEquals(true, exception is SyntaxError, "Correct exception was not thrown: $exception")
    }

    @Test
    fun `syntax error thrown if ending inner function without having started one`() {
        val source = "Fv=i)"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)

        val exception = expectException { program.runForInput() }

        assertEquals(true, exception is SyntaxError, "Correct exception was not thrown: $exception")
    }


    @Test
    fun `perform some calculation in inner function`() {
        val source = "M(_M+i)ei"
        val input = listOf(1, 2, 3, 0)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("1353", program.getResultAsString())
    }

    @Test
    fun `inner function is calculated first and taken as argument to monad`() {
        val source = "Mv+(_Mv)p"
        val input = listOf(1, 2, 3, 0)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("7896", program.getResultAsString())
    }

    @Test
    fun `perform some calculation in consecutive inner functions`() {
        val source = "M(_M+i)p+(_M+v)p+(_M+2)pFi=0"
        val input = listOf(1, 2, 3, 0)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("38", program.getResultAsString())
    }

    @Test
    fun `inner function returns single value`() {
        val source = "Mi+(_p)"
        val input = listOf(1, -52, 3, 0)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("-48-47-46-45", program.getResultAsString())
    }

    @Test
    fun `override precedence rules by making inner functions`() {
        val source = "M(5+5)*(5+5)*(5+5)"
        val input = listOf(1, 2)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("10001000", program.getResultAsString())
    }

    @Test
    fun `new outer function immediately after inner function`() {
        val source = "M(5+5)F0=i"
        val input = listOf(1, 2, 3, 4)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("10", program.getResultAsString())
    }

    @Test
    fun `access outer context from inner function contextless part`() {
        val source = "M(v+5)"
        val input = listOf(1, 2)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("67", program.getResultAsString())
    }

    @Test
    fun `append lists with flatmap with inner function providing implicit input`() {
        val source = "M+1Mv-1P(v-1)\$"
        val input = listOf(1, 2)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("1223", program.getResultAsString())
    }

    @Test
    fun `append lists with flatmap with nested inner function supplying list provider`() {
        val source = "M+1Mv-1P((v-1)\$Fi=0)"
        val input = listOf(1, 2)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("12", program.getResultAsString())
    }

    @Test
    fun `nested inner functions`() {
        val source = "M(((v)))"
        val input = listOf("hej", " ", "där")
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("hej där", program.getResultAsString())
    }

    @Test
    fun `end outer function to start new one`() {
        val source = "Mi.~F=\"hej\""
        val input = listOf("hej", " ", "där")
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("hej", program.getResultAsString())
    }

    @Test
    fun `end outer function with unfinished inner function in it`() {
        val source = "M(i.~Fv=\"hej\""
        val input = listOf("hej", " ", "där")
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("hej", program.getResultAsString())
    }

    @Test
    fun `throw syntax error if config value is of wrong type`() {
        val source = "\"k\"C"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)

        val exception = expectException { program.runForInput() }

        assertEquals(true, exception is SyntaxError, "Correct exception was not thrown: $exception")
    }

    @Test
    fun `triad behaves`() {
        val source = "Mg1,i"
        val input = listOf(0,1)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("[0, 0, 1, 1], [0, 1, 1, 2]", program.getCommaSeparatedResult())
    }
}
