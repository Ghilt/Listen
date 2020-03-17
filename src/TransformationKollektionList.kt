fun <T, R> Collection<T>.deferMap(
    deferPredicate: (T) -> Boolean,
    transformDeferred: (List<T>) -> R,
    transform: (T) -> R = { v -> transformDeferred(listOf(v))}
): List<R> {
    val accumulator = DeferAccumulator(deferPredicate, transform, transformDeferred)
    this.fold(accumulator) { acc, item ->
        acc.accumulate(item)
    }
    return accumulator.finalize()
}