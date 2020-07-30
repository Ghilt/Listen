package compressedlang.fncs

fun toBool(value: Any): Boolean = when (value) {
    false -> false
    "" -> false
    0 -> false
    listOf<Any>() -> false
    else -> true
}

fun isFilledWithNumbers(list: List<Any>): Boolean = list.all { it is Number }

fun isFilledWithStrings(list: List<Any>): Boolean = list.all { it is String }