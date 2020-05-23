package compressedlang

import compressedlang.fncs.*
import compressedlang.fncs.Function

typealias DidChange = Boolean

@Deprecated("The way to auto close functions was introducing massive complexity and simply was not worth it")
class TypeRequirements(
    val precedence: Precedence = Precedence.HIGHEST,
    val provides: TYPE? = null
) {
    var isWeaklyRequired: TYPE? = null
    var requiresWeaklyByPrevious: TYPE? = null
    val isRequiredBy: MutableList<Pair<TYPE, Int>> = mutableListOf()
    val requiresByOthers: MutableList<Pair<TYPE, Int>> = mutableListOf()

    fun isRequiredBy(type: TYPE, requirerIndex: Int) {
        isRequiredBy.add(type to requirerIndex)
    }

    fun requiresByOther(type: TYPE, requiredOfIndex: Int) {
        requiresByOthers.add(type to requiredOfIndex)
    }

    fun requiresOf(targetIndex: Int, index: Int): TYPE? {
        return requiresByOthers.firstOrNull { it.second + index == targetIndex }?.first
    }

    companion object {
        fun createFromElements(elements: List<Function>): List<TypeRequirements> {

            // TODO not thought through how context less part of a function context will behave

            val elementTypeRequirements = MutableList(elements.size) { TypeRequirements(elements[it].precedence, elements[it].output) }

            elements.withIndex().forEach { (i, element) ->
                when (element) {
                    is Monad<*, *>,
                    is Dyad<*, *, *> -> {
                        elementTypeRequirements.placeRequirements(i, element.inputs)
                    }
                    is ContextDyad<*, *>, -> elementTypeRequirements.placeRequirements(i, element.inputs)
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
    this[startingPoint].requiresWeaklyByPrevious = weakRequirement

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
    val (didChange, simplifiedReqs) = doSimplificationPass()
    return if (didChange) {
        simplifiedReqs.simplifyFully()
    } else {
        this
    }
}

// Finds a function with its input types present and accounted for and removes them
fun List<TypeRequirements>.doSimplificationPass(): Pair<DidChange, List<TypeRequirements>> {
    val simplificationPossibleAtIndex = this.getSimplificationTarget()
    return if (simplificationPossibleAtIndex != null) {
        val simplified = this.simplify(simplificationPossibleAtIndex)
        if (simplified.size == this.size) {
            val debuggingEntry1 = this.getSimplificationTarget()
            val debuggingEntry2 = this.simplify(debuggingEntry1!!)
            throw DeveloperError("Simplification error ${this.diagnosticsString()}")
        }
        true to simplified
    } else {
        false to this
    }
}

private fun List<TypeRequirements>.diagnosticsString(): String {
    return this.joinToString(" ") { it.precedence.name }
}

fun List<TypeRequirements>.getSimplificationTarget(): Int? {
    for (precedence in Precedence.values().reversed()) {
        val target = this
            .withIndex()
            .filter { it.value.precedence == precedence }
            .firstOrNull { (i, _) -> this.isSimplifiableAt(i) && this.isFulfilledAt(i) }
        if (target != null) return target.index
    }
    return null
}

fun List<TypeRequirements>.isFulfilledAt(index: Int): Boolean {
    return this[index].requiresByOthers.fold(true) { acc, data ->
        val requiresOfIndex = index + data.second
        val satisfied = requiresOfIndex < this.size && this[requiresOfIndex].provides.isSubtypeOf_nullCompensated(data.first)
        acc && satisfied && this.isFulfilledAt(requiresOfIndex)
    }
}

fun List<TypeRequirements>.areAllFulfilled(): Boolean {
    // impossible to read but was correct before since before refactor, Todo make this comprehensible
    return this.map { reqs -> reqs.isRequiredBy.none { !it.first.isSubtypeOf_nullCompensated(reqs.provides) } }.all { it }
//    this.map { reqs -> reqs.required.distinct().size <= 1 &&  }.all { it }
}

fun List<TypeRequirements>.isSimplifiableAt(index: Int): Boolean {
    val target = this[index]
    return when {
        target.provides == null -> false
        target.requiresWeaklyByPrevious == null -> false
        this.canConsumeItsWeakRequirement(index) -> true
        target.requiresByOthers.size == 0 -> false
        else -> true
    }
}

fun List<TypeRequirements>.simplify(targetIndex: Int): List<TypeRequirements> {
    val target = this[targetIndex]
    val indexOfPrevious = targetIndex - 1
    val implicitInputCanBeConsumedInstead = this.canConsumeItsWeakRequirement(targetIndex)

    if (target.requiresByOthers.isEmpty() && !implicitInputCanBeConsumedInstead) {
        return this
    }

    val targetsForRemovalForwards = target.requiresByOthers.map { it.second }
    return this.withIndex()
        .filterNot { (i, _) ->
            val consumeImplicit = i == indexOfPrevious && implicitInputCanBeConsumedInstead
            consumeImplicit || targetsForRemovalForwards.contains(i - targetIndex)
        }
        .map { (i, req) ->
            if (i == targetIndex) {
                TypeRequirements(provides = target.provides)
                // functions providing functions not allowed, for now
            } else {
                req
            }
        }.recalculateIsRequiredByInformation()
}

private fun List<TypeRequirements>.canConsumeItsWeakRequirement(index: Int): Boolean {
    return this[index - 1].provides?.isSubtypeOf_nullCompensated(this[index].requiresWeaklyByPrevious) ?: false
}

internal fun List<TypeRequirements>.recalculateIsRequiredByInformation(): List<TypeRequirements> {
    for ((targetIndex, toBeUpdated) in this.withIndex()) {
        val next = targetIndex + 1
        toBeUpdated.isRequiredBy.clear()
        toBeUpdated.isWeaklyRequired = if (next < size) this[next].requiresWeaklyByPrevious else null
        for ((index, updateSource) in this.withIndex()) {
            val r = updateSource.requiresOf(targetIndex, index)
            if (r != null) {
                toBeUpdated.isRequiredBy.add(r to index - targetIndex)
            }
        }
    }

    return this
}

private fun TYPE?.isSubtypeOf_nullCompensated(t: TYPE?) = this?.isSubtypeOf(t) ?: false

