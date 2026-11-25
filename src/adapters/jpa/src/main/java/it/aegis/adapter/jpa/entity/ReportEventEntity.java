package it.aegis.adapter.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Riga della tabella degli eventi append-only di una segnalazione.
 *
 * <p>L'identificativo numerico è una chiave tecnica di riga. Gli altri campi
 * corrispondono ai dati dell'evento di dominio necessari alla ricostruzione
 * dello stato della segnalazione.
 */
@Entity
@Table(name = SchemaNames.REPORT_EVENT)
public final class ReportEventEntity {

    /** Chiave tecnica della riga di evento. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = SchemaNames.ID, nullable = false, updatable = false)
    private Long id;

    /** Identificativo della segnalazione a cui appartiene l'evento. */
    @Column(name = SchemaNames.REPORT_ID, nullable = false, updatable = false)
    private UUID reportId;

    /** Tipo di evento memorizzato come token. */
    @Column(name = SchemaNames.EVENT_TYPE, nullable = false, updatable = false)
    private String type;

    /** Stato raggiunto dall'evento come token. */
    @Column(name = SchemaNames.RESULTING_STATUS, nullable = false, updatable = false)
    private String resultingStatus;

    /** Identificativo dell'attore che ha eseguito la transizione. */
    @Column(name = SchemaNames.AUTHOR_ID, nullable = false, updatable = false)
    private UUID actorId;

    /** Motivazione o decisione registrata con l'evento. */
    @Column(name = SchemaNames.REASON, nullable = false, updatable = false)
    private String reason;

    /** Istante in cui l'evento si è verificato. */
    @Column(name = SchemaNames.OCCURRED_AT, nullable = false, updatable = false)
    private Instant occurredAt;

    /** Identificativo della segnalazione di riferimento per un duplicato. */
    @Column(name = SchemaNames.DUPLICATE_OF, updatable = false)
    private UUID duplicateOf;

    /** Costruttore senza argomenti richiesto dal provider JPA. */
    protected ReportEventEntity() {
    }

    /**
     * Crea la riga di un evento di segnalazione.
     *
     * @param reportId identificativo della segnalazione
     * @param type tipo di evento come token
     * @param resultingStatus stato raggiunto come token
     * @param actorId identificativo dell'attore
     * @param reason motivazione dell'evento
     * @param occurredAt istante dell'evento
     * @param duplicateOf riferimento a una segnalazione duplicata, o {@code null}
     */
    public ReportEventEntity(
            UUID reportId,
            String type,
            String resultingStatus,
            UUID actorId,
            String reason,
            Instant occurredAt,
            UUID duplicateOf) {
        this.reportId = reportId;
        this.type = type;
        this.resultingStatus = resultingStatus;
        this.actorId = actorId;
        this.reason = reason;
        this.occurredAt = occurredAt;
        this.duplicateOf = duplicateOf;
    }

    /**
     * Restituisce la chiave tecnica assegnata alla riga append-only dal database.
     *
     * @return identificativo numerico, non usato per ordinare gli eventi nel tempo
     */
    public Long id() {
        return id;
    }

    /**
     * Restituisce l'identificativo della segnalazione.
     *
     * @return identificativo della segnalazione associata
     */
    public UUID reportId() {
        return reportId;
    }

    /**
     * Restituisce il token persistito del tipo di evento.
     *
     * @return token che il mapper valida prima di ricostruire la transizione di dominio
     */
    public String type() {
        return type;
    }

    /**
     * Restituisce il token dello stato prodotto dall'evento.
     *
     * @return stato atteso dopo l'evento, da verificare contro la macchina a stati
     */
    public String resultingStatus() {
        return resultingStatus;
    }

    /**
     * Restituisce l'identificativo dell'attore.
     *
     * @return identificativo dell'utente che ha eseguito la transizione
     */
    public UUID actorId() {
        return actorId;
    }

    /**
     * Restituisce la motivazione dell'evento.
     *
     * @return testo della motivazione memorizzata
     */
    public String reason() {
        return reason;
    }

    /**
     * Restituisce l'istante dell'evento.
     *
     * @return istante in cui l'evento si è verificato
     */
    public Instant occurredAt() {
        return occurredAt;
    }

    /**
     * Restituisce la segnalazione originale indicata da un evento di duplicazione.
     *
     * @return UUID della segnalazione originale per eventi di duplicazione, altrimenti {@code null}
     */
    public UUID duplicateOf() {
        return duplicateOf;
    }
}
