package ch.tutteli.atrium.specs.integration

import ch.tutteli.atrium.api.fluent.en_GB.*
import ch.tutteli.atrium.api.verbs.internal.expect
import ch.tutteli.atrium.creating.Expect
import ch.tutteli.atrium.logic.utils.Group
import ch.tutteli.atrium.specs.*
import ch.tutteli.atrium.translations.DescriptionCollectionAssertion
import org.spekframework.spek2.style.specification.Suite

abstract class IterableContainsInOrderOnlyGroupedEntriesExpectationsSpec(
    containsInOrderOnlyGroupedEntries: Fun3<Iterable<Double?>, Group<(Expect<Double>.() -> Unit)?>, Group<(Expect<Double>.() -> Unit)?>, Array<out Group<(Expect<Double>.() -> Unit)?>>>,
    groupFactory: (Array<out (Expect<Double>.() -> Unit)?>) -> Group<(Expect<Double>.() -> Unit)?>,
    describePrefix: String = "[Atrium] "
) : IterableContainsEntriesSpecBase({

    fun context(vararg assertionCreators: (Expect<Double>.() -> Unit)?) = groupFactory(assertionCreators)

    include(object : SubjectLessSpec<Iterable<Double?>>(
        describePrefix,
        containsInOrderOnlyGroupedEntries.forSubjectLess(
            context({ toEqual(2.5) }),
            context({ toEqual(4.1) }),
            arrayOf()
        )
    ) {})
    //@formatter:off
    include(object : AssertionCreatorSpec<Iterable<Double?>>(
        describePrefix, listOf(1.2, 2.0, 3.0),
        assertionCreatorSpecTriple(containsInOrderOnlyGroupedEntries.name + " [first empty]", "$toBeDescr: 1.2",
            { containsInOrderOnlyGroupedEntries(this, Entry { toEqual(1.2) }, Entry { toEqual(2.0) }, arrayOf( Entry { toEqual(3.0) })) },
            { containsInOrderOnlyGroupedEntries(this, Entry { }, Entry { toEqual(2.0) }, arrayOf( Entry { toEqual(3.0) })) }
        ),
        assertionCreatorSpecTriple(containsInOrderOnlyGroupedEntries.name + " [second empty]", "$toBeDescr: 2.0",
            { containsInOrderOnlyGroupedEntries(this, Entry { toEqual(1.2) }, Entry { toEqual(2.0) }, arrayOf( Entry { toEqual(3.0) })) },
            { containsInOrderOnlyGroupedEntries(this, Entry { toEqual(1.2) }, Entry { }, arrayOf( Entry { toEqual(3.0) })) }
        ),
        assertionCreatorSpecTriple(containsInOrderOnlyGroupedEntries.name + " [third empty]", "$toBeDescr: 3.0",
            { containsInOrderOnlyGroupedEntries(this, Entry { toEqual(1.2) }, Entry { toEqual(2.0) }, arrayOf( Entry { })) },
            { containsInOrderOnlyGroupedEntries(this, Entry { toEqual(1.2) }, Entry { toEqual(2.0) }, arrayOf( Entry {  })) }
        )
    ) {})
    //@formatter:on

    fun describeFun(vararg pairs: SpecPair<*>, body: Suite.() -> Unit) =
        describeFunTemplate(describePrefix, pairs.map { it.name }.toTypedArray(), body = body)

    fun Expect<Iterable<Double?>>.containsInOrderOnlyGroupedEntriesFun(
        t1: Group<(Expect<Double>.() -> Unit)?>,
        t2: Group<(Expect<Double>.() -> Unit)?>,
        vararg tX: Group<(Expect<Double>.() -> Unit)?>
    ) = containsInOrderOnlyGroupedEntries(this, t1, t2, tX)

    fun element(prefix: String, bulletPoint: String, indentRootBulletPoint: String, expected: Array<out String>) =
        expected.joinToString(".*$separator") {
            "$prefix\\Q$bulletPoint$anElementWhich: \\E$separator" +
                "$prefix$indentRootBulletPoint$indentListBulletPoint$explanatoryBulletPoint$it"
        }

    fun size(prefix: String, bulletPoint: String, actual: Int, expected: Int) =
        "$prefix\\Q$bulletPoint\\E${featureArrow}${DescriptionCollectionAssertion.SIZE.getDefault()}: $actual[^:]+: $expected"

    fun sizeCheck(actual: Int, expected: Int) = size(
        "$indentRootBulletPoint$indentFailingBulletPoint$indentFeatureArrow", featureBulletPoint, actual, expected)

    val afterFail = "$indentRootBulletPoint$indentFailingBulletPoint$indentFeatureArrow$indentFeatureBulletPoint"
    fun failAfterFail(vararg expected: String) =
        element(afterFail, failingBulletPoint, indentFailingBulletPoint, expected)

    fun successAfterFail(vararg expected: String) =
        element(afterFail, successfulBulletPoint, indentSuccessfulBulletPoint, expected)

    val additionalElementsFail = "$indentRootBulletPoint$indentFailingBulletPoint"

    fun <T> additionalElementsWarning(msg: String, values: Array<out T>, act: (T) -> String) =
        "$additionalElementsFail\\Q$warningBulletPoint$msg\\E: $separator" +
            values.joinToString(".*$separator") { "$additionalElementsFail$indentWarningBulletPoint$listBulletPoint${act(it)}" }

    fun <T> warning(msg: String, values: Array<out T>, act: (T) -> String) =
        "$afterFail\\Q$warningBulletPoint$msg\\E: $separator" +
            values.joinToString(".*$separator") { "$afterFail$indentWarningBulletPoint$listBulletPoint${act(it)}" }

    fun mismatchesAfterFail(vararg mismatched: Double) = warning(mismatches, mismatched.toTypedArray()) { "$it" }

    fun additional(vararg entryWithValue: Pair<Int, Double>) =
        additionalElementsWarning(additionalElements, entryWithValue) { "${elementWithIndex(it.first)}: ${it.second}" }


    val afterSuccess = "$indentRootBulletPoint$indentSuccessfulBulletPoint$indentFeatureArrow$indentFeatureBulletPoint"
    fun successAfterSuccess(vararg expected: String) =
        element(afterSuccess, successfulBulletPoint, indentSuccessfulBulletPoint, expected)

    fun Expect<String>.indexSuccess(index: Int, actual: Any, expected: String): Expect<String> {
        return this.toContain.exactly(1).regex(
            "\\Q$successfulBulletPoint$featureArrow${index(index)}: $actual\\E.*$separator" +
                "$indentRootBulletPoint$indentSuccessfulBulletPoint$indentFeatureArrow$featureBulletPoint$expected"
        )
    }

    fun Expect<String>.indexSuccess(
        fromIndex: Int,
        toIndex: Int,
        actual: List<Double?>,
        sizeCheck: String,
        vararg expected: String
    ): Expect<String> {
        return this.toContain.exactly(1).regex(
            "\\Q$successfulBulletPoint$featureArrow${index(fromIndex, toIndex)}: $actual\\E.*$separator" +
                "$sizeCheck.*$separator" +
                "$indentRootBulletPoint$indentFailingBulletPoint$indentFeatureArrow$featureBulletPoint$containsInAnyOrderOnly: $separator" +
                expected.joinToString(".*$separator")

        )
    }

    fun Expect<String>.indexFail(index: Int, actual: Any, expected: String): Expect<String> {
        return this.toContain.exactly(1).regex(
            "\\Q$failingBulletPoint$featureArrow${index(index)}: $actual\\E.*$separator" +
                "$indentRootBulletPoint$indentFailingBulletPoint$indentFeatureArrow$featureBulletPoint$expected"
        )
    }

    fun Expect<String>.indexNonExisting(index: Int, expected: String): Expect<String> {
        return this.toContain.exactly(1).regex(
            "\\Q$failingBulletPoint$featureArrow${index(index)}: $sizeExceeded\\E.*$separator" +
                "$afterFail$explanatoryBulletPoint$expected"
        )
    }

    sizeExceeded
    fun Expect<String>.indexFail(
        fromIndex: Int,
        toIndex: Int,
        actual: List<Double?>,
        sizeCheck: String,
        vararg expected: String
    ): Expect<String> {
        return this.toContain.exactly(1).regex(
            "\\Q$failingBulletPoint$featureArrow${index(fromIndex, toIndex)}: $actual\\E.*$separator" +
                "$sizeCheck.*$separator" +
                "$indentRootBulletPoint$indentFailingBulletPoint$indentFeatureArrow$featureBulletPoint$containsInAnyOrderOnly: $separator" +
                expected.joinToString(".*$separator")
        )
    }

    describeFun(containsInOrderOnlyGroupedEntries) {
        context("describe non-nullable cases") {

            context("throws an $illegalArgumentException") {
                it("if an empty group is given as first parameter") {
                    expect {
                        expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                            context(),
                            context({ toEqual(-1.2) })
                        )
                    }.toThrow<IllegalArgumentException> { messageContains("a group of values cannot be empty") }
                }
                it("if an empty group is given as second parameter") {
                    expect {
                        expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                            context({ toEqual(1.2) }),
                            context()
                        )
                    }.toThrow<IllegalArgumentException> { messageContains("a group of values cannot be empty") }
                }
                it("if an empty group is given as third parameter") {
                    expect {
                        expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                            context({ toEqual(1.2) }),
                            context({ toEqual(4.3) }),
                            context()
                        )
                    }.toThrow<IllegalArgumentException> { messageContains("a group of values cannot be empty") }
                }
                it("if an empty group is given as fourth parameter") {
                    expect {
                        expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                            context({ toEqual(1.2) }),
                            context({ toEqual(4.3) }),
                            context({ toEqual(5.7) }),
                            context()
                        )
                    }.toThrow<IllegalArgumentException> { messageContains("a group of values cannot be empty") }
                }
            }

            context("empty collection") {
                val fluentEmpty = expect(setOf<Double?>().asIterable())
                it("(1.0), (1.2) throws AssertionError") {
                    expect {
                        fluentEmpty.containsInOrderOnlyGroupedEntriesFun(context({ toEqual(1.0) }), context({
                            toEqual(
                                1.2
                            )
                        }))
                    }.toThrow<AssertionError> {
                        message {
                            toContain.exactly(1).value("$rootBulletPoint$containsInOrderOnlyGrouped:")
                            indexNonExisting(0, "$toBeDescr: 1.0")
                            indexNonExisting(1, "$toBeDescr: 1.2")
                            notToContain(additionalElements)
                            containsSize(0, 2)
                        }
                    }
                }
            }

            context("iterable ${oneToFour().toList()}") {

                context("happy case") {
                    it("(1.0), (2.0, 3.0), (4.0, 4.0)") {
                        expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                            context({ toEqual(1.0) }),
                            context({ toEqual(2.0) }, { toEqual(3.0) }),
                            context({ toEqual(4.0) }, { toEqual(4.0) })
                        )
                    }
                    it("(2.0, 1.0), (4.0, 3.0), (4.0)") {
                        expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                            context({ toEqual(2.0) }, { toEqual(1.0) }),
                            context({ toEqual(4.0) }, { toEqual(3.0) }),
                            context({ toEqual(4.0) })
                        )
                    }
                    it("(2.0, 3.0, 1.0), (4.0), (4.0)") {
                        expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                            context({ toEqual(2.0) }, { toEqual(3.0) }, { toEqual(1.0) }),
                            context({ toEqual(4.0) }),
                            context({ toEqual(4.0) })
                        )
                    }
                    it("(1.0, 2.0), (4.0, 3.0, 4.0)") {
                        expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                            context({ toEqual(1.0) }, { toEqual(2.0) }),
                            context({ toEqual(4.0) }, { toEqual(3.0) }, { toEqual(4.0) })
                        )
                    }
                    it("[$isLessThanFun(2.1) && $isGreaterThanFun(1.0), $isLessThanFun(2.0)], [$isGreaterThanFun(3.0), $isGreaterThanFun(2.0), $isGreaterThanFun(1.0) && $isLessThanFun(5.0)]") {
                        expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                            context({ toBeLessThan(2.1).and.toBeGreaterThan(1.0) }, { toBeLessThan(2.0) }),
                            context(
                                { toBeGreaterThan(3.0) },
                                { toBeGreaterThan(2.0) },
                                { toBeGreaterThan(1.0); toBeLessThan(5.0) })
                        )
                    }
                }

                context("error cases (throws AssertionError)") {

                    it("(4.0, 1.0), (2.0, 3.0, 4.0) -- wrong order") {
                        expect {
                            expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                                context({ toEqual(4.0) }, { toEqual(1.0) }),
                                context({ toEqual(2.0) }, { toEqual(3.0) }, { toEqual(4.0) })
                            )
                        }.toThrow<AssertionError> {
                            message {
                                toContain.exactly(1).value("$rootBulletPoint$containsInOrderOnlyGrouped:")
                                indexFail(
                                    0, 1, listOf(1.0, 2.0),
                                    sizeCheck(2, 2),
                                    failAfterFail("$toBeDescr: 4.0"),
                                    successAfterFail("$toBeDescr: 1.0"),
                                    mismatchesAfterFail(2.0)
                                )
                                indexFail(
                                    2, 4, listOf(3.0, 4.0, 4.0),
                                    sizeCheck(3, 3),
                                    failAfterFail("$toBeDescr: 2.0"),
                                    successAfterFail("$toBeDescr: 3.0"),
                                    successAfterFail("$toBeDescr: 4.0"),
                                    mismatchesAfterFail(4.0)
                                )
                                notToContain(size(indentRootBulletPoint, successfulBulletPoint, 5, 5))
                            }
                        }
                    }


                    it("[$isLessThanFun(2.1), $isLessThanFun(2.0)], (4.0, 3.0, 4.0) -- first win also applies here, $isLessThanFun(2.1) matches 1.0 and not 2.0") {
                        expect {
                            expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                                context({ toBeLessThan(2.1) }, { toBeLessThan(2.0) }),
                                context({ toEqual(4.0) }, { toEqual(3.0) }, { toEqual(4.0) })
                            )
                        }.toThrow<AssertionError> {
                            message {
                                toContain.exactly(1).value("$rootBulletPoint$containsInOrderOnlyGrouped:")
                                indexFail(
                                    0, 1, listOf(1.0, 2.0),
                                    sizeCheck(2, 2),
                                    successAfterFail("$isLessThanDescr: 2.1"),
                                    failAfterFail("$isLessThanDescr: 2.0"),
                                    mismatchesAfterFail(2.0)
                                )
                                indexSuccess(
                                    2, 4, listOf(3.0, 4.0, 4.0),
                                    sizeCheck(3, 3),
                                    successAfterSuccess("$toBeDescr: 4.0"),
                                    successAfterSuccess("$toBeDescr: 3.0"),
                                    successAfterSuccess("$toBeDescr: 4.0")
                                )
                                notToContain(size(indentRootBulletPoint, successfulBulletPoint, 5, 5))
                            }
                        }
                    }

                    it("(1.0), (4.0, 3.0, 2.0) -- 4.0 was missing") {
                        expect {
                            expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                                context({ toEqual(1.0) }),
                                context({ toEqual(4.0) }, { toEqual(2.0) }, { toEqual(3.0) })
                            )
                        }.toThrow<AssertionError> {
                            message {
                                containsSize(5, 4)
                                toContain.exactly(1).value("$rootBulletPoint$containsInOrderOnlyGrouped:")
                                indexSuccess(0, 1.0, "$toBeDescr: 1.0")
                                indexSuccess(
                                    1, 3, listOf(2.0, 3.0, 4.0),
                                    sizeCheck(3, 3),
                                    successAfterSuccess("$toBeDescr: 4.0", "$toBeDescr: 2.0", "$toBeDescr: 3.0")
                                )
                                toContainRegex(additional(4 to 4.0))
                            }
                        }
                    }

                    it("(1.0), (4.0) -- 2.0, 3.0 and 4.0 was missing") {
                        expect {
                            expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(context({
                                toEqual(
                                    1.0
                                )
                            }), context({ toEqual(4.0) }))
                        }.toThrow<AssertionError> {
                            message {
                                containsSize(5, 2)
                                toContain.exactly(1).value("$rootBulletPoint$containsInOrderOnlyGrouped:")
                                indexSuccess(0, 1.0, "$toBeDescr: 1.0")
                                indexFail(1, 2.0, "$toBeDescr: 4.0")
                                toContainRegex(additional(2 to 3.0, 3 to 4.0, 4 to 4.0))
                            }
                        }
                    }
                    it("(1.0, 3.0), (5.0) -- 5.0 is wrong and 4.0 and 4.0 are missing") {
                        expect {
                            expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                                context({ toEqual(1.0) }, { toEqual(3.0) }),
                                context({ toEqual(5.0) })
                            )
                        }.toThrow<AssertionError> {
                            message {
                                toContain.exactly(1).value("$rootBulletPoint$containsInOrderOnlyGrouped:")
                                indexFail(
                                    0, 1, listOf(1.0, 2.0),
                                    sizeCheck(2, 2),
                                    successAfterFail("$toBeDescr: 1.0"),
                                    failAfterFail("$toBeDescr: 3.0"),
                                    mismatchesAfterFail(2.0)
                                )
                                indexFail(2, 3.0, "$toBeDescr: 5.0")
                                toContainRegex(additional(3 to 4.0, 4 to 4.0))
                            }
                        }
                    }
                    it("(4.0, 1.0, 3.0, 2.0), (5.0, 4.0) -- 5.0 too much") {
                        expect {
                            expect(oneToFour() as Iterable<Double?>).containsInOrderOnlyGroupedEntriesFun(
                                context({ toEqual(4.0) }, { toEqual(1.0) }, { toEqual(3.0) }, { toEqual(2.0) }),
                                context({ toEqual(5.0) }, { toEqual(4.0) })
                            )
                        }.toThrow<AssertionError> {
                            message {
                                containsSize(5, 6)
                                toContain.exactly(1).value("$rootBulletPoint$containsInOrderOnlyGrouped:")
                                indexSuccess(
                                    0, 3, listOf(1.0, 2.0, 3.0, 4.0),
                                    sizeCheck(4, 4),
                                    successAfterSuccess(
                                        "$toBeDescr: 4.0",
                                        "$toBeDescr: 1.0",
                                        "$toBeDescr: 3.0",
                                        "$toBeDescr: 2.0"
                                    )
                                )
                                indexFail(
                                    4, 5, listOf(4.0),
                                    sizeCheck(1, 2),
                                    failAfterFail("$toBeDescr: 5.0"),
                                    successAfterFail("$toBeDescr: 4.0")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    nullableCases(describePrefix) {
        describeFun(containsInOrderOnlyGroupedEntries) {
            val null1null3 = { sequenceOf(null, 1.0, null, 3.0).constrainOnce().asIterable() }

            context("iterable ${null1null3().toList()}") {

                context("happy case") {
                    it("[$toBeFun(1.0), null], [null, $toBeFun(3.0)]") {
                        expect(null1null3()).containsInOrderOnlyGroupedEntriesFun(
                            context({ toEqual(1.0) }, null),
                            context(null, { toEqual(3.0) })
                        )
                    }
                    it("[null], [null, $isGreaterThanFun(2.0), $isLessThanFun(5.0)]") {
                        expect(null1null3()).containsInOrderOnlyGroupedEntriesFun(
                            context(null),
                            context(null, { toBeGreaterThan(2.0) }, { toBeLessThan(5.0) })
                        )
                    }
                }

                context("error cases (throws AssertionError)") {

                    it("[null, null], [$isLessThanFun(5.0), $isGreaterThanFun(2.0)] -- wrong order") {
                        expect {
                            expect(null1null3()).containsInOrderOnlyGroupedEntriesFun(
                                context(null, null),
                                context({ toBeLessThan(5.0) }, { toBeGreaterThan(2.0) })
                            )
                        }.toThrow<AssertionError> {
                            message {
                                toContain.exactly(1).value("$rootBulletPoint$containsInOrderOnlyGrouped:")
                                indexFail(
                                    0, 1, listOf(null, 1.0),
                                    sizeCheck(2, 2),
                                    successAfterFail("$isDescr: null"),
                                    failAfterFail("$isDescr: null")
                                )
                                indexFail(
                                    2, 3, listOf(null, 3.0),
                                    sizeCheck(2, 2),
                                    successAfterFail("$isLessThanDescr: 5.0"),
                                    failAfterFail("$isGreaterThanDescr: 2.0")
                                )
                            }
                        }
                    }

                    it("[null, $toBeFun(1.0)], [$toBeFun(3.0), null, null] -- null too much") {
                        expect {
                            expect(null1null3()).containsInOrderOnlyGroupedEntriesFun(
                                context(null, { toEqual(1.0) }),
                                context({ toEqual(3.0) }, null, null)
                            )
                        }.toThrow<AssertionError> {
                            message {
                                toContain.exactly(1).value("$rootBulletPoint$containsInOrderOnlyGrouped:")
                                indexSuccess(
                                    0, 1, listOf(null, 1.0),
                                    sizeCheck(2, 2),
                                    successAfterSuccess("$isDescr: null"),
                                    successAfterSuccess("$toBeDescr: 1.0")
                                )
                                indexFail(
                                    2, 4, listOf(null, 3.0),
                                    sizeCheck(2, 3),
                                    successAfterFail("$toBeDescr: 3.0"),
                                    successAfterFail("$isDescr: null"),
                                    failAfterFail("$isDescr: null")

                                )
                                containsSize(4, 5)
                            }
                        }
                    }
                }
            }
        }
    }
})
