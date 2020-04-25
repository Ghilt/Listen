package compressedlang


class Du81List<T : Any>(val list: List<T>, val type: TYPE) {
    operator fun get(i: Int) = list[i]
}

fun String.toListDu81List(): Du81List<Char> {
    return Du81List(this.toList(), TYPE.STRING)
}

fun List<Int>.toListDu81List(): Du81List<Int> {
    return Du81List(this, TYPE.NUMBER)
}

fun <T: Any> List<T>.toListDu81List(type: TYPE): Du81List<T> {
    return Du81List(this, type)
}
