package compressedlang.fncs

import compressedlang.ContextKey
import compressedlang.TYPE

val valueThenIndexNilad = Nilad(ContextKey.VALUE_THEN_INDEX, TYPE.NUMBER)

val indexNilad = Nilad(ContextKey.INDEX, TYPE.NUMBER)

val valueNilad = Nilad(ContextKey.VALUE, TYPE.ANY)

val no_opNilad = Nilad(ContextKey.NOP, TYPE.ANY)

val valueThenCurrentListNilad = Nilad(ContextKey.VALUE_THEN_CURRENT_LIST, TYPE.LIST_TYPE)

val constantZeroNilad = Nilad(ContextKey.CONSTANT_0, TYPE.NUMBER)

val currentListNilad = Nilad(ContextKey.LIST_BY_INDEX, TYPE.LIST_TYPE, listOf(0))

val secondCurrentListNilad = Nilad(ContextKey.LIST_BY_INDEX, TYPE.LIST_TYPE, listOf(1))

val currentListCount = Nilad(ContextKey.CURRENT_LIST_COUNT, TYPE.NUMBER)


