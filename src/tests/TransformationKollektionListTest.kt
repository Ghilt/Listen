package tests

import deferFlatMap
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
    fun `deferFlatMap work on empty list`() {
        val p = { _: Int -> true }
        val p2 = { _: List<Int> -> listOf(true) }
        val empty = listOf<Int>().deferFlatMap(p, p2)

        assertEquals(listOf<Int>(), empty)
    }

    @Test
    fun `deferMap by toggle work on empty list`() {
        val p = { _: Int -> true }
        val p2 = { _: List<Int> -> true }
        val empty = listOf<Int>().deferMap(p, p2, true)

        assertEquals(listOf<Int>(), empty)
    }

    @Test
    fun `deferMap defers transformation on list of characters and digits`() {
        val predicate = Char::isDigit
        val transform = { c: Char -> "$c" }
        val deferredTransform = { c: List<Char> -> c.joinToString("") }
        val result = listOf('a', '1', '2', 'm', '_', '1', '_').deferMap(predicate, deferredTransform, false, transform)

        assertEquals(listOf("a", "12", "m", "_", "1", "_"), result)
    }

    @Test
    fun `deferMap defers transformation on a list with only one element`() {
        val predicate = Char::isDigit
        val deferredTransform = { c: List<Char> -> c.joinToString("") }
        val result = listOf('1').deferMap(predicate, deferredTransform)

        assertEquals(listOf("1"), result)
    }

    @Test
    fun `deferMap by toggle defers transformation on list of characters`() {
        val predicate = { c: Char -> c == '|' }
        val transform = { c: Char -> "$c" }
        val deferredTransform = { c: List<Char> -> c.joinToString("_") }
        val result = "abc|def|ghijk||lmn|o|pq".toList().deferMap(predicate, deferredTransform, true, transform)

        assertEquals("abc|_d_e_f_|ghijk|_|lmn|_o_|pq", result.joinToString(""))
    }

    @Test
    fun `deferMap by toggle includes section after straggling marker`() {
        val predicate = { c: Char -> c == '|' }
        val transform = { c: Char -> "$c" }
        val deferredTransform = { c: List<Char> -> c.joinToString("_") }
        val result = "abc|imtoggled".toList().deferMap(predicate, deferredTransform, true, transform)

        assertEquals("abc|_i_m_t_o_g_g_l_e_d", result.joinToString(""))
    }

    @Test
    fun `deferFlatMap by toggle can remove toggled sections`() {
        val predicate = { c: Char -> c == '|' }
        val transform = { c: Char -> listOf("$c") }
        val deferredTransform = { _: List<Char> -> listOf<String>() }
        val result = "abc|def|ghijk||lmn|o|pq".toList().deferFlatMap(predicate, deferredTransform, true, transform)

        assertEquals("abcghijklmnpq", result.joinToString(""))
    }

    @Test
    fun `deferFlatMap can remove predicated characters`() {
        val predicate = { c: Char -> c == '|' }
        val transform = { c: Char -> listOf("$c") }
        val deferredTransform = { _: List<Char> -> listOf<String>() }
        val result =
            "abc|def|ghijk||lmn|o|pq".toList().deferFlatMap(predicate, deferredTransform, transform = transform)

        assertEquals("abcdefghijklmnopq", result.joinToString(""))
    }
}