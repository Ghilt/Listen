package tests.compressedlang

import compressedlang.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tests.*

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
    fun `diagnostics print`() {
        log("\nDu81 characters free: ${Du.repo.getCharacterUsedDiagnostic()}")
        assert(true)
    }

    @Test
    fun `filters numbers in list which appear on their own index`() {
        val source = "F=i"
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, listOf(input))
        program.runForInput()

        assertEquals(listOf(0, 2, 5, 7), program.getResult()[0])
    }

    @Test
    fun `filters numbers in list which appear not on their own index`() {
        val source = "F≠i"
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, listOf(input))
        program.runForInput()

        assertEquals(listOf(2, 4, 6, 8), program.getResult()[0])
    }


    @Test
    fun `filters numbers in list which appear on their own index works the same with explicit input`() {
        val source1 = "Fv=i"
        val source2 = "F=i"
        val input = listOf(0, 2, 2, 4, 6, 5, 8, 7)
        val program1 = Du81Program(source1, source1.lex(), listOf(input)).apply { runForInput() }
        val program2 = Du81Program(source2, source2.lex(), listOf(input)).apply { runForInput() }

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
    fun `mixes filters and maps`() {
        val source = "M+iF>0"
        val input = listOf(0, 10, 100)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, listOf(input))
        program.runForInput()

        assertEquals(listOf(11, 102), program.getResult()[0])
    }

    @Test
    fun `filters explicit index equals to 1`() {
        val source = "F1=i"
        val input = listOf(100, 202, 300, 400)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, listOf(input))
        program.runForInput()

        assertEquals(listOf(202), program.getResult()[0])
    }

    @Test
    fun `filters empty list`() {
        val source = "F>1"
        val input = listOf<Int>()
        val lexed = source.lex()
        val program = Du81Program(source, lexed, listOf(input))
        program.runForInput()

        assertEquals(listOf<Int>(), program.getResult()[0])
    }

    @Test
    fun `maps empty result list`() {
        val source = "F>1M=0"
        val input = listOf(0, 0, 0, 0)
        val lexed = source.lex()
        val program = Du81Program(source, lexed, listOf(input))
        program.runForInput()

        assertEquals(listOf<Int>(), program.getResult()[0])
    }

    @Test
    fun `zip reversed list with original`() {
        val source = "rz_"
        val input = listOf(1, 2, 90, 100)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[100, 1], [90, 2], [2, 90], [1, 100]", program.getCommaSeparatedResult())
    }

    @Test
    fun `filter all a characters`() {
        val source = "F=\"a\""
        val input = "aabbac".split("")
        val lexed = source.lex()
        val program = Du81Program(source, lexed, listOf(input))
        program.runForInput()

        assertEquals("aaa", program.getResultAsString())
    }

    @Test
    fun `not monad inverts boolean value`() {
        val source = "M!"
        val input = listOf(false, true, 0, -2, 3)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("1, 0, 1, 0, 0", program.getCommaSeparatedResult())
    }

    @Test
    fun `map to nilad value`() {
        val source = "MvF=3"
        val input = listOf(1, 2, 3, 2, 1)
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("3", program.getResultAsString())
    }

    @Test
    fun `list by index monad fetches correct list`() {
        val source = "M10M20M30Mi\$"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals(
            "30, 30, 30, 20, 20, 20, 10, 10, 10",
            program.getResult()[0].flatMap { it as ArrayList<*> }.joinToString()
        )
    }

    @Test
    fun `get element of list by index dyad`() {
        val source = "M100M200M300Mi\$e0"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("300200100", program.getResultAsString())
    }

    @Test
    fun `count list count returns correct count`() {
        val source = "M0M0M0Mq"
        val input = listOf("a")
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("4", program.getResultAsString())
    }

    @Test
    fun `create list by taking single item from list`() {
        val source = "e1"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("b", program.getResultAsString())
    }

    @Test
    fun `flatmap list into a list of copies of itself`() {
        val source = "P_"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()
        assertEquals("a, b, c, a, b, c, a, b, c", program.getCommaSeparatedResult())
    }

    @Test
    fun `flatmap different past results together`() {
        val source = "M11M22M33Mi\$Pv"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("333333222222111111", program.getResultAsString())
    }

    @Test
    fun `create list from value works correctly`() {
        val source = ";112121229@"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("112121229", program.getResultAsString())
    }

    @Test
    fun `| pipe works correctly`() {
        val source = "§§§§M0\$e2"
        val input = listOf("a", "b", "c")
        val program = Du81Program(source, source.lex(), listOf(input))
        program.runForInput()

        assertEquals("ccc", program.getResultAsString())
    }

    @Test
    fun `nop resolves to nothingness`() {
        val source = "F,,,,,,=,,,,,1,,,,,,+,,,,,1,,,,,,,,"
        val input = listOf(1, 2, 3, 2)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("22", program.getResultAsString())
    }

    @Test
    fun `sum monad sums list`() {
        val source = "Mp"
        val input = listOf(1, 2, 3, -2)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("4444", program.getResultAsString())
    }

    @Test
    fun `second most current list nilad built in`() {
        val source = "MiM(~Fv=\"hej\")"
        val input = listOf("hej", " ", "där")
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[hej][hej][hej]", program.getResultAsString())
    }

    @Test
    fun `chunk list into chunks of 1`() {
        val source = "1C"
        val input = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[1][2][3][4][5][6][7][8][9]", program.getResultAsString())
    }

    @Test
    fun `chunk list into chunks of the default value for chunking`() {
        val source = "C"
        val input = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[1, 2, 3][4, 5, 6][7, 8, 9]", program.getResultAsString())
    }


    @Test
    fun `chunk list into chunks of 4`() {
        val source = "2*1*2C"
        val input = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[1, 2, 3, 4][5, 6, 7, 8][9]", program.getResultAsString())
    }

    @Test
    fun `turn list into windowed representation of size 3 with step 2`() {
        val source = "3,2W"
        val input = listOf(1, 2, 3, 4, 5)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[1, 2, 3][3, 4, 5]", program.getResultAsString())
    }

    @Test
    fun `turn list into windowed representation of size 3 with step 1 using default configuration value`() {
        val source = "3W"
        val input = listOf(1, 2, 3, 4, 5)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[1, 2, 3][2, 3, 4][3, 4, 5]", program.getResultAsString())
    }

    @Test
    fun `turn list into windowed representation with trailing windows`() {
        val source = "3,1,1W"
        val input = listOf(1, 2, 3, 4, 5)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[1, 2, 3][2, 3, 4][3, 4, 5][4, 5][5]", program.getResultAsString())
    }

    @Test
    fun `consecutive truthy values yields true`() {
        val source = "2WM(e0)&e1"
        val input = listOf(0, 2, 7, 0, 5, 0, 1, 1)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("false, true, false, false, false, false, true", program.getCommaSeparatedResult())
    }

    @Test
    fun `empty string is falsy value`() {
        val source = ";4&\"\""
        val input = listOf(0, 2, 7)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("false", program.getCommaSeparatedResult())
    }

    @Test
    fun `consecutive values need at least one truthy value to yield true`() {
        val source = "2WM(e0)|e1"
        val input = listOf(0, 2, 7, 0, 0, 0, 1, 1)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("true, true, true, false, false, true, true", program.getCommaSeparatedResult())
    }

    @Test
    fun `filter sectioned splits list into sections with filtered values removed`() {
        val source = "S≠0"
        val input = listOf(0, 2, 7, 0, 902, 0, 1, 1)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[2, 7], [902], [1, 1]", program.getCommaSeparatedResult())
    }

    @Test
    fun `filter with neighbors brings with neighbors around the filtered items`() {
        val source = "N<0"
        val input = listOf(1, 2, 3, -2, 3, -1, -1, 2, 3, 0, -10)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("3, -2, 3, -1, -1, 2, 0, -10", program.getCommaSeparatedResult())
    }

    @Test
    fun `filter with neighbors with configured hood bring with neighbors around the filtered items`() {
        val source = "2,0N<0"
        val input = listOf(1, 2, 3, -2, 3, -1, -1, 2, 3, 0, -10)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("2, 3, -2, 3, -1, -1, 3, 0, -10", program.getCommaSeparatedResult())
    }

    @Test
    fun `extend entries by the index they are on`() {
        val source = "Ei"
        val input = listOf("hej", 1, 2.3)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("hej, 1, 1, 2.3, 2.3, 2.3", program.getCommaSeparatedResult())
    }

    @Test
    fun `grow entries of number list`() {
        val source = "g1,2"
        val input = listOf(-1, 9, 0.5)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("-1, 1, 9, 11, 0.5, 2.5", program.getCommaSeparatedResult())
    }

    @Test
    fun `grow entries of string list`() {
        val source = "g2,1"
        val input = listOf("a", "3", "&z")
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("abc, 345, &'(z{|", program.getCommaSeparatedResult())
    }

    @Test
    fun `grow entries throws exception if there is a mixed list as input`() {
        val source = "g2,1"
        val input = listOf("a", 1)
        val program = Du81Program(source, source.lex(), listOf(input))

        val exception = expectException { program.runForInput() }

        assertEquals(true, exception is SyntaxError, "Correct exception was not thrown: $exception")
    }

    @Test
    fun `append strings`() {
        val source = "Ma\"s\"ai"
        val input = listOf(1, -1, 900)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("1s0, -1s1, 900s2", program.getCommaSeparatedResult())
    }

    @Test
    fun `zip insert to double every element`() {
        val source = "Z+1"
        val input = listOf(1, -1, 90)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[1, 2], [-1, 0], [90, 91]", program.getCommaSeparatedResult())
    }

    @Test
    fun `zip to double every element`() {
        val source = "z_"
        val input = listOf(1, -1, true)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[1, 1], [-1, -1], [true, true]", program.getCommaSeparatedResult())
    }

    @Test
    fun `all dyad returns empty list(which is falsy) if all false`() {
        val source = "A>0"
        val input = listOf(1, -1, 90)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("", program.getCommaSeparatedResult())
    }

    @Test
    fun `all dyad returns the list as is(which is truthy) if all true`() {
        val source = "A>0"
        val input = listOf(1, 13, 9)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("1, 13, 9", program.getCommaSeparatedResult())
    }

    @Test
    fun `any dyad returns empty list(which is falsy) if any false`() {
        val source = "Ä>0"
        val input = listOf(-1, -1, -5)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("", program.getCommaSeparatedResult())
    }

    @Test
    fun `any dyad returns the list as is(which is truthy) if any true`() {
        val source = "Ä>0"
        val input = listOf(-1, 13, -9)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("-1, 13, -9", program.getCommaSeparatedResult())
    }

    //https://math.stackexchange.com/questions/2848284/oeis-database-download

    @Test
    fun `if else branch`() {
        val source = "Mv>0f\"above zero\"\"below zero\""
        val input = listOf(-1, 13, -9)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("below zero, above zero, below zero", program.getCommaSeparatedResult())
    }

    @Test
    fun `generate alphabet loops if size is longer than alphabet`() {
        val source = ";2b30"
        val input = listOf(1)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals(
            "Α, Β, Γ, Δ, Ε, Ζ, Η, Θ, Ι, Κ, Λ, Μ, Ν, Ξ, Ο, Π, Ρ, Σ, Τ, Υ, Φ, Χ, Ψ, Ω, Α, Β, Γ, Δ, Ε, Ζ",
            program.getCommaSeparatedResult()
        )
    }

    @Test
    fun `take 5 elements`() {
        val source = "[5"
        val input = listOf(1, 1, 1, 1, 2, 2, 2, 2)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("1, 1, 1, 1, 2", program.getCommaSeparatedResult())
    }

    @Test
    fun `drop 5 elements`() {
        val source = "]5"
        val input = listOf(1, 1, 1, 1, 2, 2, 2, 2)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("2, 2, 2", program.getCommaSeparatedResult())
    }

    @Test
    fun `oeis generation generates sequence of correct size`() {
        val source = ";145o5"
        val input = listOf(1)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("1, 24, 264, 1760, 7944", program.getCommaSeparatedResult())
    }

    @Test
    fun `pad start to size`() {
        val source = "#1,4"
        val input = listOf(2)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("1, 1, 1, 2", program.getCommaSeparatedResult())
    }

    @Test
    fun `pad end to size`() {
        // Pad end by supplying negative length
        val source = "#\"abc\"-4"
        val input = listOf(2)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("2, abc, abc, abc", program.getCommaSeparatedResult())
    }

    @Test
    fun `distinct values of a list removes duplicates `() {
        val source = "d"
        val input = listOf(2, 1, 2, 4, 5, 5)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("2, 1, 4, 5", program.getCommaSeparatedResult())
    }

    @Test
    fun `remove distinct values of only keeps duplicates `() {
        val source = "n"
        val input = listOf(2, 1, 2, 4, 5, 5)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("2, 2, 5, 5", program.getCommaSeparatedResult())
    }

    @Test
    fun `string to list monad`() {
        val source = ";\"abcde\"t"
        val input = listOf(2)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("a, b, c, d, e", program.getCommaSeparatedResult())
    }

    @Test
    fun `remove consecutive identical elements`() {
        val source = "c"
        val input = listOf(4, 2, 1, 2, 2, 4, 5, 5)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("4, 2, 1, 2, 4, 5", program.getCommaSeparatedResult())
    }

    @Test
    fun `join list to hyphen separated string`() {
        val source = "s\"-\""
        val input = listOf(4, 2, 5)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("4-2-5", program.getCommaSeparatedResult())
    }

    @Test
    fun `join list to string`() {
        val source = "s\"\""
        val input = listOf(4, 2, true, " hi")
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("42true hi", program.getCommaSeparatedResult())
    }

    @Test
    fun `to grouped string list, groups consecutive items of truthy predicate`() {
        val source = "G>2&<8"
        val input = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("1, 2, 34567, 8, 9, 0, 1, 2, 34", program.getCommaSeparatedResult())
    }

    @Test
    fun `to grouped string list, groups consecutive items after toggly truthy predicate`() {
        val source = "1G=3|=5|=7"
        val input = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("1, 2, 345, 6, 7890123, 4", program.getCommaSeparatedResult())
    }

    @Test
    fun `to upper case`() {
        val source = "Mk"
        val input = listOf("hellO", "there!")
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("HELLO, THERE!", program.getCommaSeparatedResult())
    }

    @Test
    fun `to lower case`() {
        val source = "Mw"
        val input = listOf("hELlO", "thERe!")
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("hello, there!", program.getCommaSeparatedResult())
    }

    @Test
    fun `is upper case`() {
        val source = "My"
        val input = listOf("LO", "thERe!", "", "1")
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("true, false, true, false", program.getCommaSeparatedResult())
    }

    @Test
    fun `use static storage to use things from one context in another`() {
        // Map (:) -> store value (x) -> obliterate return value from store operation return in favor of inner function resolved value
        val source = "M:x(M+?)"
        val input = listOf(1, 2, 3)
        val program = Du81Program(source, source.lex(), listOf(input))

        program.runForInput()

        assertEquals("[2, 3, 4], [3, 4, 5], [4, 5, 6]", program.getCommaSeparatedResult())
    }

}
