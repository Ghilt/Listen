package compressedlang

enum class TYPE {
    LIST_TYPE, NUMBER, DOUBLE, INT, STRING, BOOL, ANY
}

fun TYPE.isSatisfiedBy(type: TYPE?): Boolean {
    return this == type
            || (this == TYPE.NUMBER && type == TYPE.DOUBLE)  // super temporary
            || (this == TYPE.DOUBLE && type == TYPE.NUMBER)
            || (this == TYPE.NUMBER && type == TYPE.INT)
            || (this == TYPE.INT && type == TYPE.NUMBER)
            || (this == TYPE.DOUBLE && type == TYPE.INT)
            || (this == TYPE.INT && type == TYPE.DOUBLE)
}