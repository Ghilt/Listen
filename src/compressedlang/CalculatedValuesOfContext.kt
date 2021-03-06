@file:Suppress("MemberVisibilityCanBePrivate")

package compressedlang

import compressedlang.fncs.ConfigValues
import compressedlang.fncs.ResolvedFunction

class CalculatedValuesOfContext(
    val listToOperateOn: List<Any>,
    val configValuesForFunction: List<ResolvedFunction>,
    val calculatedValuesOfContext: List<List<ResolvedFunction>>
){

    internal var defaultConfigValues: List<Any> = listOf()

    fun injectDefaultConfigValues(defaultConfigValues: List<Any>) {
        this.defaultConfigValues = defaultConfigValues
    }

    fun listOfFirstCalculatedValues(): List<Any> = calculatedValuesOfContext.map { it.first().value }

    fun listOfSecondCalculatedValues(): List<Any> = calculatedValuesOfContext.map { it[1].value }

    fun getConfigValues() = ConfigValues(configValuesForFunction, defaultConfigValues)
}