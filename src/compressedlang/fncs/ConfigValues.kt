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
                // To make it easier to setup default values in the defined functions this hack is in place:
                // If the raw value cannot be cast then try to Integers to bool as
                // 0 == false, otherwise true
                try {
                    ((values[i].value as Int) != 0) as T
                } catch (_: Exception) {
                    throw SyntaxError("Configuration value for function expected to be of specific type, but was not: ${e.message}")
                }
            }
        } else {
            try {
                defaultValues[i] as T
            } catch (e: Exception) {
                throw DeveloperError("Default values for context function was not setup properly: ${e.message}")
            }
        }

    fun getBool(i: Int): Boolean =
        if (i in values.indices) {
            try {
                (values[i].value as Int) != 0
            } catch (e: Exception) {
                throw SyntaxError("Configuration value for function expected to be of Boolean, but was not: ${e.message}")
            }
        } else {
            try {
                defaultValues[i] as Boolean
            } catch (e: Exception) {
                throw DeveloperError("Default values for context function was not setup properly: ${e.message}")
            }
        }
}