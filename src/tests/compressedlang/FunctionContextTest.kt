package tests.compressedlang

import compressedlang.*
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
        functionContext.put(1.toResolvedFunction())

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(2.toResolvedFunction())

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
        functionContext.put(1.toResolvedFunction())

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(2.toResolvedFunction())

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(3.toResolvedFunction())

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
        functionContext.put(1.toResolvedFunction())

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(2.toResolvedFunction())

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(3.toResolvedFunction())

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(4.toResolvedFunction())

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
        functionContext.put(1.toResolvedFunction())

        functionContext.put(lengthMonad)
        functionContext.put(largerThanDyad)
        functionContext.put(2.toResolvedFunction())

        val willAcceptContextCreator = functionContext.willAccept(filterDyad)

        assertEquals(false, willAcceptContextCreator)
    }

    @Test
    fun `execute filter on index yields filtered list`() {
        val functionContext = FunctionContext(listOf("12345".toListDu81List()), currentListNilad)
        functionContext.put(filterDyad)
        functionContext.put(largerThanDyad)
        functionContext.put(1.toResolvedFunction())

        val list = functionContext.execute()

        assertEquals("3, 4, 5", list.list.joinToString())
    }
}
