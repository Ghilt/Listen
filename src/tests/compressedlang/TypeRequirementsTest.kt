package tests.compressedlang

import compressedlang.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@Suppress("PrivatePropertyName")
internal class TypeRequirementsTest {

    private val PREVIOUS = -1
    private val NEXT = 1

    @Test
    fun `basic requirement is recognized as fulfilled`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresByOther(TYPE.NUMBER, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.NUMBER))

        val result = list.isFulfilledAt(0)

        assertEquals(true, result)
    }

    @Test
    fun `basic requirement is recognized as unfulfilled`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresByOther(TYPE.NUMBER, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.STRING))

        val result = list.isFulfilledAt(0)

        assertEquals(false, result)
    }

    @Test
    fun `two step requirement is recognized as fulfilled`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresByOther(TYPE.NUMBER, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresByOther(TYPE.STRING, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.STRING))

        val result = list.isFulfilledAt(0)

        assertEquals(true, result)
    }

    @Test
    fun `three step requirement on item outside of list is recognized as unfulfilled`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresByOther(TYPE.NUMBER, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresByOther(TYPE.BOOL, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.BOOL).apply {
            requiresByOther(TYPE.LIST_TYPE, NEXT)
            requiresByOther(TYPE.BOOL, 2)
        })

        list.add(TypeRequirements(provides = TYPE.LIST_TYPE))

        val result = list.isFulfilledAt(0)

        assertEquals(false, result)
    }

    @Test
    fun `simplify removes implicit receiver type if possible`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = TYPE.NUMBER))

        list.add(TypeRequirements(provides = TYPE.BOOL).apply {
            isWeaklyRequired = TYPE.BOOL
        })

        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresWeaklyByPrevious = TYPE.BOOL
            requiresByOther(TYPE.BOOL, PREVIOUS)
            requiresByOther(TYPE.STRING, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.STRING))

        list.add(TypeRequirements(provides = TYPE.NUMBER))

        val result = list.simplify(2)

        assertEquals(3, result.size)
        assertEquals(TYPE.NUMBER, result[0].provides)
        assertEquals(TYPE.NUMBER, result[1].provides)
        assertEquals(TYPE.NUMBER, result[2].provides)
        assertEquals(mutableListOf<Pair<TYPE, Int>>(), result[1].requiresByOthers)
        assertEquals(mutableListOf<Pair<TYPE, Int>>(), result[1].isRequiredBy)
    }

    @Test
    fun `simplify ignores implicit receiver if wrong type`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            isWeaklyRequired = TYPE.BOOL
        })

        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
           requiresWeaklyByPrevious = TYPE.BOOL
            requiresByOther(TYPE.STRING, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.STRING).apply {
            isRequiredBy(TYPE.STRING, PREVIOUS)
        })


        val result = list.simplify(1)

        assertEquals(2, result.size)
        assertEquals(TYPE.NUMBER, result[0].provides)
        assertEquals(TYPE.NUMBER, result[1].provides)
        assertEquals(mutableListOf<Pair<TYPE, Int>>(), result[1].requiresByOthers)
        assertEquals(mutableListOf<Pair<TYPE, Int>>(), result[1].isRequiredBy)
    }

    @Test
    fun `simplify on type with no requirements does nothing`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = TYPE.NUMBER))

        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresByOther(TYPE.BOOL, PREVIOUS)
            requiresByOther(TYPE.STRING, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.STRING))

        val result = list.simplify(2)

        assertEquals(3, result.size)
        assertEquals(TYPE.NUMBER, result[0].provides)
        assertEquals(TYPE.NUMBER, result[1].provides)
        assertEquals(TYPE.STRING, result[2].provides)
    }

    @Test
    fun `out of bounds type requirement null provider is not simplifiable`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = TYPE.LIST_TYPE))

        list.add(TypeRequirements(provides = TYPE.LIST_TYPE).apply {
            requiresByOther(TYPE.BOOL, NEXT)
        })

        list.add(TypeRequirements(provides = null).apply {
            isRequiredBy(TYPE.BOOL, NEXT)
        })

        val result = list.isSimplifiableAt(2)

        assertEquals(false, result)
    }

    @Test
    fun `list provider type requirement null provider is not simplifiable`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = TYPE.LIST_TYPE))
        val result = list.isSimplifiableAt(0)

        assertEquals(false, result)
    }

    @Test
    fun `recalculating isRequiredBy correctly sets up dependencies`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = TYPE.NUMBER))

        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresWeaklyByPrevious = TYPE.BOOL
            requiresByOther(TYPE.STRING, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.STRING).apply {
            requiresWeaklyByPrevious = TYPE.NUMBER
            requiresByOther(TYPE.STRING, NEXT)
            requiresByOther(TYPE.STRING, NEXT + 1)
        })

        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresByOther(TYPE.NUMBER, NEXT)
        })

        list.add(TypeRequirements(provides = TYPE.NUMBER))

        val result = list.recalculateIsRequiredByInformation()

        assertEquals(TYPE.BOOL, result[0].isWeaklyRequired)
        assertEquals(TYPE.NUMBER, result[1].isWeaklyRequired)
        assertEquals(TYPE.STRING, result[2].isRequiredBy[0].first)
        assertEquals(PREVIOUS, result[2].isRequiredBy[0].second)
        assertEquals(TYPE.STRING, result[3].isRequiredBy[0].first)
        assertEquals(PREVIOUS, result[3].isRequiredBy[0].second)
        assertEquals(TYPE.STRING, result[4].isRequiredBy[0].first)
        assertEquals(PREVIOUS - 1, result[4].isRequiredBy[0].second)
        assertEquals(TYPE.NUMBER, result[4].isRequiredBy[1].first)
        assertEquals(PREVIOUS, result[4].isRequiredBy[1].second)
    }


    @Test
    fun `big requirer can be fulfilled`() {
        val list = mutableListOf<TypeRequirements>()

        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresByOther(TYPE.STRING, NEXT)
            requiresByOther(TYPE.NUMBER, NEXT + 1)
            requiresByOther(TYPE.LIST_TYPE, NEXT + 2)
            requiresByOther(TYPE.NUMBER, NEXT + 3)
        })

        list.add(TypeRequirements(provides = TYPE.STRING))
        list.add(TypeRequirements(provides = TYPE.NUMBER))
        list.add(TypeRequirements(provides = TYPE.LIST_TYPE))
        list.add(TypeRequirements(provides = TYPE.NUMBER).apply {
            requiresByOther(TYPE.LIST_TYPE, PREVIOUS)
            requiresByOther(TYPE.NUMBER, PREVIOUS - 1)
            requiresByOther(TYPE.STRING, PREVIOUS - 2)
            requiresByOther(TYPE.NUMBER, PREVIOUS - 3)
        })

        val result = list.areAllFulfilled()

        assertEquals(true, result)
    }
}