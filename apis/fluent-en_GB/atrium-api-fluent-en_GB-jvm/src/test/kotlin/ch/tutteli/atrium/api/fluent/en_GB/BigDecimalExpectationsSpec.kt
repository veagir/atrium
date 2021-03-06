package ch.tutteli.atrium.api.fluent.en_GB

import ch.tutteli.atrium.api.verbs.internal.expect
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.specs.fun1
import ch.tutteli.atrium.specs.withNullableSuffix
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.math.BigDecimal

class BigDecimalExpectationsSpec : Spek({
    include(object : ch.tutteli.atrium.specs.integration.BigDecimalExpectationsSpec(
        @Suppress("DEPRECATION") fun1<BigDecimal, BigDecimal>(Expect<BigDecimal>::toBe),
        @Suppress("DEPRECATION") fun1<BigDecimal?, BigDecimal?>(Expect<BigDecimal?>::toBe).withNullableSuffix(),
        fun1<BigDecimal?, Nothing?>(Expect<BigDecimal?>::toBe).withNullableSuffix(),
        Expect<Any>::toEqual,
        @Suppress("DEPRECATION") Expect<BigDecimal>::notToBe.name to @Suppress("DEPRECATION") Expect<BigDecimal>::notToBe,
        Expect<Any>::notToEqual,
        Expect<BigDecimal>::isNumericallyEqualTo.name to Expect<BigDecimal>::isNumericallyEqualTo,
        Expect<BigDecimal>::isNotNumericallyEqualTo.name to Expect<BigDecimal>::isNotNumericallyEqualTo,
        Expect<BigDecimal>::isEqualIncludingScale.name to Expect<BigDecimal>::isEqualIncludingScale,
        Expect<BigDecimal>::isNotEqualIncludingScale.name to Expect<BigDecimal>::isNotEqualIncludingScale
    ) {})

    describe("fun toBe for BigDecimal? and subject is null") {
        it("chooses the correct overload if `null` is passed, does not throw") {
            expect(null as BigDecimal?).toBe(null)
        }
    }
})
