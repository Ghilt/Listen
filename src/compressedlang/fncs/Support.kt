package compressedlang.fncs

fun toBool(value: Any): Boolean = when (value) {
    false -> false
    "" -> false
    0 -> false
    listOf<Any>() -> false
    else -> true
}