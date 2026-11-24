package it.aegis.application.port;

import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.TrackingCode;

/** Prenotazione durevole che serializza i retry prima degli effetti esterni. */
public interface SubmissionReservationPort {

    ReservationOutcome reserve(SubmissionKey key, Fingerprint fingerprint, UserId authorId);

    void complete(SubmissionKey key, TrackingCode code);
}
