package it.aegis.application.port;

import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.TrackingCode;

/**
 * Prenotazione durevole che serializza i retry prima degli effetti esterni.
 * La prenotazione distingue un nuovo invio da un retry già completato, in corso
 * o in conflitto con un contenuto diverso.
 */
public interface SubmissionReservationPort {

    /**
     * Prenota l'invio prima di generare il codice e di persistere la segnalazione.
     *
     * @param key chiave di idempotenza fornita dal client
     * @param fingerprint impronta canonica del contenuto dell'invio
     * @param authorId autore autenticato della segnalazione
     * @return esito della prenotazione, che determina se proseguire o ripetere l'invio
     */
    ReservationOutcome reserve(SubmissionKey key, Fingerprint fingerprint, UserId authorId);

    /**
     * Completa la prenotazione dopo il salvataggio dell'aggregato.
     *
     * @param key chiave di idempotenza della prenotazione da completare
     * @param code codice di tracking assegnato alla segnalazione salvata
     */
    void complete(SubmissionKey key, TrackingCode code);
}
