package it.aegis.adapter.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Chiave di una prenotazione durevole di invio.
 *
 * <p>La chiave combina lo scope dell'account autenticato e il digest della chiave
 * di idempotenza, secondo il vincolo di unicità descritto nello schema.
 */
@Embeddable
public final class SubmissionReservationId implements Serializable {

    /** Versione della forma serializzata della chiave. */
    private static final long serialVersionUID = 1L;

    /** Identificativo dell'account che possiede la prenotazione. */
    @Column(name = SchemaNames.SCOPE, nullable = false, updatable = false)
    private UUID scope;

    /** Digest esadecimale della chiave di idempotenza fornita dal client. */
    @Column(name = SchemaNames.KEY_DIGEST, nullable = false, updatable = false)
    private String keyDigest;

    /** Costruttore senza argomenti richiesto dal provider JPA. */
    protected SubmissionReservationId() {
    }

    /**
     * Crea la chiave di una prenotazione.
     *
     * @param scope identificativo dell'account autenticato
     * @param keyDigest digest esadecimale della chiave di invio
     */
    public SubmissionReservationId(UUID scope, String keyDigest) {
        this.scope = Objects.requireNonNull(scope);
        this.keyDigest = Objects.requireNonNull(keyDigest);
    }

    /**
     * Restituisce lo scope della prenotazione.
     *
     * @return identificativo dell'account proprietario
     */
    public UUID scope() {
        return scope;
    }

    /**
     * Restituisce il digest della chiave di invio.
     *
     * @return digest esadecimale memorizzato nella chiave
     */
    public String keyDigest() {
        return keyDigest;
    }

    /**
     * Confronta la chiave con un'altra chiave della stessa classe.
     *
     * @param other oggetto da confrontare
     * @return {@code true} se scope e digest coincidono
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SubmissionReservationId that)) {
            return false;
        }
        return scope.equals(that.scope) && keyDigest.equals(that.keyDigest);
    }

    /**
     * Restituisce un codice hash coerente con {@link #equals(Object)}.
     *
     * @return codice hash calcolato da scope e digest
     */
    @Override
    public int hashCode() {
        return Objects.hash(scope, keyDigest);
    }
}
