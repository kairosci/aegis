package it.aegis.adapter.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Chiave naturale di un'osservazione di accessibilità.
 *
 * <p>La coppia luogo e codice identifica la caratteristica osservata senza
 * introdurre un identificativo tecnico non previsto dallo schema.
 */
@Embeddable
public final class AccessibilityFeatureId implements Serializable {

    /** Versione della forma serializzata della chiave. */
    private static final long serialVersionUID = 1L;

    /** Identificativo del luogo a cui appartiene l'osservazione. */
    @Column(name = SchemaNames.PLACE_ID, nullable = false, updatable = false)
    private UUID placeId;

    /** Codice della caratteristica osservata. */
    @Column(name = SchemaNames.CODE, nullable = false, updatable = false)
    private String code;

    /** Costruttore senza argomenti richiesto dal provider JPA. */
    protected AccessibilityFeatureId() {
    }

    /**
     * Crea la chiave di una caratteristica osservata in un luogo.
     *
     * @param placeId identificativo del luogo
     * @param code codice della caratteristica
     */
    public AccessibilityFeatureId(UUID placeId, String code) {
        this.placeId = Objects.requireNonNull(placeId);
        this.code = Objects.requireNonNull(code);
    }

    /**
     * Restituisce l'identificativo del luogo.
     *
     * @return identificativo del luogo osservato
     */
    public UUID placeId() {
        return placeId;
    }

    /**
     * Restituisce il codice della caratteristica.
     *
     * @return codice della caratteristica osservata
     */
    public String code() {
        return code;
    }

    /**
     * Confronta la chiave con un'altra chiave della stessa classe.
     *
     * @param other oggetto da confrontare
     * @return {@code true} se luogo e codice coincidono
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AccessibilityFeatureId that)) {
            return false;
        }
        return placeId.equals(that.placeId) && code.equals(that.code);
    }

    /**
     * Restituisce un codice hash coerente con {@link #equals(Object)}.
     *
     * @return codice hash calcolato da luogo e codice
     */
    @Override
    public int hashCode() {
        return Objects.hash(placeId, code);
    }
}
