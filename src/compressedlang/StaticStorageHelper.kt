package compressedlang

class StaticStorageHelper {
    companion object {
        private val stack = mutableListOf<Any>()
        private val map = mutableMapOf<Any, Any>()

        fun storeStack(v: Any): Any {
            log("Du81, storing on static stack: $v")
            stack.add(v)
            return v
        }

        fun peekStack() = if (stack.isEmpty()) 0 else stack.last()

        @OptIn(ExperimentalStdlibApi::class)
        fun popStack() = if (stack.isEmpty()) 0 else stack.removeLast()

        fun storeMap(key: Any, value: Any): Any {
            log("Du81, storing on static map: $key, $value")
            return map.put(key, value) ?: 0
        }

        fun loadMap(key: Any): Any = map[key] ?: 0
        fun clearAll() {
            stack.clear()
            map.clear()
        }

    }
}