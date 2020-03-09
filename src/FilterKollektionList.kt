fun <T> Collection<T>.filterWithNeighbors(
    neighborhoodSizeLeft: Int,
    neighborhoodSizeRight: Int,
    predicate: (T) -> Boolean
): Collection<T> {
    val indexes = this.withIndex().flatMap { x -> if (predicate(x.value)) listOf(x.index) else listOf() }
    val accepted = indexes.growEntriesBothDirections(neighborhoodSizeLeft, neighborhoodSizeRight, 1).toSet()
    return this.filterIndexed { index, _ -> accepted.contains(index) }
}

fun String.filterWithNeighbors(
    neighborhoodSizeLeft: Int,
    neighborhoodSizeRight: Int,
    predicate: (Char) -> Boolean
): String {
    return this.toList().filterWithNeighbors(neighborhoodSizeLeft, neighborhoodSizeRight, predicate).joinToString("")
}

fun <T> Collection<T>.filterBasedOnNeighbors(
    predicate: (T?, T, T?) -> Boolean
): Collection<T> {
    return this.filterIndexed { i, v ->
        when {
            i == 0 && this.size <= 1 -> predicate(null, v, null)
            i == 0 -> predicate(null, v, elementAt(i + 1))
            i == this.size - 1 -> predicate(elementAt(i - 1), v, null)
            else -> predicate(elementAt(i - 1), v, elementAt(i + 1))
        }
    }
}

fun <T> Collection<T>.filterBasedOnNeighbors(
    leftEdgeValue: T,
    rightEdgeValue: T = leftEdgeValue,
    predicate: (T, T, T) -> Boolean
): Collection<T> {
    return this.filterIndexed { i, v ->
        when {
            i == 0 && this.size <= 1 -> predicate(leftEdgeValue, v, rightEdgeValue)
            i == 0 -> predicate(leftEdgeValue, v, elementAt(i + 1))
            i == this.size - 1 -> predicate(elementAt(i - 1), v, rightEdgeValue)
            else -> predicate(elementAt(i - 1), v, elementAt(i + 1))
        }
    }
}

fun <T> Collection<T>.filterBasedOnNeighborsCyclic(
    predicate: (T, T, T) -> Boolean
): Collection<T> {
    return this.filterIndexed { i, v ->
        when {
            i == 0 && this.size <= 1 -> predicate(v, v, v)
            i == 0 -> predicate(last(), v, elementAt(i + 1))
            i == this.size - 1 -> predicate(elementAt(i - 1), v, first())
            else -> predicate(elementAt(i - 1), v, elementAt(i + 1))
        }
    }
}

fun <T> filterBasedOnNeighborsCyclicTEST(
    test: Collection<T>,
    predicate: (T, T, T) -> Boolean
): Collection<T> {
    return test.filterIndexed { i, v ->
        when {
            i == 0 && test.size <= 1 -> predicate(v, v, v)
            i == 0 -> predicate(test.last(), v, test.elementAt(i + 1))
            i == test.size - 1 -> predicate(test.elementAt(i - 1), v, test.first())
            else -> predicate(test.elementAt(i - 1), v, test.elementAt(i + 1))
        }
    }
}

