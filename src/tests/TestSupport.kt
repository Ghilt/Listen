package tests

import compressedlang.Du81Program
import compressedlang.lex

fun runSeveralProgramsOnTheSameInput(input: List<Int>, vararg programs: String): List<List<Any>> {
    return programs.map {
        Du81Program(it, it.lex(), input).apply { runForInput() }.getResult()[0]
    }
}

fun Du81Program.getResultAsString() = this.getResult()[0].joinToString("")

fun assertAllEquals(target: List<Int>, results: List<List<Any>>) {
    val nonEqualElements = results.withIndex().filter {
            iv -> iv.value != (target)
    }
    if (nonEqualElements.isNotEmpty()) {

        val errorMessage = nonEqualElements.joinToString("\n") { iv -> "Item at position ${iv.index} was ${iv.value}" }

        throw AssertionError("All items not equal to $target\n$errorMessage")
    }
}