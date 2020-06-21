@file:Suppress("UNCHECKED_CAST", "unused")

package compressedlang.fncs

import compressedlang.SyntaxError

class ConfigValues(val values: List<ResolvedFunction>) {

    operator fun <T : Any> get(i: Int) = try {
        values[i].value as T
    } catch (e: Exception) {
        throw SyntaxError("Configuration value for function expected to be of specific type, but was not: ${e.message}")
    }
}