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
     * Restituisce la chiave tecnica usata dalle relazioni persistenti.
     *
     * @return UUID interno della segnalazione, distinto dal codice pubblico di tracking
     */
    public UUID id() {
        return id;
    }

    /**
     * Restituisce il token di tracking conservato nella riga.
     *
     * @return codice opaco per il tracciamento pubblico, distinto dall'identificativo interno
     */
    public String trackingCode() {
        return trackingCode;
    }

    /**
     * Restituisce l'UUID dell'account autore conservato per le verifiche interne.
     *
     * @return identificativo non destinato alla risposta pubblica di tracking
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
     * Restituisce il token dello stato memorizzato nella riga principale.
     * Il mapper lo confronta con lo stato ricostruito dallo storico e rifiuta
     * la riga se i due valori non coincidono.
     *
     * @return token non interpretato a livello JPA
     */
    public String status() {
        return status;
    }

    /**
     * Restituisce l'UUID della segnalazione originale quando questa riga rappresenta un duplicato.
     *
     * @return identificativo della segnalazione originale, oppure {@code null} negli altri stati
     */
    public UUID duplicateOf() {
        return duplicateOf;
    }
}
