package tests.compressedlang

import compressedlang.Du81Lexer
import compressedlang.Du81Program
import compressedlang.Du81ProgramEnvironment
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Du81ProgramTest {

    @AfterEach
    fun makeStaticSingletonTestable() {
        Du81ProgramEnvironment.for_test_only_ResetEnvironment()
    }

    @Test
    fun `program that filters away numbers smaller than 1`() {
        val source = "F>0"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(1, 2, 8), program.getResult()[0].unwrap())
    }

    @Test
    fun `program that filters away negative numbers`() {
        val source = "F>-1"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(1, 2, 0, 8), program.getResult()[0].unwrap())
    }

    @Test
    fun `program that filters numbers in list which appear on their own index`() {
        val source = "F=i"
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(0, 2, 5, 7), program.getResult()[0].unwrap())
    }

    @Test
    fun `program that filters numbers in list which appear on their own index works the same with explicit input`() {
        val source = "Fv=i"
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(0, 2, 5, 7), program.getResult()[0].unwrap())
    }

    @Test
    fun `program that maps to one higher`() {
        val source = "M+1"
        val input = listOf(-1, 0, 1, 2, 3)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(0, 1, 2, 3, 4), program.getResult()[0].unwrap())
    }

    @Test
    fun `program that has a chain of filters`() {
        val source = "F>100F>100F>200F>300" // TODO filtering to empty list gives index out of bounds on the next call
        val input = listOf(100, 200, 300, 400)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(400), program.getResult()[0].unwrap())
    }

    @Test
    fun `program that has a chain of maps`() {
        val source = "M+1M+1M+1M+1M+1M+1"
        val input = listOf(-1, 0, 1, 2)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(5, 6, 7, 8), program.getResult()[0].unwrap())
    }

    @Test
    fun `program that consumes weak inputs`() {
        val source = "M1+1+1"
        val input = listOf(-123, 213123, 12312)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(3, 3, 3), program.getResult()[0].unwrap())
    }

    @Test
    fun `program that mixes consuming and not consuming previous input`() {
        val source = "M+1+1"
        val input = listOf(-123, 213123, 12312)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(-121, 213125, 12314), program.getResult()[0].unwrap())
    }

    @Test
    fun `program that mixes filters and maps`() {
        val source = "M+iF>0"
        val input = listOf(0, 10, 100)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(11, 102), program.getResult()[0].unwrap())
    }
}
