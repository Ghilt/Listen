package tests.compressedlang

import compressedlang.Du81ProgramEnvironment
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import tests.TestRun
import tests.assertAllSuccessful
import tests.runSeveralTimesForDifferentInput

internal class Du81ProgramGolfTest {

    @BeforeEach
    fun initiateEnvironment() {
        Du81ProgramEnvironment.initialize()
    }

    @AfterEach
    fun makeStaticSingletonTestable() {
        Du81ProgramEnvironment.for_test_only_resetEnvironment()
    }

    // https://codegolf.stackexchange.com/questions/209579/rotate-a-number
    @Test
    fun `Rotate a number golf`() {
        val source = "cα;θ2\$cD=0"

        val result = runSeveralTimesForDifferentInput(
            source,
            listOf(
                TestRun("312", listOf(123), listOf(1)),
                TestRun("231", listOf(123), listOf(2)),
                TestRun("123", listOf(123), listOf(3)),
                TestRun("312", listOf(123), listOf(4)),
                TestRun("1", listOf(1), listOf(637)),
                TestRun("1", listOf(10), listOf(1)),
                TestRun("1", listOf(100), listOf(2)),
                TestRun("10", listOf(10), listOf(2)),
                TestRun("101", listOf(110), listOf(2)),
                TestRun("123", listOf(123), listOf(0)),
                TestRun("9899", listOf(9998), listOf(2)),
            )
        )

        assertAllSuccessful(result)
    }
}
