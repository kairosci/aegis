package it.aegis.application.port;

import it.aegis.domain.reports.TrackingCode;

/**
 * Esiti distinti della prenotazione durevole di un invio.
 * L'esito guida il caso d'uso senza esporre dettagli di persistenza: solo
 * {@link Completed} contiene il codice di tracking già assegnato.
 */
public sealed interface ReservationOutcome
        permits ReservationOutcome.New,
                ReservationOutcome.InProgress,
                ReservationOutcome.Completed,
                ReservationOutcome.Conflict {

    /**
     * Nessuna prenotazione precedente: l'invio può proseguire.
     */
    record New() implements ReservationOutcome {
    }

    /**
     * Un'altra richiesta con la stessa chiave è in corso.
     */
    record InProgress() implements ReservationOutcome {
    }

    /**
     * Prenotazione già completata per la stessa chiave: l'invio non va ripetuto.
     *
     * @param code codice di tracking assegnato alla prenotazione completata
     */
    record Completed(TrackingCode code) implements ReservationOutcome {
    }

    /**
     * La chiave è già associata a un'impronta diversa.
     */
    record Conflict() implements ReservationOutcome {
    }
}
