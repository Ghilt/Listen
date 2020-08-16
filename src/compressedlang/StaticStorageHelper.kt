package compressedlang

class StaticStorageHelper {
    companion object {
        private val stack = mutableListOf<Any>()
        private val map = mutableMapOf<Any, Any>()

        fun storeStack(v: Any): Any {
            stack.add(v)
            return v
        }

        fun peekStack() = if (stack.isEmpty()) 0 else stack.last()

        @OptIn(ExperimentalStdlibApi::class)
        fun popStack() = if (stack.isEmpty()) 0 else stack.removeLast()

        fun storeMap(key: Any, value: Any): Any = map.put(key, value) ?: 0

        fun loadMap(key: Any): Any = map[key] ?: 0

    }
}