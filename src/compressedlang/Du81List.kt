package compressedlang

class Du81List<T>(val list: List<T>, val type: List<TYPE>) {

}

fun String.toListDu81List(): Du81List<*> {
    return Du81List(this.toList(), listOf(TYPE.STRING))
}


fun List<Int>.toListDu81List(): Du81List<*> {
    return Du81List(this, listOf(TYPE.INT))
}