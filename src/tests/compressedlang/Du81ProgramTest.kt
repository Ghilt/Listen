package tests.compressedlang

import compressedlang.Du81Program
import compressedlang.Du81ProgramEnvironment
import compressedlang.SyntaxError
import compressedlang.lex
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tests.assertAllEquals
import tests.expectException
import tests.getResultAsString
import tests.runSeveralProgramsOnTheSameInput

internal class Du81ProgramTest {

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
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(1.3, 2.1, 0.1, 8.2), program.getResult()[0])
    }

    @Test
    fun `filters numbers larger than 0`() {
        val source = "Fv>0"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(1, 2, 8), program.getResult()[0])
    }

    @Test
    fun `filters numbers larger than 0 with nilad fetching value`() {
        val source = "Fv>0"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(1, 2, 8), program.getResult()[0])
    }

    @Test
    fun `filters away negative numbers`() {
        val source = "F>-1"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(1, 2, 0, 8), program.getResult()[0])
    }

    @Test
    fun `filters numbers in list which appear on their own index`() {
        val source = "F=i"
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(0, 2, 5, 7), program.getResult()[0])
    }

    @Test
    fun `filters numbers in list which appear on their own index works the same with explicit input`() {
        val source1 = "Fv=i"
        val source2 = "F=i"
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val program1 = Du81Program(source1, source1.lex(), input).apply { runForInput() }
        val program2 = Du81Program(source2, source2.lex(), input).apply { runForInput() }

        assertEquals(listOf(0, 2, 5, 7), program1.getResult()[0])
        assertEquals(program1.getResult(), program2.getResult())
    }

    @Test
    fun `filters numbers in list which appear on their own index works the same with explicit inputs`() {
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val results = runSeveralProgramsOnTheSameInput(
            input,
            "Fv=i",
            "F=i",
            "Fi=v",
            "F2+i=v+2",
            "Fi+3=3+v",
            "F4+v=i+4",
            "Fi,+5,=v+5",
            "Fi+3=3+,v",
            "F4+v,,=,i+4",
            "Fi,+5=v,+,,5",
            "Fi+8+8-8=v+1+1+1+1+1+1+1+1"
        )

        assertAllEquals(listOf(0, 2, 5, 7), results)
    }

    @Test
    fun `maps to one higher`() {
        val source = "M+1"
        val input = listOf(-1, 0, 1, 2, 3)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(0, 1, 2, 3, 4), program.getResult()[0])
    }

    @Test
    fun `maps to one lower`() {
        val source = "Mv-1" // Does not use implicit values with (-) sign as it is a dyad in all cases. Zero is its implicit input
        val input = listOf(-1, 0, 1, 2, 3)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(-2, -1, 0, 1, 2), program.getResult()[0])
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
    fun `mixes filters and maps`() {
        val source = "M+iF>0"
        val input = listOf(0, 10, 100)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(11, 102), program.getResult()[0])
    }

    @Test
    fun `filters explicit index equals to 1`() {
        val source = "F1=i"
        val input = listOf(100, 202, 300, 400)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(202), program.getResult()[0])
    }

    @Test
    fun `filters empty list`() {
        val source = "F>1"
        val input = listOf<Int>()
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf<Int>(), program.getResult()[0])
    }

    @Test
    fun `maps empty result list`() {
        val source = "F>1M=0"
        val input = listOf(0, 0, 0, 0)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf<Int>(), program.getResult()[0])
    }

    @Test
    fun `filter all a characters`() {
        val source = "F=\"a\""
        val input = "aabbac".split("")
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals("aaa", program.getResultAsString())
    }

    @Test
    fun `map to nilad value`() {
        val source = "MvF=3"
        val input = listOf(1, 2, 3, 2, 1)
        val program = Du81Program(source, source.lex(), input)
        program.runForInput()

        assertEquals("3", program.getResultAsString())
    }

    @Test
    fun `list by index monad fetches correct list`() {
        val source = "M100M200M300Mi\$"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)
        program.runForInput()

        assertEquals("300, 300, 300, 200, 200, 200, 100, 100, 100",
            program.getResult()[0].flatMap { it as ArrayList<*> }.joinToString()
        )
    }

    @Test
    fun `get element of list by index dyad`() {
        val source = "M100M200M300Mi\$e0"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)
        program.runForInput()

        assertEquals("300200100", program.getResultAsString())
    }

    @Test
    fun `create list by taking single item from list`() {
        val source = "e1"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)
        program.runForInput()

        assertEquals("b", program.getResultAsString())
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
    fun `flatmap list into a list of copies of itself`() {
        val source = "P_"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)
        program.runForInput()
        assertEquals("abcabcabc", program.getResultAsString())
    }

    @Test
    fun `flatmap different past results together`() {
        val source = "M11M22M33Mi\$Pv"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)
        program.runForInput()

        assertEquals("333333222222111111", program.getResultAsString())
    }

    @Test
    fun `create list from value works correctly`() {
        val source = "@112121229"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)
        program.runForInput()

        assertEquals("112121229", program.getResultAsString())
    }

    @Test
    fun `| pipe works correctly`() {
        val source = "|||||M0\$e2"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), input)
        program.runForInput()

        assertEquals("ccc", program.getResultAsString())
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
    fun `nop resolves to nothingness`() {
        val source = "F,,,,,,=,,,,,1,,,,,,+,,,,,1,,,,,,,,"
        val input = listOf(1, 2, 3, 2)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("22", program.getResultAsString())
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
    fun `sum monad sums list`() {
        val source = "Mp"
        val input = listOf(1, 2, 3, -2)
        val program = Du81Program(source, source.lex(), input)

        program.runForInput()

        assertEquals("4444", program.getResultAsString())
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
}
