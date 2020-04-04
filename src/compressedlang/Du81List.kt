package compressedlang

class Du81List<T>(val list: List<T>, val type: TYPE) {

}

fun String.toListDu81List(): Du81List<Char> {
    return Du81List(this.toList(), TYPE.STRING)
}


fun List<Int>.toListDu81List(): Du81List<Int> {
    return Du81List(this, TYPE.INT)
}