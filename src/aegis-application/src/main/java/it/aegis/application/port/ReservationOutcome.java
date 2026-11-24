package it.aegis.application.port;

import it.aegis.domain.reports.TrackingCode;

/** Esiti distinti della prenotazione durevole di un invio. */
public sealed interface ReservationOutcome
        permits ReservationOutcome.New,
                ReservationOutcome.InProgress,
                ReservationOutcome.Completed,
                ReservationOutcome.Conflict {

    record New() implements ReservationOutcome {
    }

    record InProgress() implements ReservationOutcome {
    }

    record Completed(TrackingCode code) implements ReservationOutcome {
    }

    record Conflict() implements ReservationOutcome {
    }
}
