package it.aegis.application.port;

import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.ReportStatus;
import java.util.Objects;

/** Intenzione di notifica persistita prima del commit del caso d'uso. */
public record NotificationIntent(UserId recipient, ReportId reportId, ReportStatus status) {

    public NotificationIntent {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(reportId, "reportId");
        Objects.requireNonNull(status, "status");
    }
}
