package it.aegis.application.usecase;

import it.aegis.domain.reports.ReportStatus;
import it.aegis.domain.reports.TrackingCode;
import java.util.Objects;

/** Rappresentazione pubblica della segnalazione, priva di dati dell'autore. */
public record PublicReportStatus(TrackingCode code, ReportStatus status) {

    public PublicReportStatus {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(status, "status");
    }
}
