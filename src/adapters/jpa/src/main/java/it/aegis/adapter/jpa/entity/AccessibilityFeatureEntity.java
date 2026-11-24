package it.aegis.adapter.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Riga della tabella delle caratteristiche di accessibilità osservate.
 *
 * <p>Lo stato letto dal database è un token grezzo. Il mapper lo converte in uno
 * stato di dominio solo quando l'osservazione è completa e ancora valida.
 */
@Entity
@Table(name = SchemaNames.ACCESSIBILITY_FEATURE)
public final class AccessibilityFeatureEntity {

    /** Chiave naturale composta da luogo e codice della caratteristica. */
    @EmbeddedId
    private AccessibilityFeatureId id;

    /** Stato osservato memorizzato come token. */
    @Column(name = SchemaNames.STATUS)
    private String status;

    /** Fonte che ha fornito l'osservazione. */
    @Column(name = SchemaNames.SOURCE)
    private String source;

    /** Istante in cui l'osservazione è stata effettuata. */
    @Column(name = SchemaNames.OBSERVED_AT)
    private Instant observedAt;

    /** Costruttore senza argomenti richiesto dal provider JPA. */
    protected AccessibilityFeatureEntity() {
    }

    /**
     * Crea la riga di un'osservazione di accessibilità.
     *
     * @param id chiave naturale della caratteristica
     * @param status stato osservato come token di persistenza
     * @param source fonte dell'osservazione
     * @param observedAt istante dell'osservazione
     */
    public AccessibilityFeatureEntity(
            AccessibilityFeatureId id, String status, String source, Instant observedAt) {
        this.id = id;
        this.status = status;
        this.source = source;
        this.observedAt = observedAt;
    }

    /**
     * Restituisce la chiave naturale della caratteristica.
     *
     * @return chiave composta da luogo e codice
     */
    public AccessibilityFeatureId id() {
        return id;
    }

    /**
     * Restituisce lo stato osservato grezzo.
     *
     * @return token di stato letto dal database
     */
    public String status() {
        return status;
    }

    /**
     * Restituisce la fonte dell'osservazione.
     *
     * @return fonte memorizzata nella riga
     */
    public String source() {
        return source;
    }

    /**
     * Restituisce l'istante dell'osservazione.
     *
     * @return istante memorizzato nella riga
     */
    public Instant observedAt() {
        return observedAt;
    }
}
