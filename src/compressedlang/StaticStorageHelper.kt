package compressedlang

class StaticStorageHelper {
    companion object {
        private val stack = mutableListOf<Any>()

        fun storeStack(v: Any) {
            stack.add(v)
        }

        fun peekStack() = if (stack.isEmpty()) 0 else stack.last()

        @OptIn(ExperimentalStdlibApi::class)
        fun popStack() = if (stack.isEmpty()) 0 else stack.removeLast()
    }
}