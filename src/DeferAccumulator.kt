class DeferAccumulator<T, R>(
    private val deferPredicate: (T) -> Boolean,
    private val deferPredicateIsByToggle: Boolean = false,
    private val transform: (T) -> R,
    private val transformDeferred: (List<T>) -> R
) {
    private val deferredAccumulator: MutableList<T> = mutableListOf()
    private val finalAccumulator: MutableList<R> = mutableListOf()
    private var toggle: Boolean = false

    fun accumulate(item: T): DeferAccumulator<T, R> {
        if (deferPredicateIsByToggle) {
            deferByToggling(item)
        } else {
            deferPerItem(item)
        }
        return this
    }

    private fun deferByToggling(item: T) {
        val predicate = deferPredicate(item)

        when {
            predicate && toggle -> {
                deferredAccumulator.add(item)
                consumeDeferred()
                toggle = !toggle
            }
            predicate && !toggle -> {
                deferredAccumulator.add(item)
                toggle = !toggle
            }
            !predicate && toggle -> deferredAccumulator.add(item)
            !predicate && !toggle -> finalAccumulator.add(transform(item))
        }
    }

    private fun deferPerItem(item: T) {
        when {
            deferPredicate(item) -> deferredAccumulator.add(item)
            else -> {
                if (deferredAccumulator.isNotEmpty()) {
                    consumeDeferred()
                }
                finalAccumulator.add(transform(item))
            }
        }
    }

    private fun consumeDeferred() {
        finalAccumulator.add(transformDeferred(deferredAccumulator))
        deferredAccumulator.clear()
    }

    fun finalize(): List<R> {
        if (deferredAccumulator.isNotEmpty()) {
            consumeDeferred()
        }
        return finalAccumulator.toList()
    }
}