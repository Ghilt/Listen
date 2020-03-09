operator fun Number.plus(v: Int): Number {
    // I wonder if this hacky thing is really needed
    return when (this) {
        is Byte -> this.toByte() + v
        is Int -> this.toInt() + v
        is Double -> this.toDouble() + v
        is Long -> this.toLong() + v
        is Float -> this.toFloat() + v
        is Short -> this.toShort() + v
        else -> this
    }
}

fun <Receiver, A, B, C, D> bindArgs(f: Receiver.(A, B, C) -> D, a: A, b: B): Receiver.(C) -> D {
    return { c: C -> f(a, b, c) }
}

fun <Receiver, A, O> bindArgs(f: Receiver.(A) -> O, a: A): Receiver.() -> O {
    return { f(a) }
}

fun <Receiver, A, B, O> bindArgs(f: Receiver.(A, B) -> O, a: A, b: B): Receiver.() -> O {
    return { f(a, b) }
}

fun <Receiver, A, B, C, O> bindArgs(f: Receiver.(A, B, C) -> O, a: A, b: B, c: C): Receiver.() -> O {
    return { f(a, b, c) }
}