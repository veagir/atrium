module ch.tutteli.atrium.verbs {
    requires ch.tutteli.atrium.domain.builders;
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core;

    uses ch.tutteli.atrium.reporting.ReporterFactory;

    exports ch.tutteli.atrium.api.verbs;
    exports ch.tutteli.atrium.verbs;
}
