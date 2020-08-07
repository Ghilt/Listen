package compressedlang

import java.io.File

fun readOeisSequence(number: Int): List<Int> =
    readOeisSequence("raw\\oeis_data.txt", number)


private fun readOeisSequence(filePath: String, number: Int): List<Int> {
    var sequenceData: String? = null
    val id = "A" + "$number".padStart(6, '0')

    File(filePath).forEachLine {
        if (it.startsWith(id)) sequenceData = it
    }

    val foundSequence = sequenceData?.drop(9)?.dropLast(1)
        ?: throw IllegalArgumentException("No oeis sequence found for $id, for more info visit https://oeis.org, It is a great resource.")

    return foundSequence.split(',').map { it.toInt() }
}
