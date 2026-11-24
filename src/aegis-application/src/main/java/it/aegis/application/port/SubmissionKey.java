package it.aegis.application.port;

import java.util.Objects;

/** Chiave di idempotenza fornita dal client nello scope dell'account autenticato. */
public record SubmissionKey(String value) {

    public SubmissionKey {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("submission key must not be blank");
        }
    }
}
