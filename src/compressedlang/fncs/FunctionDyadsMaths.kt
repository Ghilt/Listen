package compressedlang.fncs

import compressedlang.*

class MathsDyad(
    precedence: Precedence,
    defaultImplicitInput: Nilad = valueThenIndexNilad,
    mathematicalDyad: (Double, Double) -> Double
): Dyad<Double, Double, Double>(
    defaultImplicitInput = defaultImplicitInput,
    precedence = precedence,
    inputs = listOf(TYPE.NUMBER, TYPE.NUMBER),
    output = TYPE.NUMBER,
    f = mathematicalDyad
)

val additionDyad = MathsDyad(
    precedence = Precedence.MEDIUM,
) { a, b -> a + b }

val subtractionDyad = MathsDyad(
    defaultImplicitInput = constantZeroNilad,
    precedence = Precedence.MEDIUM,
) { a, b -> a - b }

val multiplicationDyad = MathsDyad(
    precedence = Precedence.HIGHEST,
) { a, b -> a * b }

val divisionDyad = MathsDyad(
    precedence = Precedence.HIGHEST,
) { a, b -> a / b }

val wholeDivisionDyad = MathsDyad(
    precedence = Precedence.HIGHEST,
) { a, b -> (a / b).toInt().toDouble() }
