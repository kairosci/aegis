package it.aegis.domain.accessibility;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Finestra di validità di un'osservazione, fornita dalla configurazione esterna.
 *
 * @param value durata massima trascorsa la quale un'osservazione non è più considerata valida
 */
public record FreshnessWindow(Duration value) {

    /**
     * Verifica che la durata configurata sia presente e strettamente positiva.
     *
     * @param value durata della finestra di validità
     * @throws NullPointerException se {@code value} è null
     * @throws IllegalArgumentException se la durata è negativa o uguale a zero
     */
    public FreshnessWindow {
        Objects.requireNonNull(value, "value");
        if (value.isNegative() || value.isZero()) {
            throw new IllegalArgumentException("window must be positive");
        }
    }

    /**
     * Verifica se un'osservazione cade ancora nella finestra rispetto all'istante di riferimento.
     *
     * @param observedAt istante in cui l'osservazione è stata effettuata
     * @param now istante di riferimento per il confronto
     * @return {@code true} se l'osservazione non è nel futuro e non è più vecchia della finestra
     * @throws NullPointerException se {@code observedAt} o {@code now} sono null
     */
    public boolean covers(Instant observedAt, Instant now) {
        Objects.requireNonNull(observedAt, "observedAt");
        Objects.requireNonNull(now, "now");
        return !observedAt.isAfter(now) && !observedAt.isBefore(now.minus(value));
    }
}
