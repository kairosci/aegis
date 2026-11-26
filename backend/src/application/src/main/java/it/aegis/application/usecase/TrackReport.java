package it.aegis.application.usecase;

import it.aegis.application.port.ReportRepository;
import it.aegis.domain.reports.Report;
import it.aegis.domain.reports.TrackingCode;
import java.util.Objects;

/**
 * Caso d'uso UC-04. Restituisce solo lo stato pubblico consentito.
 * L'aggregato viene letto tramite il codice di tracking e non viene esposto
 * alcun dato dell'autore.
 */
public final class TrackReport {

    /** Porta di persistenza dell'aggregato segnalazione. */
    private final ReportRepository reports;

    /**
     * Crea il caso d'uso con la porta di persistenza da utilizzare.
     *
     * @param reports porta di persistenza dell'aggregato
     * @throws NullPointerException se {@code reports} è {@code null}
     */
    public TrackReport(ReportRepository reports) {
        this.reports = Objects.requireNonNull(reports, "reports");
    }

    /**
     * Restituisce lo stato pubblico associato a un codice di tracking.
     *
     * @param code codice di tracking della segnalazione
     * @return stato pubblico della segnalazione
     * @throws NullPointerException se {@code code} è {@code null}
     * @throws IllegalArgumentException se il codice di tracking non è noto
     */
    public PublicReportStatus track(TrackingCode code) {
        Objects.requireNonNull(code, "code");
        Report report = reports.findByTrackingCode(code)
                .orElseThrow(() -> new IllegalArgumentException("tracking code not found"));
        return new PublicReportStatus(code, report.status());
    }
}
