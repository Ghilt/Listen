package tests

import collectionlib.filterBasedOnNeighbors
import collectionlib.filterBasedOnNeighborsCyclic
import collectionlib.filterSectioned
import collectionlib.filterWithNeighbors
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FilterKollektionListTest {

    @Test
    fun `filterWithNeighbors work on empty list`() {
        val empty = listOf<Int>().filterWithNeighbors(1, 2) { x -> x == 1 }

        assertEquals(listOf<Int>(), empty)
    }

    @Test
    fun `filterBasedOnNeighborsCyclic work on empty list`() {
        val empty = listOf<Int>().filterWithNeighbors(1, 2) { x -> x == 1 }

        assertEquals(listOf<Int>(), empty)
    }

    @Test
    fun `filterSectioned work on empty list`() {
        val empty = listOf<Int>().filterSectioned { it != 0 }

        assertEquals(listOf<Int>(), empty)
    }

    @Test
    fun `filterSectioned splits list into sections with filtered elements not present`() {
        val empty = listOf(1, 2, 4, 0, 5, 0, 2, 6, 3, 0, 100, 0, 0, 0, 0).filterSectioned { it != 0 }

        assertEquals(listOf(listOf(1, 2, 4), listOf(5), listOf(2, 6, 3), listOf(100)), empty)
    }

    @Test
    fun `filterWithNeighbors on string`() {
        val result = "aaabbbcccddddeeeb".filterWithNeighbors(1, 2) { x -> x == 'b' }

        assertEquals("abbbcceb", result)
    }

    @Test
    fun `filterBasedOnNeighbors finds biggest int among neighbors`() {
        val biggerThanNeighbors =
            listOf(1, 2, 3, 5, 1, 33, 4, 53, 43, 1, 1, 1).filterBasedOnNeighbors { prev, v, next ->
                prev ?: Int.MAX_VALUE < v && v > next ?: Int.MAX_VALUE
            }

        assertEquals(listOf(5, 33, 53), biggerThanNeighbors)
    }

    @Test
    fun `filterBasedOnNeighbors with default edge values depends on neighbors`() {
        val biggerThanPreviousSmallerThanFollowing = listOf(1, 2, 3, 5, 1, 33, 4, 53, 43, 1, 1, 2)
            .filterBasedOnNeighbors(0, 0) { prev, v, next -> v in (1 + prev) until next }

        assertEquals(listOf(1, 2, 3), biggerThanPreviousSmallerThanFollowing)
    }

    @Test
    fun `filterBasedOnNeighbors on set with default edge values finds all dividends`() {
        val isDivisibleByNeighbors = setOf(1, 33, 3, 4, 5, 6, 7, 8, 9, 81, 3)
            .filterBasedOnNeighbors(1, 1) { prev, v, next -> v % prev == 0 && v % next == 0 }

        assertEquals(listOf(33, 81), isDivisibleByNeighbors)
    }

    @Test
    fun `filterBasedOnNeighborsCyclic finds all adjacent pairs`() {
        val isDivisibleByNeighbors = listOf(1, 2, 3, 3, 1, 3, 4, 5, 5, 1)
            .filterBasedOnNeighborsCyclic { prev, v, next -> v == prev || v == next }

        assertEquals(listOf(1, 3, 3, 5, 5, 1), isDivisibleByNeighbors)
    }
}