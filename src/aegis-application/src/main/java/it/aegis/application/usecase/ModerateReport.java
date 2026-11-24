package it.aegis.application.usecase;

import it.aegis.application.port.AuthorizationPolicy;
import it.aegis.application.port.NotificationIntent;
import it.aegis.application.port.NotificationPort;
import it.aegis.application.port.ReportRepository;
import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.Report;
import it.aegis.domain.reports.ReportEvent;
import it.aegis.domain.reports.ReportStatus;
import java.time.Clock;
import java.util.Objects;

/** Caso d'uso UC-05. Stato, evento e intenzione di notifica cambiano insieme. */
public final class ModerateReport {

    private final ReportRepository reports;
    private final AuthorizationPolicy policy;
    private final NotificationPort notifications;
    private final Clock clock;

    public ModerateReport(
            ReportRepository reports,
            AuthorizationPolicy policy,
            NotificationPort notifications,
            Clock clock) {
        this.reports = Objects.requireNonNull(reports, "reports");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.notifications = Objects.requireNonNull(notifications, "notifications");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public void moderate(ModerateReportCommand command, UserId adminId) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(adminId, "adminId");
        if (!policy.canModerate(adminId)) {
            throw new SecurityException("moderation not authorized");
        }

        Report report = reports.findById(command.reportId())
                .orElseThrow(() -> new IllegalArgumentException("report not found"));
        ReportEvent event = command.decision() == ReportStatus.MARKED_DUPLICATE
                ? report.transitionToDuplicate(
                        command.duplicateOf().orElseThrow(), adminId,
                        command.reason(), clock.instant())
                : report.transition(
                        command.decision(), adminId, command.reason(), clock.instant());

        reports.save(report, event);
        notifications.enqueue(
                new NotificationIntent(report.authorId(), report.id(), event.resultingStatus()));
    }
}
