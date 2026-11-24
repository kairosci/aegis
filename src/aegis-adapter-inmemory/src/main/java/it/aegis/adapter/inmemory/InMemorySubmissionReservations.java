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
 */
public final class InMemorySubmissionReservations implements SubmissionReservationPort {

    private final Map<SubmissionKey, Row> rows = new ConcurrentHashMap<>();

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

    @Override
    public void complete(SubmissionKey key, TrackingCode code) {
        Row existing = rows.get(key);
        if (existing == null) {
            throw new IllegalStateException("reservation not found");
        }
        rows.put(key, new Row(existing.fingerprint(), existing.authorId(), code));
    }

    private record Row(Fingerprint fingerprint, UserId authorId, TrackingCode code) {
    }
}
