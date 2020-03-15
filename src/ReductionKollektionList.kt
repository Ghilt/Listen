// TODO clean up generics to use list as return value

fun <T> Collection<T>.reduceConsecutive() : Collection<T> { // Add possibility to exclude characters? or specify max consecs?
    return fold(mutableListOf()) { acc, v ->
        if (acc.isEmpty() || acc.last() != v) acc.add(v)
        acc
    }
}

fun <T> Collection<T>.reduceBasedOnNeighbors(
    finalSize: Int = 1,
    maxIterations: Int = Int.MAX_VALUE,
    predicate: (T?, T, T?) -> Boolean
): Collection<T> {
    val func = bindArgs(Collection<T>::filterBasedOnNeighbors, predicate)
    return reduceByFunction(finalSize, maxIterations, func)
}

fun <T> Collection<T>.reduceBasedOnNeighbors(
    finalSize: Int = 1,
    maxIterations: Int = Int.MAX_VALUE,
    leftEdgeValue: T,
    rightEdgeValue: T = leftEdgeValue,
    predicate: (T, T, T) -> Boolean
): Collection<T> {
    val func = bindArgs(Collection<T>::filterBasedOnNeighbors, leftEdgeValue, rightEdgeValue, predicate)
    return reduceByFunction(finalSize, maxIterations, func)
}

fun <T> Collection<T>.reduceBasedOnNeighborsCyclic(
    finalSize: Int = 1,
    maxIterations: Int = Int.MAX_VALUE,
    predicate: (T, T, T) -> Boolean
): Collection<T> {
    val func = bindArgs(Collection<T>::filterBasedOnNeighborsCyclic, predicate)
    return reduceByFunction(finalSize, maxIterations, func)
}

private fun <T> Collection<T>.reduceByFunction(
    finalSize: Int,
    maxIterations: Int,
    function: Collection<T>.() -> Collection<T>
): Collection<T> {
    var result = this
    for (i in 0 until maxIterations) {
        result = result.function()
        if (result.size <= finalSize) {
            break
        }
    }
    return result
}
