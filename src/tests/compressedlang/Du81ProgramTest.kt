package tests.compressedlang

import compressedlang.Du81Program
import compressedlang.Du81ProgramEnvironment
import compressedlang.lex
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tests.assertAllEquals
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
    fun `filters numbers larger than 0`() {
        val source = "Fv>0"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(1, 2, 8), program.getResult()[0].unwrap())
    }

    @Test
    fun `filters numbers larger than 0 with nilad fetching value`() {
        val source = "Fv>0"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(1, 2, 8), program.getResult()[0].unwrap())
    }

    @Test
    fun `filters away negative numbers`() {
        val source = "F>-1"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(1, 2, 0, 8), program.getResult()[0].unwrap())
    }

    @Test
    fun `filters numbers in list which appear on their own index`() {
        val source = "F=i"
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(0, 2, 5, 7), program.getResult()[0].unwrap())
    }

    @Test
    fun `filters numbers in list which appear on their own index works the same with explicit input`() {
        val source1 = "Fv=i"
        val source2 = "F=i"
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val program1 = Du81Program(source1, source1.lex(), input).apply { runForInput() }
        val program2 = Du81Program(source2, source2.lex(), input).apply { runForInput() }

        assertEquals(listOf(0, 2, 5, 7), program1.getResult()[0].unwrap())
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
            "Fi+5=v+5",
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

        assertEquals(listOf(0, 1, 2, 3, 4), program.getResult()[0].unwrap())
    }

    @Test
    fun `chain filters`() {
        val source = "F>100F>100F>200F>300"
        val input = listOf(100, 200, 300, 400)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(400), program.getResult()[0].unwrap())
    }

    @Test
    fun `chain maps`() {
        val source = "M+1M+1M+1M+1M+1M+1"
        val input = listOf(-1, 0, 1, 2)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(5, 6, 7, 8), program.getResult()[0].unwrap())
    }

    @Test
    fun `consumes weak inputs`() {
        val source = "M1+1+1"
        val input = listOf(-123, 213123, 12312)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(3, 3, 3), program.getResult()[0].unwrap())
    }

    @Test
    fun `mixes consuming and not consuming previous input`() {
        val source = "M+1+1"
        val input = listOf(-123, 213123, 12312)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(-121, 213125, 12314), program.getResult()[0].unwrap())
    }

    @Test
    fun `mixes filters and maps`() {
        val source = "M+iF>0"
        val input = listOf(0, 10, 100)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(11, 102), program.getResult()[0].unwrap())
    }

    @Test
    fun `filters explicit index equals to 1`() {
        val source = "F1=i"
        val input = listOf(100, 202, 300, 400)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf(202), program.getResult()[0].unwrap())
    }

    @Test
    fun `filters empty list`() {
        val source = "F>1"
        val input = listOf<Int>()
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf<Int>(), program.getResult()[0].unwrap())
    }

    @Test
    fun `maps empty result list`() {
        val source = "F>1M=0"
        val input = listOf(0, 0, 0, 0)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, input)
        program.runForInput()

        assertEquals(listOf<Int>(), program.getResult()[0].unwrap())
    }

    @Test
    fun `filter all a characters`() {
        val source = "F=\"a\""
        val input = "aabbac".toList()
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
}
