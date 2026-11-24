package it.aegis.domain.routing;

import it.aegis.domain.accessibility.AccessibilityFeature;
import it.aegis.domain.accessibility.AccessibilityStatus;
import java.util.List;
import java.util.Objects;

/**
 * Valuta un tratto usando le sole evidenze valide. Un esito del provider senza
 * osservazioni valide resta sconosciuto e non diventa prova di compatibilità.
 */
public final class RouteAssessor {

    public RouteSegment assess(RouteCandidate candidate, List<AccessibilityFeature> evidence) {
        Objects.requireNonNull(candidate, "candidate");
        Objects.requireNonNull(evidence, "evidence");

        if (!candidate.providerVerified()) {
            return new RouteSegment(
                    candidate.from(), candidate.to(), RouteCompatibility.UNVERIFIED,
                    candidate.barrierCodes());
        }
        if (!candidate.barrierCodes().isEmpty()
                || evidence.stream().anyMatch(feature -> feature.status()
                        == AccessibilityStatus.UNAVAILABLE)) {
            return new RouteSegment(
                    candidate.from(), candidate.to(), RouteCompatibility.INCOMPATIBLE,
                    candidate.barrierCodes());
        }
        if (evidence.stream().noneMatch(AccessibilityFeature::isUsableAsEvidence)) {
            return new RouteSegment(
                    candidate.from(), candidate.to(), RouteCompatibility.UNKNOWN,
                    candidate.barrierCodes());
        }
        return new RouteSegment(
                candidate.from(), candidate.to(), RouteCompatibility.COMPATIBLE,
                candidate.barrierCodes());
    }
}
