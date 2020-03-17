class DeferAccumulator<T, R>(
    private val deferPredicate: (T) -> Boolean,
    private val transform: (T) -> R,
    private val transformDeferred: (List<T>) -> R
) {
    private val deferredAccumulator: MutableList<T> = mutableListOf()
    private val finalAccumulator: MutableList<R> = mutableListOf()

    fun accumulate(item: T): DeferAccumulator<T, R> {
        when {
            deferPredicate(item) -> deferredAccumulator.add(item)
            else -> {
                if (deferredAccumulator.isNotEmpty()) {
                    finalAccumulator.add(transformDeferred(deferredAccumulator))
                    deferredAccumulator.clear()
                }
                finalAccumulator.add(transform(item))
            }
        }
        return this
    }

    fun finalize(): List<R> {
        if (deferredAccumulator.isNotEmpty()) {
            finalAccumulator.add(transformDeferred(deferredAccumulator))
        }
        deferredAccumulator.clear()
        return finalAccumulator.toList()
    }

}