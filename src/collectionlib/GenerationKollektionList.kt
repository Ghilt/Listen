package collectionlib

// TODO: should not need to produce a collection of the input type
fun <T> Collection<T>.extendEntries(
    length: Int,
    fillFunction: (index: Int, T) -> T = { _, v -> v }
): Collection<T> = flatMap { x -> List(1 + length) { i -> fillFunction(i, x) } }

fun <T> List<T>.extendEntries(
    lengths: List<Int>,
    fillFunction: (index: Int, T) -> T = { _, v -> v }
): List<T> = withIndex().flatMap { x -> List(1 + lengths[x.index % lengths.size]) { i -> fillFunction(i, x.value) } }

fun String.extendEntries(
    length: Int,
    fillFunction: (Int, Char) -> Char = { _, v -> v }
): String = toList().extendEntries(length, fillFunction).joinToString("")

fun List<Number>.growEntries(
    length: Int,
    step: Int = 1
): List<Number> = flatMap { x -> List(1 + length) { i -> x + i * step } }

fun String.growEntries(
    length: Int,
    step: Int = 1
): String = toList().flatMap { x -> List(1 + length) { i -> x + i * step } }.joinToString("")

fun Collection<Number>.growEntriesBothDirections(
    lengthDescend: Int,
    lengthAscend: Int,
    step: Int = 1
): Collection<Number> = flatMap { x -> List(1 + lengthAscend + lengthDescend) { i -> x + (i - lengthDescend) * step } }

fun Collection<Number>.growEntriesBothDirections(
    length: Int,
    step: Int = 1
): Collection<Number> = growEntriesBothDirections(length, length, step)

fun String.growEntriesBothDirections(
    lengthDescend: Int,
    lengthAscend: Int,
    step: Int = 1
): String = toList()
    .flatMap { x -> List(1 + lengthAscend + lengthDescend) { i -> x + (i - lengthDescend) * step } }
    .joinToString("")

fun String.growEntriesBothDirections(
    length: Int,
    step: Int = 1
): String = growEntriesBothDirections(length, length, step)

fun List<Number>.growFractal(
    steps: Int = 1
): List<Number> = if (steps == 0) this else flatMap { x -> List(size) { i -> (x * this[i]) } }.growFractal(steps - 1)




