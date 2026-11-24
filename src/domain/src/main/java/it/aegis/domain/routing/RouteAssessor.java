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

    /** Crea un valutatore senza stato, riutilizzabile per più tratti. */
    public RouteAssessor() {
    }

    /**
     * Deriva la compatibilità di un tratto dalle barriere segnalate e dalle evidenze disponibili.
     *
     * <p>La verifica del provider è condizione necessaria: senza verifica il
     * tratto resta non verificato. In presenza di barriere o di un'osservazione
     * di indisponibilità il tratto è incompatibile; senza almeno un'osservazione
     * utilizzabile come prova resta sconosciuto.</p>
     *
     * @param candidate risposta grezza del provider per il tratto
     * @param evidence caratteristiche di accessibilità osservate nei luoghi del tratto
     * @return tratto con la compatibilità derivata dalle evidenze
     * @throws NullPointerException se {@code candidate} o {@code evidence} sono null
     */
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
