package compressedlang

typealias DidChange = Boolean

class TypeRequirements(
    val precedence: Precedence = Precedence.HIGHEST,
    val provides: TYPE? = null
) {
    var requiresWeaklyByOthers: TYPE? = null
    var isWeaklyRequired: TYPE? = null
    val isRequiredBy: MutableList<Pair<TYPE, Int>> = mutableListOf()
    val requiresByOthers: MutableList<Pair<TYPE, Int>> = mutableListOf()

    fun isRequiredBy(type: TYPE, requirerIndex: Int) {
        isRequiredBy.add(type to requirerIndex)
    }

    fun requiresByOther(type: TYPE, requiredOfIndex: Int) {
        requiresByOthers.add(type to requiredOfIndex)
    }

    fun isFulfilled(atIndex: Int, context: List<TypeRequirements>): Boolean {
        return requiresByOthers.fold(true) { acc, data ->
            val requiresOfIndex = atIndex + data.second
            val satisfied =
                requiresOfIndex < context.size && data.first.isSatisfiedBy(context[requiresOfIndex].provides)
            acc && satisfied && context[requiresOfIndex].isFulfilled(requiresOfIndex, context)
        }
    }

    fun isSimplifiable(): Boolean {
        return when {
            provides == null -> false
            requiresByOthers.size == 0 && requiresWeaklyByOthers == null -> false
            else -> true
        }
    }

    fun requiresOf(targetIndex: Int, index: Int): TYPE? {
        return requiresByOthers.firstOrNull { it.second + index == targetIndex }?.first
    }

    companion object {
        fun createFromElements(elements: List<Function>): List<TypeRequirements> {
            val elementTypeRequirements =
                MutableList(elements.size) { TypeRequirements(elements[it].precedence, elements[it].output) }

            elements.withIndex().forEach { (i, element) ->
                when (element) {
                    is Monad<*, *>,
                    is Dyad<*, *, *> -> {
                        elementTypeRequirements.placeRequirements(i, element.inputs)
                    }
                }
            }
            return elementTypeRequirements
        }
    }
}

fun MutableList<TypeRequirements>.placeRequirements(
    startingPoint: Int,
    inputs: List<TYPE>
) {
    // Handle weak implicit requirement
    val previousIndex = startingPoint - 1
    val weakRequirement = inputs[0]
    this[previousIndex].isWeaklyRequired = weakRequirement
    this[startingPoint].requiresWeaklyByOthers = weakRequirement

    // Skip first implicit input
    for (relativeIndex in 1 until inputs.size) {
        val absoluteIndex = startingPoint + relativeIndex
        val type = inputs[relativeIndex]
        if (absoluteIndex >= this.size) {
            //Requires stuff from outside of list
            this.add(TypeRequirements().apply { isRequiredBy(type, startingPoint) })
        } else {
            this[absoluteIndex].isRequiredBy(type, relativeIndex)
        }
        this[startingPoint].requiresByOther(type, relativeIndex)
    }
}

fun List<TypeRequirements>.simplifyFully(): List<TypeRequirements> {
    return if (areAllFulfilled()) {
        this
    } else {
        val (didChange, simplifiedReqs) = doSimplificationPass()
        if (didChange) {
            simplifiedReqs.simplifyFully()
        } else {
            this
        }
    }
}

// Finds a function with its input types present and accounted for and removes them
fun List<TypeRequirements>.doSimplificationPass(): Pair<DidChange, List<TypeRequirements>> {
    val simplificationPossible: IndexedValue<TypeRequirements>? = this.getSimplificationTarget()
    return if (simplificationPossible != null) {
        val simplified = this.simplify(simplificationPossible)
        if (simplified.size == this.size) {
            throw DeveloperError("Simplification error ${this.diagnosticsString()}")
        }
        true to simplified
    } else {
        false to this
    }
}

private fun List<TypeRequirements>.diagnosticsString(): String {
    return "TODO" // TODO
}

fun List<TypeRequirements>.getSimplificationTarget(): IndexedValue<TypeRequirements>? {
    for (precedence in Precedence.values().reversed()) {
        val target = this
            .withIndex()
            .filter { it.value.precedence == precedence }
            .firstOrNull { (i, req) -> req.isSimplifiable() && req.isFulfilled(i, this) }
        if (target != null) return target
    }
    return null
}

fun List<TypeRequirements>.areAllFulfilled(): Boolean {
    // impossible to read but correct, should be prettier somehow
    return this.map { reqs -> reqs.isRequiredBy.none { !it.first.isSatisfiedBy(reqs.provides) } }.all { it }
//    this.map { reqs -> reqs.required.distinct().size <= 1 &&  }.all { it }
}

fun List<TypeRequirements>.simplify(target: IndexedValue<TypeRequirements>): List<TypeRequirements> {
    val indexOfPrevious = target.index - 1
    val implicitInputCanBeConsumedInstead =
        target.value.requiresWeaklyByOthers?.isSatisfiedBy(this[indexOfPrevious].provides) ?: false

    if (target.value.requiresByOthers.isEmpty() && !implicitInputCanBeConsumedInstead) {
        return this
    }

    val targetsForRemovalForwards = target.value.requiresByOthers.map { it.second }
    return this.withIndex()
        .filterNot { (i, _) ->
            val consumeImplicit = i == indexOfPrevious && implicitInputCanBeConsumedInstead
            consumeImplicit || targetsForRemovalForwards.contains(i - target.index)
        }
        .map { (i, req) ->
            if (i == target.index) {
                TypeRequirements(provides = target.value.provides)
                // functions providing functions not allowed, for now
            } else {
                req
            }
        }.recalculateRequiresInformation()
}

private fun List<TypeRequirements>.recalculateRequiresInformation(): List<TypeRequirements> {
    for ((targetIndex, toBeUpdated) in this.withIndex()) {
        toBeUpdated.isRequiredBy.clear()
        for ((index, updateSource) in this.withIndex()) {
            val r = updateSource.requiresOf(targetIndex, index)
            if (r != null) {
                toBeUpdated.isRequiredBy.add(r to targetIndex - index)
            }
        }
    }

    return this
}
