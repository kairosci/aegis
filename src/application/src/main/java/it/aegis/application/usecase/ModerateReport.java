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

/**
 * Caso d'uso UC-05. Stato, evento e intenzione di notifica cambiano insieme.
 * L'autorizzazione è verificata lato server prima di leggere l'aggregato e la
 * transizione è applicata dal dominio.
 */
public final class ModerateReport {

    /** Porta di persistenza dell'aggregato segnalazione e del suo evento. */
    private final ReportRepository reports;
    /** Politica di autorizzazione lato server per la moderazione. */
    private final AuthorizationPolicy policy;
    /** Porta che registra l'intenzione di notifica nella stessa unità di lavoro. */
    private final NotificationPort notifications;
    /** Orologio iniettato per datare l'evento di moderazione. */
    private final Clock clock;

    /**
     * Crea il caso d'uso con le porte e l'orologio da utilizzare.
     *
     * @param reports porta di persistenza dell'aggregato
     * @param policy politica di autorizzazione lato server
     * @param notifications porta di scrittura delle intenzioni di notifica
     * @param clock orologio usato per datare l'evento
     * @throws NullPointerException se una delle dipendenze è {@code null}
     */
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

    /**
     * Applica una decisione di moderazione alla segnalazione indicata.
     * Una decisione di duplicato usa la transizione dedicata, che richiede il
     * riferimento alla segnalazione originale.
     *
     * @param command decisione di moderazione già validata
     * @param adminId amministratore che esegue la moderazione
     * @throws NullPointerException se {@code command} o {@code adminId} è {@code null}
     * @throws SecurityException se l'amministratore non è autorizzato a moderare
     * @throws IllegalArgumentException se la segnalazione non esiste o se manca il
     *                                  riferimento per una decisione di duplicato
     */
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
