package tests.compressedlang

import compressedlang.*
import compressedlang.Number
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class FunctionContextTest {

    @Test
    fun `functionContext creates inner functions when it needs new inner contexts`() {
        val functionContext = FunctionContext(listOf("123".toListDu81List()), currentListNilad)
        functionContext.put(filterDyad)
        functionContext.put(currentListNilad)
        functionContext.put(filterDyad)
        functionContext.put(currentListNilad)
        functionContext.put(filterDyad)
        functionContext.put(currentListNilad)
        functionContext.put(filterDyad)

        assertEquals("_D(_D(_D(_D)))", functionContext.diagnosticsString())
    }

    @Test
    fun `functionContext creates inner function`() {
        val functionContext = FunctionContext(listOf("123".toListDu81List()), currentListNilad)
        functionContext.put(filterDyad)

        functionContext.put(currentListNilad)
        functionContext.put(filterDyad)

        functionContext.put(largerThanDyad)
        functionContext.put(Number(1))

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(Number(2))

        //             F_ F_ F_ F>1 L>2 L>3 L>4
        assertEquals("_D(_DDN)MDN", functionContext.diagnosticsString())
    }

    @Test
    fun `functionContext creates two inner functions`() {
        val functionContext = FunctionContext(listOf("123".toListDu81List()), currentListNilad)
        functionContext.put(filterDyad)

        functionContext.put(currentListNilad)
        functionContext.put(filterDyad)

        functionContext.put(currentListNilad)
        functionContext.put(filterDyad)
        functionContext.put(largerThanDyad)
        functionContext.put(Number(1))

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(Number(2))

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(Number(3))

        //             F_ F_ F_ F>1 L>2 L>3 L>4
        assertEquals("_D(_D(_DDN)MDN)MDN", functionContext.diagnosticsString())
    }

    @Test
    fun `functionContext creates inner functions`() {
        val functionContext = FunctionContext(listOf("123".toListDu81List()), currentListNilad)
        functionContext.put(filterDyad)

        functionContext.put(currentListNilad)
        functionContext.put(filterDyad)

        functionContext.put(currentListNilad)
        functionContext.put(filterDyad)

        functionContext.put(currentListNilad)
        functionContext.put(filterDyad)
        functionContext.put(largerThanDyad)
        functionContext.put(Number(1))

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(Number(2))

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(Number(3))

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(Number(4))

        //             F_ F_ F_ F>1 L>2 L>3 L>4
        assertEquals("_D(_D(_D(_DDN)MDN)MDN)MDN", functionContext.diagnosticsString())
    }

    @Test
    fun `functionContext creates inner functions and sequential functions`() {
        val functionContext = FunctionContext(listOf("123".toListDu81List()), currentListNilad)
        functionContext.put(filterDyad)

        functionContext.put(currentListNilad)
        functionContext.put(filterDyad)

        functionContext.put(largerThanDyad)
        functionContext.put(Number(1))

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(Number(2))

        val willAcceptContextCreator = functionContext.willAccept(filterDyad)

        assertEquals(false, willAcceptContextCreator)
    }
}
