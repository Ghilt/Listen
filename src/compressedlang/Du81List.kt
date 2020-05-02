package compressedlang

import compressedlang.fncs.ResolvedFunction

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

fun List<List<ResolvedFunction>>.toListDu81ListFromResolvedFunctions(): List<List<Du81value<Any>>> {
    return this.map { innerList -> innerList.map { it.value }}
}

fun Du81List.toDu81Value(): Du81value<out Any> {
    return Du81value(TYPE.LIST_TYPE, this.list)
}