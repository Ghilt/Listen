package compressedlang.fncs

import compressedlang.ContextKey
import compressedlang.Du81List
import compressedlang.Precedence
import compressedlang.TYPE

// this could be thought of as a nilad
val lengthMonad = Monad(
    defaultImplicitInput = currentListNilad,
    inputs = listOf(TYPE.LIST_TYPE),
    output = TYPE.NUMBER,
    precedence = Precedence.HIGHEST
) { list: List<*> -> list.size }

val listByIndexMonad = Monad(
    defaultImplicitInput = valueThenIndexNilad,
    inputs = listOf(TYPE.NUMBER),
    output = TYPE.LIST_TYPE,
    contextKey = ContextKey.LIST_BY_INDEX,
    precedence = Precedence.HIGH // TODO need to fix precedences -> Int
) { listIndex: Du81List -> listIndex.list  }
