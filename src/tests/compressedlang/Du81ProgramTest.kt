package tests.compressedlang

import compressedlang.Du81Lexer
import compressedlang.Du81Program
import compressedlang.Du81ProgramEnvironment
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class Du81ProgramTest {

    @AfterEach
    fun makeStaticSingletonTestable(){
        Du81ProgramEnvironment.for_test_only_ResetEnvironment()
    }

    @Test
    fun `program that filters away numbers smaller than 1`() {
        val source = "F>0"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(1, 2, 8), program.getResult()[0].list)
    }

    @Test
    fun `program that filters away negative numbers`() {
        val source = "F>-1"
        val input = listOf(-1, -1, 1, -2, 2, -3, -5, 0, 8)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(1, 2, 0, 8), program.getResult()[0].list)
    }


    @Test
    fun `program that filters numbers in list which appear on their own index`() {
        val source = "F=i"
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val lexed = Du81Lexer(source, false)
        val program = Du81Program(source, lexed.tokens, input)
        program.runForInput()

        assertEquals(listOf(0, 2, 5, 7), program.getResult()[0].list)
    }
}
