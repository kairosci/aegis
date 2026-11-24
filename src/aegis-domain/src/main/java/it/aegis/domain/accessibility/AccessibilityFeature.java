package it.aegis.domain.accessibility;

import java.time.Instant;
import java.util.Objects;

/** Caratteristica osservata di un luogo con fonte, stato e data di osservazione. */
public record AccessibilityFeature(
        String code,
        AccessibilityStatus status,
        String source,
        Instant observedAt) {

    public AccessibilityFeature {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(observedAt, "observedAt");
        if (code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
    }

    /**
     * Classifica come sconosciuto ogni stato assente o osservato fuori dalla finestra di validità.
     */
    public static AccessibilityFeature observed(
            String code,
            AccessibilityStatus status,
            String source,
            Instant observedAt,
            FreshnessWindow window,
            Instant now) {
        Objects.requireNonNull(observedAt, "observedAt");
        Objects.requireNonNull(window, "window");
        AccessibilityStatus effective = status != null && window.covers(observedAt, now)
                ? status
                : AccessibilityStatus.UNKNOWN;
        return new AccessibilityFeature(code, effective, source, observedAt);
    }

    public boolean isUsableAsEvidence() {
        return status == AccessibilityStatus.AVAILABLE;
    }
}
