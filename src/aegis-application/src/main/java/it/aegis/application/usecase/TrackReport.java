package it.aegis.application.usecase;

import it.aegis.application.port.ReportRepository;
import it.aegis.domain.reports.Report;
import it.aegis.domain.reports.TrackingCode;
import java.util.Objects;

/** Caso d'uso UC-04. Restituisce solo lo stato pubblico consentito. */
public final class TrackReport {

    private final ReportRepository reports;

    public TrackReport(ReportRepository reports) {
        this.reports = Objects.requireNonNull(reports, "reports");
    }

    public PublicReportStatus track(TrackingCode code) {
        Objects.requireNonNull(code, "code");
        Report report = reports.findByTrackingCode(code)
                .orElseThrow(() -> new IllegalArgumentException("tracking code not found"));
        return new PublicReportStatus(code, report.status());
    }
}
