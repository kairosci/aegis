package it.aegis.application.port;

import it.aegis.domain.reports.Report;
import it.aegis.domain.reports.ReportEvent;
import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.TrackingCode;
import java.util.Optional;

/** Porta di persistenza dell'aggregato segnalazione e del suo evento. */
public interface ReportRepository {

    Optional<Report> findById(ReportId id);

    Optional<Report> findByTrackingCode(TrackingCode code);

    void save(Report report, ReportEvent event);
}
