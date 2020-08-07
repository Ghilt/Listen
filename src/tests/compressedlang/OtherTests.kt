package tests.compressedlang

import compressedlang.readOeisSequence
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class OtherTests {

    @Test
    fun `read last oeis sequence from file`() {
        val sequence = readOeisSequence(336879)

        Assertions.assertEquals(listOf(0, 1, 2, 3, 1), sequence.take(5))
    }
}