package it.aegis.config;

import java.time.Duration;
import java.util.Objects;

/** Configurazione tipizzata. Ogni valore deriva dal file esterno e nessuno ha un default silenzioso. */
public record AegisConfig(
        Duration freshnessWindow,
        long maxAttachmentBytes,
        int trackingEntropyBits,
        String trackingAlphabet,
        double earthRadiusMetres) {

    public AegisConfig {
        Objects.requireNonNull(freshnessWindow, "freshnessWindow");
        Objects.requireNonNull(trackingAlphabet, "trackingAlphabet");
        if (freshnessWindow.isNegative() || freshnessWindow.isZero()) {
            throw new IllegalArgumentException("freshnessWindow must be positive");
        }
        if (maxAttachmentBytes <= 0L) {
            throw new IllegalArgumentException("maxAttachmentBytes must be positive");
        }
        if (trackingEntropyBits <= 0 || trackingEntropyBits % Byte.SIZE != 0) {
            throw new IllegalArgumentException("trackingEntropyBits must be a positive multiple of Byte.SIZE");
        }
        if (trackingAlphabet.isBlank()) {
            throw new IllegalArgumentException("trackingAlphabet must not be blank");
        }
        if (!Double.isFinite(earthRadiusMetres) || earthRadiusMetres <= 0.0) {
            throw new IllegalArgumentException("earthRadiusMetres must be positive");
        }
    }

    public int trackingTokenBytes() {
        return trackingEntropyBits / Byte.SIZE;
    }

    public int trackingBase() {
        return trackingAlphabet.length();
    }
}
