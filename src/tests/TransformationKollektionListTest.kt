package tests

import deferMap
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TransformationKollektionListTest {

    @Test
    fun `deferMap work on empty list`() {
        val p = { _: Int -> true }
        val p2 = { _: List<Int> -> true }
        val empty = listOf<Int>().deferMap(p, p2)

        assertEquals(listOf<Int>(), empty)
    }

    @Test
    fun `deferMap defers transformation on list of characters and digits`() {
        val predicate = Char::isDigit
        val transform = { x: Char -> "$x" }
        val deferredTransform = { x: List<Char> -> x.joinToString("") }
        val result = listOf('a', '1', '2', 'm', '_', '1', '_').deferMap(predicate, deferredTransform, transform)

        assertEquals(listOf("a", "12", "m", "_", "1", "_"), result)
    }

    @Test
    fun `deferMap defers transformation on a list with only one element`() {
        val predicate = Char::isDigit
        val deferredTransform = { x: List<Char> -> x.joinToString("") }
        val result = listOf('1').deferMap(predicate, deferredTransform)

        assertEquals(listOf("1"), result)
    }
}