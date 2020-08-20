package tests

import compressedlang.Du81Program
import compressedlang.lex

fun runSeveralProgramsOnTheSameInput(input: List<Int>, vararg programs: String): List<List<Any>> {
    return programs.map {
        Du81Program(it, it.lex(), listOf(input)).apply { runForInput() }.getResult()[0]
    }
}

fun runSeveralTimesForDifferentInput(program: String, inputs: List<TestRun>): List<CompletedTestRun> {
    return inputs.map {
        it.withResult(Du81Program(program, program.lex(), it.inputs.toList()).apply { runForInput() }
            .getResultAsString())
    }
}

class TestRun(private val expectedResult: Any, vararg input: List<Any>) {

    val inputs: Array<List<Any>> = arrayOf(*input)

    fun withResult(result: String) = CompletedTestRun(expectedResult, result = result, input = inputs.toList())
}

class CompletedTestRun(val expectedResult: Any, val result: Any, @Suppress("UNUSED_PARAMETER") input: List<List<Any>>)

fun assertAllSuccessful(results: List<CompletedTestRun>) {
    val nonEqualElements = results.withIndex().filter {
        it.value.result != it.value.expectedResult
    }

    if (nonEqualElements.isNotEmpty()) {

        val errorMessage = nonEqualElements.joinToString("\n")
        { iv ->
            "Item at position ${iv.index} " +
                    "was ${iv.value.result} " +
                    "but was expected to be ${iv.value.expectedResult}"
        }

        throw AssertionError("Results not as expected:\n$errorMessage")
    }
}

// TODO These functions are not test support functions anymore
fun Du81Program.getResultAsString() = this.getResult()[0].joinToString("")
fun Du81Program.getResultAsFirstElement() = this.getResult()[0].first().toString()
fun Du81Program.getResultAsLastElement() = this.getResult()[0].last().toString()
fun Du81Program.getCommaSeparatedResult() = this.getResult()[0].joinToString()

fun assertAllEquals(target: List<Int>, results: List<List<Any>>) {
    val nonEqualElements = results.withIndex().filter { iv ->
        iv.value != target
    }
    if (nonEqualElements.isNotEmpty()) {

        val errorMessage = nonEqualElements.joinToString("\n") { iv -> "Item at position ${iv.index} was ${iv.value}" }

        throw AssertionError("All items not equal to $target\n$errorMessage")
    }
}

fun expectException(function: () -> Unit): Any? {
    try {
        function()
    } catch (e: Throwable) {
        return e
    }
    return null
}