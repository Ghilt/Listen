package compressedlang.fncs

import compressedlang.ContextKey
import compressedlang.TYPE

val valueThenIndexNilad = Nilad(
    ContextKey.VALUE_THEN_INDEX,
    TYPE.NUMBER
)
val indexNilad = Nilad(
    ContextKey.INDEX,
    TYPE.NUMBER
)
val valueNilad = Nilad(ContextKey.VALUE, TYPE.ANY)
val constantZeroNilad = Nilad(
    ContextKey.CONSTANT_0,
    TYPE.NUMBER
)
val currentListNilad = Nilad(
    ContextKey.CURRENT_LIST,
    TYPE.LIST_TYPE
)

