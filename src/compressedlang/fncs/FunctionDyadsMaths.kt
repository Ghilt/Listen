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

val moduloDyad = MathsDyad(
    precedence = Precedence.HIGHEST,
) { a, b -> a % b }

val moduloMathematicalDyad = MathsDyad(
    precedence = Precedence.HIGHEST,
) { a, b ->
    val programmingModulo = a % b
    val needsToAddTheMod = (b < 0 && programmingModulo > 0) || (b > 0 && programmingModulo < 0)
    if (needsToAddTheMod) b + programmingModulo else programmingModulo
}