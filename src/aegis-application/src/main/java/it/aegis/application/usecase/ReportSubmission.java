package it.aegis.application.usecase;

import it.aegis.application.port.AuthorizationPolicy;
import it.aegis.application.port.Fingerprint;
import it.aegis.application.port.ReportRepository;
import it.aegis.application.port.ReservationOutcome;
import it.aegis.application.port.SubmissionReservationPort;
import it.aegis.application.port.TrackingCodeGenerator;
import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.Report;
import it.aegis.domain.reports.ReportEvent;
import it.aegis.domain.reports.ReportEventType;
import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.ReportStatus;
import it.aegis.domain.reports.TrackingCode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/** Caso d'uso UC-03. La prenotazione durevole precede ogni effetto esterno. */
public final class ReportSubmission {

    private static final String CANONICAL_SEPARATOR = "\u001f";

    private final SubmissionReservationPort reservations;
    private final ReportRepository reports;
    private final AuthorizationPolicy policy;
    private final TrackingCodeGenerator codes;
    private final Clock clock;

    public ReportSubmission(
            SubmissionReservationPort reservations,
            ReportRepository reports,
            AuthorizationPolicy policy,
            TrackingCodeGenerator codes,
            Clock clock) {
        this.reservations = Objects.requireNonNull(reservations, "reservations");
        this.reports = Objects.requireNonNull(reports, "reports");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.codes = Objects.requireNonNull(codes, "codes");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public TrackingCode submit(SubmitReportCommand command, UserId authorId) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(authorId, "authorId");
        if (!policy.canSubmit(authorId)) {
            throw new SecurityException("submission not authorized");
        }

        Fingerprint fingerprint = fingerprintOf(command);
        ReservationOutcome outcome = reservations.reserve(command.key(), fingerprint, authorId);

        if (outcome instanceof ReservationOutcome.Completed completed) {
            return completed.code();
        }
        if (outcome instanceof ReservationOutcome.InProgress) {
            throw new IllegalStateException("submission already in progress");
        }
        if (outcome instanceof ReservationOutcome.Conflict) {
            throw new IllegalArgumentException("submission key reused with a different payload");
        }

        TrackingCode code = codes.generate();
        Instant now = clock.instant();
        Report report = Report.create(
                new ReportId(UUID.randomUUID()), code, authorId, now);
        ReportEvent event = new ReportEvent(
                ReportEventType.CREATED, ReportStatus.IN_VERIFICATION,
                authorId, ReportEventType.CREATED.name().toLowerCase(Locale.ROOT), now);
        reports.save(report, event);
        reservations.complete(command.key(), code);
        return code;
    }

    private Fingerprint fingerprintOf(SubmitReportCommand command) {
        String canonical = command.category() + CANONICAL_SEPARATOR + command.description();
        try {
            byte[] digest = MessageDigest.getInstance(Fingerprint.algorithm())
                    .digest(canonical.getBytes(StandardCharsets.UTF_8));
            return new Fingerprint(HexFormat.of().formatHex(digest));
        } catch (NoSuchAlgorithmException cause) {
            throw new IllegalStateException(Fingerprint.algorithm() + " is required by the platform", cause);
        }
    }
}
