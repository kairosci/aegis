package it.aegis.domain.accessibility;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/** Finestra di validità di un'osservazione, fornita dalla configurazione esterna. */
public record FreshnessWindow(Duration value) {

    public FreshnessWindow {
        Objects.requireNonNull(value, "value");
        if (value.isNegative() || value.isZero()) {
            throw new IllegalArgumentException("window must be positive");
        }
    }

    public boolean covers(Instant observedAt, Instant now) {
        Objects.requireNonNull(observedAt, "observedAt");
        Objects.requireNonNull(now, "now");
        return !observedAt.isAfter(now) && !observedAt.isBefore(now.minus(value));
    }
}
