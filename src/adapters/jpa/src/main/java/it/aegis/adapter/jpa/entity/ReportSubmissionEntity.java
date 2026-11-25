package it.aegis.adapter.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/**
 * Riga della tabella delle prenotazioni durevoli degli invii.
 *
 * <p>La classe espone un metodo di completamento con nome di dominio. I campi sono
 * aggiornati solo tramite tale metodo, così lo stato non viene impostato in modo
 * arbitrario dal codice di persistenza.
 */
@Entity
@Table(name = SchemaNames.REPORT_SUBMISSION)
public final class ReportSubmissionEntity {

    /** Chiave composta da scope e digest della chiave di invio. */
    @EmbeddedId
    private SubmissionReservationId id;

    /** Impronta canonica del contenuto dell'invio prenotato. */
    @Column(name = SchemaNames.FINGERPRINT, nullable = false, updatable = false)
    private String fingerprint;

    /** Stato della prenotazione. */
    @Enumerated(EnumType.STRING)
    @Column(name = SchemaNames.STATUS, nullable = false)
    private SubmissionState status;

    /** Codice di tracking assegnato alla segnalazione completata. */
    @Column(name = SchemaNames.TRACKING_CODE)
    private String trackingCode;

    /** Costruttore senza argomenti richiesto dal provider JPA. */
    protected ReportSubmissionEntity() {
    }

    /**
     * Crea una prenotazione in corso.
     *
     * @param id chiave composta da scope e digest della chiave
     * @param fingerprint impronta canonica dell'invio
     */
    public ReportSubmissionEntity(SubmissionReservationId id, String fingerprint) {
        this.id = id;
        this.fingerprint = fingerprint;
        this.status = SubmissionState.PENDING;
    }

    /**
     * Completa la prenotazione collegando il codice di tracking assegnato.
     *
     * @param code codice di tracking della segnalazione salvata
     */
    public void markCompleted(String code) {
        this.trackingCode = code;
        this.status = SubmissionState.COMPLETED;
    }

    /**
     * Restituisce la chiave composta che limita l'idempotenza all'account autenticato.
     *
     * @return coppia tra account proprietario e digest della chiave fornita dal client
     */
    public SubmissionReservationId id() {
        return id;
    }

    /**
     * Restituisce l'impronta del payload associato alla prima richiesta con questa chiave.
     * I retry la confrontano senza sostituire il contenuto già prenotato.
     *
     * @return digest esadecimale di categoria e descrizione
     */
    public String fingerprint() {
        return fingerprint;
    }

    /**
     * Restituisce lo stato della prenotazione.
     *
     * @return stato corrente della prenotazione
     */
    public SubmissionState status() {
        return status;
    }

    /**
     * Restituisce il codice assegnato dopo il salvataggio della segnalazione.
     *
     * @return codice da restituire ai retry completati, o {@code null} mentre la prenotazione è in corso
     */
    public String trackingCode() {
        return trackingCode;
    }
}
