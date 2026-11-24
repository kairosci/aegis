package it.aegis.adapter.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Riga della tabella delle segnalazioni.
 *
 * <p>La classe conserva i campi necessari alle porte di persistenza. Il codice di
 * tracking è memorizzato come valore perché il dominio deve poter ricostruire
 * l'aggregato e la prenotazione completata deve poter restituire la credenziale.
 */
@Entity
@Table(name = SchemaNames.REPORT)
public final class ReportEntity {

    /** Identificativo interno della segnalazione. */
    @Id
    @Column(name = SchemaNames.ID, nullable = false, updatable = false)
    private UUID id;

    /** Codice di tracking pubblico della segnalazione. */
    @Column(name = SchemaNames.TRACKING_CODE, nullable = false, unique = true)
    private String trackingCode;

    /** Identificativo dell'autore della segnalazione. */
    @Column(name = SchemaNames.AUTHOR_ID, nullable = false)
    private UUID authorId;

    /** Istante di creazione della segnalazione. */
    @Column(name = SchemaNames.CREATED_AT, nullable = false, updatable = false)
    private Instant createdAt;

    /** Stato corrente memorizzato come token. */
    @Column(name = SchemaNames.STATUS, nullable = false)
    private String status;

    /** Identificativo della segnalazione di riferimento per un duplicato. */
    @Column(name = SchemaNames.DUPLICATE_OF)
    private UUID duplicateOf;

    /** Costruttore senza argomenti richiesto dal provider JPA. */
    protected ReportEntity() {
    }

    /**
     * Crea la riga di una segnalazione.
     *
     * @param id identificativo interno
     * @param trackingCode codice di tracking pubblico
     * @param authorId identificativo dell'autore
     * @param createdAt istante di creazione
     * @param status stato corrente come token
     * @param duplicateOf riferimento a una segnalazione duplicata, o {@code null}
     */
    public ReportEntity(
            UUID id,
            String trackingCode,
            UUID authorId,
            Instant createdAt,
            String status,
            UUID duplicateOf) {
        this.id = id;
        this.trackingCode = trackingCode;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.status = status;
        this.duplicateOf = duplicateOf;
    }

    /**
     * Restituisce l'identificativo interno.
     *
     * @return identificativo della segnalazione
     */
    public UUID id() {
        return id;
    }

    /**
     * Restituisce il codice di tracking pubblico.
     *
     * @return codice memorizzato nella riga
     */
    public String trackingCode() {
        return trackingCode;
    }

    /**
     * Restituisce l'identificativo dell'autore.
     *
     * @return identificativo dell'account autore
     */
    public UUID authorId() {
        return authorId;
    }

    /**
     * Restituisce l'istante di creazione.
     *
     * @return istante memorizzato nella riga
     */
    public Instant createdAt() {
        return createdAt;
    }

    /**
     * Restituisce lo stato corrente come token.
     *
     * @return token di stato memorizzato nella riga
     */
    public String status() {
        return status;
    }

    /**
     * Restituisce il riferimento a una segnalazione duplicata.
     *
     * @return identificativo di riferimento, o {@code null} se non è un duplicato
     */
    public UUID duplicateOf() {
        return duplicateOf;
    }
}
