package compressedlang

import java.nio.file.Files
import java.nio.file.Paths

class InterpreterFlagManager(args: Array<String>) {

    private val FLAG_FILE_INPUT = "-f"
    private val FLAG_PROGRAM_FROM_COMMAND_LINE = "-c"
    private val FLAG_INPUT_LIST_SEPARATOR = "-s" // Default is ','

    var program: String? = null
    val inputs = mutableListOf<List<Any>>()
    private var separator = ","

    init {

        var consumer: (String) -> Boolean = ::doNotConsume
        for (index in args.indices) {

            val success = consumer(args[index])
            consumer = ::doNotConsume
            if (success) continue

            consumer = when (args[index]) {
                FLAG_FILE_INPUT -> ::consumeFileInput
                FLAG_PROGRAM_FROM_COMMAND_LINE -> ::consumeProgramDirectly
                FLAG_INPUT_LIST_SEPARATOR -> ::consumeListSeparator
                else -> consumeDirectly(args[index])
            }
        }
    }

    private fun consumeListSeparator(arg: String): Boolean {
        separator = arg
        return true
    }

    private fun consumeProgramDirectly(arg: String): Boolean {
        if (program != null) {
            throw IllegalArgumentException("Program already defined: $program, error: $arg")
        }
        program = arg
        return true
    }

    private fun consumeFileInput(arg: String): Boolean {
        addInput(arg)
        return true
    }

    private fun readFileIntoString(arg: String): String {
        val stream = Files.newInputStream(Paths.get(arg))
        var fileString = ""
        stream.buffered().reader().use { reader ->
            fileString = reader.readText()
        }
        return fileString
    }

    @Suppress("UNUSED_PARAMETER")
    private fun doNotConsume(arg: String): Boolean {
        return false
    }

    private fun consumeDirectly(arg: String): (String) -> Boolean {
        if (program == null) {
            program = readFileIntoString(arg)
        } else {
            addInput(arg)
        }
        return this::doNotConsume
    }

    private fun addInput(stringValue: String) {
        val elements = stringValue.split(separator)
        val elementsAsInt = elements.mapNotNull { it.toIntOrNull() }

        if (elementsAsInt.size == elements.size) {
            inputs.add(elementsAsInt)
            return
        }

        val elementsAsDouble = elements.mapNotNull { it.toDoubleOrNull() }

        if (elementsAsDouble.size == elements.size) {
            inputs.add(elementsAsDouble)
            return
        }
        inputs.add(elements)
    }
}