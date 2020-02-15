import ch.tutteli.atrium.api.fluent.en_GB.toBe
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect


suspend fun foo() {
    throw IllegalStateException("asdf")
}

fun bar() {
    throw IllegalStateException("asdf")
}

fun main() {
    expect("foo").toBe("bar") // resolved to the “normal” version
    // normal lambda
    expect { bar() }.toThrow<IllegalArgumentException>() // resolved to the suspend version, but that’s okay

    expect { foo() }.toThrow<IllegalArgumentException>() // resolved to the suspend version
}
