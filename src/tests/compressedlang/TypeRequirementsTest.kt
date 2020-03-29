package tests.compressedlang

import compressedlang.TYPE
import compressedlang.TypeRequirements
import compressedlang.simplify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TypeRequirementsTest {

    @Test
    fun `basic requirement is recognized as fulfilled`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements().apply {
            provides = TYPE.INT
            requires(TYPE.INT, 1)
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.INT
        })

        val result = list[0].isFulfilled(0, list)

        assertEquals(true, result)
    }

    @Test
    fun `basic requirement is recognized as unfulfilled`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements().apply {
            provides = TYPE.INT
            requires(TYPE.INT, 1)
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.STRING
        })

        val result = list[0].isFulfilled(0, list)

        assertEquals(false, result)
    }

    @Test
    fun `two step requirement is recognized as fulfilled`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements().apply {
            provides = TYPE.INT
            requires(TYPE.INT, 1)
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.INT
            requires(TYPE.STRING, 1)
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.STRING
        })

        val result = list[0].isFulfilled(0, list)

        assertEquals(true, result)
    }

    @Test
    fun `three step requirement on item outside of list is recognized as unfulfilled`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements().apply {
            provides = TYPE.INT
            requires(TYPE.INT, 1)
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.INT
            requires(TYPE.BOOL, 1)
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.BOOL
            requires(TYPE.LIST_TYPE, 1)
            requires(TYPE.BOOL, 2)
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.LIST_TYPE
        })

        val result = list[0].isFulfilled(0, list)

        assertEquals(false, result)
    }

    @Test
    fun `simplify removes implicit receiver type if possible`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements().apply {
            provides = TYPE.DOUBLE
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.BOOL
            weakRequirement = TYPE.BOOL
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.INT
            requiresWeaklyByOthers = TYPE.BOOL
            requires(TYPE.BOOL, -1)
            requires(TYPE.STRING, 1)
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.STRING
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.DOUBLE
        })

        val target: IndexedValue<TypeRequirements> = list.withIndex().elementAt(2)
        val result = list.simplify(target)

        assertEquals(3, result.size)
        assertEquals(TYPE.DOUBLE, result[0].provides)
        assertEquals(TYPE.INT, result[1].provides)
        assertEquals(TYPE.DOUBLE, result[2].provides)
        assertEquals(mutableListOf<Pair<TYPE, Int>>(), result[1].requiresByOthers)
        assertEquals(mutableListOf<Pair<TYPE, Int>>(), result[1].requires)
    }

    @Test
    fun `simplify ignores implicit receiver if wrong type`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements().apply {
            provides = TYPE.DOUBLE
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.INT
            requires(TYPE.BOOL, -1)
            requires(TYPE.STRING, 1)
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.STRING
        })


        val target: IndexedValue<TypeRequirements> = list.withIndex().elementAt(1)
        val result = list.simplify(target)

        assertEquals(2, result.size)
        assertEquals(TYPE.DOUBLE, result[0].provides)
        assertEquals(TYPE.INT, result[1].provides)
        assertEquals(mutableListOf<Pair<TYPE, Int>>(), result[1].requiresByOthers)
        assertEquals(mutableListOf<Pair<TYPE, Int>>(), result[1].requires)
    }

    @Test
    fun `simplify on type with no requirements does nothing`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements().apply {
            provides = TYPE.DOUBLE
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.INT
            requires(TYPE.BOOL, -1)
            requires(TYPE.STRING, 1)
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.STRING
        })

        val target: IndexedValue<TypeRequirements> = list.withIndex().elementAt(2)
        val result = list.simplify(target)

        assertEquals(3, result.size)
        assertEquals(TYPE.DOUBLE, result[0].provides)
        assertEquals(TYPE.INT, result[1].provides)
        assertEquals(TYPE.STRING, result[2].provides)
    }

    @Test
    fun `out of bounds type requirement null provider is not simplifiable`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements().apply {
            provides = TYPE.LIST_TYPE
        })

        list.add(TypeRequirements().apply {
            provides = TYPE.LIST_TYPE
            requires(TYPE.BOOL, 1)
        })

        list.add(TypeRequirements().apply {
            provides = null
            isRequiredOf(TYPE.BOOL, 1)
        })

        val result = list[2].isSimplifiable()

        assertEquals(false, result)
    }

    @Test
    fun `list provider type requirement null provider is not simplifiable`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements().apply {
            provides = TYPE.LIST_TYPE
        })

        val result = list[0].isSimplifiable()

        assertEquals(false, result)
    }

}