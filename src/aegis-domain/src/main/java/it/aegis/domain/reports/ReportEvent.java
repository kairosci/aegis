package it.aegis.domain.reports;

import it.aegis.domain.identity.UserId;
import java.time.Instant;
import java.util.Objects;

/** Evento immutabile dello storico di una segnalazione. */
public record ReportEvent(
        ReportEventType type,
        ReportStatus resultingStatus,
        UserId actorId,
        String reason,
        Instant occurredAt) {

    public ReportEvent {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(resultingStatus, "resultingStatus");
        Objects.requireNonNull(actorId, "actorId");
        Objects.requireNonNull(reason, "reason");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }
}
