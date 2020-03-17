fun <T, R> Collection<T>.deferMap(
    deferPredicate: (T) -> Boolean,
    transformDeferred: (List<T>) -> R,
    deferPredicateIsByToggle: Boolean = false,
    transform: (T) -> R = { v -> transformDeferred(listOf(v)) }
): List<R> {
    val accumulator = DeferAccumulator(deferPredicate, deferPredicateIsByToggle, transform, transformDeferred)
    this.fold(accumulator) { acc, item ->
        acc.accumulate(item)
    }
    return accumulator.finalize()
}

fun <T, R> Collection<T>.deferFlatMap(
    deferPredicate: (T) -> Boolean,
    transformDeferred: (List<T>) -> Iterable<R>,
    deferPredicateIsByToggle: Boolean = false,
    transform: (T) -> Iterable<R> = { v -> transformDeferred(listOf(v)) }
): List<R> {
    val accumulator = DeferAccumulator(deferPredicate, deferPredicateIsByToggle, transform, transformDeferred)
    this.fold(accumulator) { acc, item ->
        acc.accumulate(item)
    }
    return accumulator.finalize().flatten()
}