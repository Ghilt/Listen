package compressedlang

import compressedlang.ContextKey.INDEX
import compressedlang.ContextKey.LIST
import compressedlang.Precedence.*
import compressedlang.TYPE.*

sealed class Function(val consumesList: Boolean = false) {
    abstract val inputs: List<TYPE>
    abstract val output: TYPE
}

data class Number(
    val number: kotlin.Number
) : Function() {
    override val inputs: List<TYPE>
        get() = listOf()
    override val output: TYPE
        get() = NUMBER
}

data class StringLiteral(
    val literal: String
) : Function() {
    override val inputs: List<TYPE>
        get() = listOf()
    override val output: TYPE
        get() = STRING
}

data class Nilad(
    val contextKey: ContextKey,
    override val output: TYPE
) : Function() {
    override val inputs: List<TYPE>
        get() = listOf()
}

data class Monad<I, O>(
    val default: Nilad,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    val f: (I) -> O
) : Function()

data class Dyad<I, I2, O>(
    val default: Nilad,
    val dyadConsume: Boolean = false,
    val precedence: Precedence = LOWEST,
    override val inputs: List<TYPE>,
    override val output: TYPE,
    val f: (I, I2) -> O
) : Function(dyadConsume)

data class InnerFunction(
    val index: Int
) : Function() {
    override val inputs: List<TYPE>
        get() = listOf(LIST_TYPE)

    override val output: TYPE
        get() = LIST_TYPE
}

val indexNilad = Nilad(INDEX, INT)
val currentListNilad = Nilad(LIST, LIST_TYPE)

val lengthMonad = Monad(currentListNilad, listOf(LIST_TYPE), INT) { list: List<*> -> list.size }

val multiplyDyad = Dyad<Double, Double, Double>(
    default = indexNilad,
    precedence = HIGHEST,
    inputs = listOf(DOUBLE, DOUBLE),
    output = DOUBLE
) { a, b -> a * b }

val largerThanDyad = Dyad<Double, Double, Boolean>(
    default = indexNilad,
    precedence = MEDIUM,
    inputs = listOf(DOUBLE, DOUBLE),
    output = BOOL
) { a, b -> a > b }

val smallerThanDyad = Dyad<Double, Double, Boolean>(
    default = indexNilad,
    precedence = MEDIUM,
    inputs = listOf(DOUBLE, DOUBLE),
    output = BOOL
) { a, b -> a < b }

val equalToDyad = Dyad<Double, Double, Boolean>(
    default = indexNilad,
    precedence = MEDIUM,
    inputs = listOf(DOUBLE, DOUBLE),
    output = BOOL
) { a, b -> a == b }

val filterDyad = Dyad<List<Any>, (Any) -> Boolean, List<Any>>(
    currentListNilad,
    dyadConsume = true,
    inputs = listOf(LIST_TYPE, BOOL),
    output = LIST_TYPE
) { a, b -> a.filter { b(it) } }

fun Function.usesNewContext(): Boolean {
    return this is Nilad && contextKey == LIST
}