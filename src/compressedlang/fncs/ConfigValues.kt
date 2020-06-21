@file:Suppress("UNCHECKED_CAST", "unused")

package compressedlang.fncs

import compressedlang.DeveloperError
import compressedlang.SyntaxError

class ConfigValues(val values: List<ResolvedFunction>, private val defaultValues: List<Any>) {

    operator fun <T : Any> get(i: Int): T =
        if (i in values.indices) {
            try {
                values[i].value as T
            } catch (e: Exception) {
                throw SyntaxError("Configuration value for function expected to be of specific type, but was not: ${e.message}")
            }
        } else {
            try {
                defaultValues[i] as T
            } catch (e: Exception) {
                throw DeveloperError("Default values for context function was not setup properly: ${e.message}")
            }
        }

}