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

fun <T> Collection<T>.toGroupedStringList(
    deferPredicateIsByToggle: Boolean = false,
    deferPredicate: (T) -> Boolean
): List<String> {
    return deferMap(
        deferPredicate = deferPredicate,
        deferPredicateIsByToggle = deferPredicateIsByToggle,
        transform = { v -> v.toString() },
        transformDeferred = { items -> items.joinToString("") }
    )
}

fun <T> Collection<T>.joinNeighbors(
    predicate: (T, T, T) -> Boolean,
    joinFunction: (T, T, T) -> T,
): Collection<T> {
    // ^___ ^
    data class Neighbor(val value: T, val isOriginal: Boolean)
    fun List<Neighbor>.left() = this[size - 3]
    fun List<Neighbor>.middle() = this[size - 2]
    fun List<Neighbor>.right() = this[size - 1]
    val nowJoined = this.fold(mutableListOf()) { acc: MutableList<Neighbor>, t: T ->
        acc.apply {
            add(Neighbor(t, true))
            if (size >= 3
                && left().isOriginal
                && middle().isOriginal
                && right().isOriginal
                && predicate(left().value, middle().value, right().value)
            ) {
                val joined = joinFunction(left().value, middle().value, right().value)
                acc.removeAt(size - 3)
                acc.removeAt(size - 2)
                acc.removeAt(size - 1)
                add(Neighbor(joined, false))
            }
        }
    }

    return nowJoined.map { it.value }
}
