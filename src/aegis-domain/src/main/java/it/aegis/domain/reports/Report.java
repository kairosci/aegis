package it.aegis.domain.reports;

import it.aegis.domain.identity.UserId;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Aggregato della segnalazione. Le transizioni sono validate nello stato corrente
 * e producono un evento da persistere nella stessa unità di lavoro.
 */
public final class Report {

    private final ReportId id;
    private final TrackingCode trackingCode;
    private final UserId authorId;
    private final Instant createdAt;
    private ReportStatus status;
    private ReportId duplicateOf;

    private Report(ReportId id, TrackingCode trackingCode, UserId authorId, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.trackingCode = Objects.requireNonNull(trackingCode, "trackingCode");
        this.authorId = Objects.requireNonNull(authorId, "authorId");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.status = ReportStatus.IN_VERIFICATION;
    }

    public static Report create(ReportId id, TrackingCode trackingCode, UserId authorId, Instant createdAt) {
        return new Report(id, trackingCode, authorId, createdAt);
    }

    public ReportId id() {
        return id;
    }

    public TrackingCode trackingCode() {
        return trackingCode;
    }

    public UserId authorId() {
        return authorId;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public ReportStatus status() {
        return status;
    }

    public Optional<ReportId> duplicateOf() {
        return Optional.ofNullable(duplicateOf);
    }

    public ReportEvent transition(ReportStatus next, UserId actorId, String reason, Instant occurredAt) {
        requireTransitionInputs(next, actorId, reason, occurredAt);
        if (next == ReportStatus.MARKED_DUPLICATE) {
            throw new IllegalArgumentException("use transitionToDuplicate for MARKED_DUPLICATE");
        }
        return apply(ReportEventType.valueOf(next.name()), next, actorId, reason, occurredAt);
    }

    public ReportEvent transitionToDuplicate(
            ReportId reference, UserId actorId, String reason, Instant occurredAt) {
        Objects.requireNonNull(reference, "reference");
        requireTransitionInputs(ReportStatus.MARKED_DUPLICATE, actorId, reason, occurredAt);
        ReportEvent event = apply(
                ReportEventType.MARKED_DUPLICATE, ReportStatus.MARKED_DUPLICATE,
                actorId, reason, occurredAt);
        this.duplicateOf = reference;
        return event;
    }

    private void requireTransitionInputs(
            ReportStatus next, UserId actorId, String reason, Instant occurredAt) {
        Objects.requireNonNull(next, "next");
        Objects.requireNonNull(actorId, "actorId");
        Objects.requireNonNull(reason, "reason");
        Objects.requireNonNull(occurredAt, "occurredAt");
        if (!status.allows(next)) {
            throw new IllegalStateException("transition not allowed: " + status + " -> " + next);
        }
    }

    private ReportEvent apply(
            ReportEventType type, ReportStatus next, UserId actorId, String reason, Instant occurredAt) {
        ReportEvent event = new ReportEvent(type, next, actorId, reason, occurredAt);
        this.status = next;
        return event;
    }
}
