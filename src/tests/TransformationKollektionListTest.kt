package tests

import collectionlib.deferFlatMap
import collectionlib.deferMap
import collectionlib.joinNeighbors
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import collectionlib.toGroupedStringList

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
    fun `toGroupedStringList by toggle work on empty list`() {
        val result = listOf<Int>().toGroupedStringList { item -> item == 0 || item == 9 }

        assertEquals(listOf<Int>(), result)
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

    @Test
    fun `toGroupedStringList groups items into strings`() {
        val result =
            listOf(1, 0, 2, 3, 9, 8, 9, 9, 5, 4, 9, 0, 9).toGroupedStringList(true) { item -> item == 0 || item == 9 }

        assertEquals("1, 0239, 8, 99, 5, 4, 90, 9", result.joinToString())
    }

    @Test
    fun `join neighbors works on empty list`() {
        val result = listOf<Int>().joinNeighbors({ _, _, _ -> true }) { a, b, c -> a + b + c}
        assertEquals("", result.joinToString())

    }

    @Test
    fun `join neighbors do not join doubly when adjacent predicates are true`() {
        val result = listOf(1,2,1,2,1,2,1).joinNeighbors({ _, b, _ -> b == 2 }) { a, b, c -> a + b + c}
        assertEquals("4, 2, 4", result.joinToString())

    }

    @Test
    fun `join neighbors joins single neighborhood `() {
        val result = listOf(1,2,3,4,5).joinNeighbors({ _, b, _ -> b == 3 }) { a, b, c -> a + b + c }
        assertEquals("1, 9, 5", result.joinToString())

    }
}