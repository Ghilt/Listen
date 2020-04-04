package tests.compressedlang

import compressedlang.Precedence
import compressedlang.TYPE.*
import compressedlang.TypeRequirements
import compressedlang.doSimplificationPass
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class TypeRequirementsPrecedenceTest {

    @Test
    fun `requirement of high precedence gets simplified before one of low precedence earlier in list`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = INT).apply {
            isWeaklyRequired = INT
        })

        list.add(TypeRequirements(provides = STRING, precedence = Precedence.LOWEST).apply {
            requiresWeaklyByPrevious = INT
            requiresByOther(INT, 1)
        })

        list.add(TypeRequirements(provides = INT).apply {
            isRequiredBy(INT, -1)
            isWeaklyRequired = INT
        })

        list.add(TypeRequirements(provides = INT).apply {
            requiresWeaklyByPrevious = INT
            requiresByOther(INT, 1)
        })

        list.add(TypeRequirements(provides = INT).apply {
            isRequiredBy(INT, -1)
        })


        val (didChange, result) = list.doSimplificationPass()

        assertEquals(true, didChange)
        assertEquals(STRING, result[1].provides)
    }

    @Test
    fun `requirements of similar precedence gets processed in turn`() {
        val list = mutableListOf<TypeRequirements>()
        list.add(TypeRequirements(provides = INT, precedence = Precedence.LOW).apply {
            isWeaklyRequired = INT
        })

        list.add(TypeRequirements(provides = INT, precedence = Precedence.MEDIUM).apply {
            requiresWeaklyByPrevious = INT
            requiresByOther(INT, 1)
        })

        list.add(TypeRequirements(provides = INT, precedence = Precedence.LOWEST).apply {
            isRequiredBy(INT, -1)
            isWeaklyRequired = INT
        })

        list.add(TypeRequirements(provides = INT, precedence = Precedence.MEDIUM).apply {
            requiresWeaklyByPrevious = INT
            requiresByOther(INT, 1)
        })

        list.add(TypeRequirements(provides = INT, precedence = Precedence.HIGH).apply {
            isRequiredBy(INT, -1)
        })


        val (didChange, result) = list.doSimplificationPass().second.doSimplificationPass()

        assertEquals(true, didChange)
        assertEquals(1, result.size)
    }

}