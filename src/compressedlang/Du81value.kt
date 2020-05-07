package compressedlang

data class Du81value<T : Any>(val type: TYPE, val value: T) {
    init {
//        if (value is List<*>) throw DeveloperError("Trying to put a Du81List in a Du81Value. A decision was taken to store it as List<Du81Value>")
    }
}

