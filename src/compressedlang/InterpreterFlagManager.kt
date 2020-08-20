package compressedlang

import compressedlang.OutputMode.*
import java.nio.file.Files
import java.nio.file.Paths

class InterpreterFlagManager(args: Array<String>) {

    private val FLAG_OUTPUT_MODE_COMMA_SEPARATED_LIST = "-l"
    private val FLAG_OUTPUT_MODE_FIRST_ELEMENT = "-a"
    private val FLAG_OUTPUT_MODE_JUST_LAST_ELEMENT = "-o"
    private val FLAG_FILE_INPUT = "-f"
    private val FLAG_PROGRAM_FROM_COMMAND_LINE = "-c"
    private val FLAG_INPUT_LIST_SEPARATOR = "-s" // Default is ','

    var program: String? = null
    val inputs = mutableListOf<List<Any>>()
    private var separator = ","
    private var _outputMode = STRING

    val outputMode
        get() = _outputMode

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
                FLAG_OUTPUT_MODE_COMMA_SEPARATED_LIST -> consumeOutputMode(COMMA_SEPARATED_LIST)
                FLAG_OUTPUT_MODE_FIRST_ELEMENT -> consumeOutputMode(FIRST_ELEMENT)
                FLAG_OUTPUT_MODE_JUST_LAST_ELEMENT -> consumeOutputMode(LAST_ELEMENT)
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
        var fileString: String
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

    private fun consumeOutputMode(flagArg: OutputMode): (String) -> Boolean {
        _outputMode = flagArg
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

enum class OutputMode { STRING, COMMA_SEPARATED_LIST, FIRST_ELEMENT, LAST_ELEMENT }