package tests

import collectionlib.extendEntries
import collectionlib.growEntries
import collectionlib.growEntriesBothDirections
import collectionlib.growFractal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class GenerationKollektionListTest {

    @Test
    fun `extendEntries on string by 2 doubles every list item`() {
        val result = "abcde".extendEntries(1)
        assertEquals("aabbccddee", result)
    }

    @Test
    fun `extendEntries by 100 increases list size by 100`() {
        val original = listOf(1, 2, 3, 3, 3, 3, 3, 1, 5, 6)
        val result = original.extendEntries(100)
        assertEquals(original.size * 101, result.size)
        assertEquals(List(101) { 1 }, result.take(101))
    }

    @Test
    fun `extendEntries and fill by decreasing numbers`() {
        val original = listOf(100, 200, 300)
        val result = original.extendEntries(3) { i, v -> v - i }
        assertEquals(listOf(100, 99, 98, 97, 200, 199, 198, 197, 300, 299, 298, 297), result)
    }

    @Test
    fun `growEntries grows by correct step`() {
        val original = listOf(1, 3, 8)
        val result = original.growEntries(3, 2)
        assertEquals(listOf(1, 3, 5, 7, 3, 5, 7, 9, 8, 10, 12, 14), result)
    }

    @Test
    fun `growEntries grows negatively with negative step`() {
        val original = listOf(1, 10, 100)
        val result = original.growEntries(1, -10)
        assertEquals(listOf(1, -9, 10, 0, 100, 90), result)
    }

    @Test
    fun `growEntries on string correctly 'increases' char values`() {
        val result = "abcdef".growEntries(1, 1)
        assertEquals("abbccddeeffg", result)
    }

    @Test
    fun `growEntriesBothDirections `() {
        val result = listOf(1, 10).growEntriesBothDirections(1, 1)
        assertEquals(listOf(0, 1, 2, 9, 10, 11), result)
    }

    @Test
    fun `growEntriesBothDirections on string`() {
        val result = "a6b".growEntriesBothDirections(2, 1)
        assertEquals("_`abc45678`abcd", result)
    }

    @Test
    fun `growEntriesBothDirections on string with different lengths ascending and descending`() {
        val result = "b".growEntriesBothDirections(1, 5, 1)
        assertEquals("abcdefg", result)
    }

    @Test
    fun `growEntriesBothDirections with different lengths ascending and descending`() {
        val result = listOf(10000, 10).growEntriesBothDirections(5, 1, 2)
        assertEquals(listOf(9990, 9992, 9994, 9996, 9998, 10000, 10002, 0, 2, 4, 6, 8, 10, 12), result)
    }

    @Test
    fun `growFractal on int`() {
        val result = listOf(1, 2, 1).growFractal(1)
        assertEquals(listOf(1, 2, 1, 2, 4, 2, 1, 2, 1), result)
    }

    @Test
    fun `growFractal on double`() {
        val result = listOf(0.9, 1.5, 0.9).growFractal(1)
        assertEquals(listOf(0.81, 1.35, 0.81, 1.35, 2.25, 1.35, 0.81, 1.35, 0.81), result)
    }
}