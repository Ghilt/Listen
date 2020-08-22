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
    fun `rotate a number`() {
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

    // https://codegolf.stackexchange.com/questions/132926/triple-balanced-numbers
    @Test
    fun `triple balanced numbers`() {
//  1st attempt      val source = "cα;_0(l/3):Gi≥pö&i≤(2*p-1)öGi≠l¤2MαΣA=c"
//  2nd attempt      val source = "cα;G(l/3):xi≥pö&i≤(2*p-1)öGi≠l¤2MαΣA=c"
        val source = "cα;Gi≥l/3ö&i≤(2*l/3-1)öGi≠l¤2MαΣA=c"

        /*
        *  cα;                   - convert to digit list
        *  Gi≥l/3ö&i≤(2*l/3-1)ö  - Use maths to discover start and end of middle section to group the middle number
        *  Gi≠l¤2                - Group every item but the middle one(which is many digits combined in previous step)
        *  MαΣ                   - Map to digit sum
        *  A=c                   - All sums equals to first sum of list
        */

        val result = runSeveralTimesForDifferentInput(
            source,
            listOf(
                TestRun("", listOf(312)),
                TestRun("", listOf(2312)),
                TestRun("", listOf(23123)),
                TestRun("", listOf(231234)),
                TestRun("", listOf(2312352)),
                TestRun("", listOf(23123521)),
                TestRun("", listOf(231235212)),
                TestRun("333", listOf(333)),
                TestRun("", listOf(343)),
                TestRun("333", listOf(3123)),
                TestRun("777", listOf(34725)),
                TestRun("", listOf(456456)),
                TestRun("666", listOf(123222321)),
            )
        )

        assertAllSuccessful(result)
    }
}
