package compressedlang.fncs

import compressedlang.Precedence
import compressedlang.TYPE

// this could be thought of as a nilad
val lengthMonad = Monad(
    default = currentListNilad,
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHEST
) { list: List<*> -> list.size }
