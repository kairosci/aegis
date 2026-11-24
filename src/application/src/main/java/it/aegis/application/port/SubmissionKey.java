package it.aegis.application.port;

import java.util.Objects;

/**
 * Chiave di idempotenza fornita dal client nello scope dell'account autenticato.
 * Il valore identifica l'invio nello scope dell'account autenticato e non deve
 * contenere dati personali.
 *
 * @param value valore non vuoto della chiave
 */
public record SubmissionKey(String value) {

    /**
     * Verifica che la chiave sia presente e non costituita solo da spazi.
     *
     * @throws NullPointerException se {@code value} è {@code null}
     * @throws IllegalArgumentException se il valore è vuoto o solo spazi
     */
    public SubmissionKey {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("submission key must not be blank");
        }
    }
}
