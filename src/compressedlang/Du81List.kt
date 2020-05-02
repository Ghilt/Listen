package compressedlang

import compressedlang.fncs.ResolvedFunction
import java.lang.IllegalArgumentException

data class Du81List(val innerType: TYPE, val list: List<Du81value<*>>) {
    operator fun get(i: Int) = list[i]
    fun unwrap(): List<Any> = list.map { it.value }
}


fun List<Int>.toDu81List(): Du81List {
    return Du81List(TYPE.NUMBER, this.map { Du81value(TYPE.NUMBER, it) })
}

fun <T: Any> List<T>.toDu81List(type: TYPE): Du81List {
    return Du81List(type, this.map { if (it is Du81value<*>) it else Du81value(type, it) })
}

fun String.toDu81List(): Du81List {
    return Du81List(TYPE.STRING, this.toList().map { Du81value(TYPE.STRING, it) })
}
fun <T: Any> List<T>.toDu81ListAutoType(): Du81List {
    // Convenience method, probably remove later

    if (this.isEmpty()) {
        return Du81List(TYPE.ANY, listOf())
    }

    return when (val sample = this[0]) {
        is Boolean ->  this.toDu81List(TYPE.BOOL)
        is Number -> this.toDu81List(TYPE.NUMBER)
        is String ->  this.toDu81List(TYPE.STRING)
        is Char ->  this.map { "$it" }.toDu81List(TYPE.STRING) // Maybe support Chars?
        is List<*> ->  TODO() // Needs some more thought, this.toDu81List(TYPE.LIST_TYPE)
        else -> throw IllegalArgumentException("List with $sample is not supported. ${this.joinToString()}")
    }
}

fun List<List<ResolvedFunction>>.toListDu81ListFromResolvedFunctions(): List<List<Du81value<Any>>> {
    return this.map { innerList -> innerList.map { it.value }}
}

fun Du81List.toDu81Value(): Du81value<out Any> {
    return Du81value(TYPE.LIST_TYPE, this.list)
}