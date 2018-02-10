package ch.tutteli.atrium.creating.any.typetransformation.failurehandlers

import ch.tutteli.atrium.assertions.Assertion
import ch.tutteli.atrium.assertions.builders.AssertionBuilder
import ch.tutteli.atrium.assertions.builders.invisibleGroup
import ch.tutteli.atrium.creating.AssertionCollector
import ch.tutteli.atrium.creating.AssertionPlant
import ch.tutteli.atrium.creating.any.typetransformation.AnyTypeTransformation
import ch.tutteli.atrium.creating.any.typetransformation.AnyTypeTransformation.ParameterObject
import ch.tutteli.atrium.reporting.translating.Translatable

abstract class ExplanatoryFailureHandlerBase<in S : Any, out T : Any> : AnyTypeTransformation.FailureHandler<S, T> {

    override fun createAndAddAssertionToPlant(parameterObject: ParameterObject<S, T>) {
        val explanatoryAssertions = collectAssertions(parameterObject.warningTransformationFailed, parameterObject.assertionCreator)
        val assertion = AssertionBuilder.invisibleGroup.create(listOf(
            createFailingAssertion(parameterObject.description, parameterObject.representation),
            AssertionBuilder.explanatoryGroup.withDefault.create(explanatoryAssertions)
        ))
        parameterObject.subjectPlant.addAssertion(assertion)
    }

    private fun collectAssertions(
        warningDownCastFailed: Translatable,
        assertionCreator: AssertionPlant<T>.() -> Unit
    ) = AssertionCollector
        .doNotThrowIfNoAssertionIsCollected
        .collectAssertionsForExplanation(warningDownCastFailed, assertionCreator, null)

    abstract fun createFailingAssertion(description: Translatable, representation: Any): Assertion

}