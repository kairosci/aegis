package it.aegis.adapter.inmemory;

import it.aegis.application.port.Fingerprint;
import it.aegis.application.port.ReservationOutcome;
import it.aegis.application.port.SubmissionKey;
import it.aegis.application.port.SubmissionReservationPort;
import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.TrackingCode;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adattatore in memoria della prenotazione durevole. Non sostituisce il vincolo univoco
 * del database richiesto per la produzione.
 *
 * <p>Le prenotazioni sono tenute in una mappa concorrente e riportano gli esiti
 * definiti dalla porta: nuova, in corso, completata oppure in conflitto con
 * un'altra identità o impronta.
 */
public final class InMemorySubmissionReservations implements SubmissionReservationPort {

    /** Prenotazioni indicizzate dalla chiave di invio, con impronta, autore ed esito. */
    private final Map<SubmissionKey, Row> rows = new ConcurrentHashMap<>();

    /** Crea un registro vuoto di prenotazioni in memoria. */
    public InMemorySubmissionReservations() {
    }

    /**
     * Registra una nuova prenotazione o restituisce lo stato di una già esistente.
     *
     * @param key chiave che identifica l'invio
     * @param fingerprint impronta del contenuto inviato
     * @param authorId identità dell'autore della segnalazione
     * @return nuovo stato se la chiave non esiste, conflitto se impronta o autore
     *         differiscono, codice già assegnato se la prenotazione è completata,
     *         altrimenti stato in corso
     * @throws NullPointerException se {@code key} è {@code null}
     */
    @Override
    public ReservationOutcome reserve(
            SubmissionKey key, Fingerprint fingerprint, UserId authorId) {
        Objects.requireNonNull(key, "key");
        Row existing = rows.get(key);
        if (existing == null) {
            rows.put(key, new Row(fingerprint, authorId, null));
            return new ReservationOutcome.New();
        }
        if (!existing.fingerprint().equals(fingerprint)
                || !existing.authorId().equals(authorId)) {
            return new ReservationOutcome.Conflict();
        }
        if (existing.code() != null) {
            return new ReservationOutcome.Completed(existing.code());
        }
        return new ReservationOutcome.InProgress();
    }

    /**
     * Collega il codice di tracking a una prenotazione già presente.
     *
     * @param key chiave della prenotazione da completare
     * @param code codice di tracking assegnato
     * @throws IllegalStateException se la prenotazione non esiste
     */
    @Override
    public void complete(SubmissionKey key, TrackingCode code) {
        Row existing = rows.get(key);
        if (existing == null) {
            throw new IllegalStateException("reservation not found");
        }
        rows.put(key, new Row(existing.fingerprint(), existing.authorId(), code));
    }

    /**
     * Stato memorizzato di una prenotazione.
     *
     * @param fingerprint impronta del contenuto associato alla prenotazione
     * @param authorId autore della segnalazione
     * @param code codice di tracking, {@code null} finché la prenotazione non è completata
     */
    private record Row(Fingerprint fingerprint, UserId authorId, TrackingCode code) {
    }
}
