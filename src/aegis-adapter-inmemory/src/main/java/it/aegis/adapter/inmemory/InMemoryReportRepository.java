package it.aegis.adapter.inmemory;

import it.aegis.application.port.ReportRepository;
import it.aegis.domain.reports.Report;
import it.aegis.domain.reports.ReportEvent;
import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.TrackingCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Adattatore in memoria per l'aggregato segnalazione e lo storico degli eventi. */
public final class InMemoryReportRepository implements ReportRepository {

    private final Map<ReportId, Report> byId = new ConcurrentHashMap<>();
    private final Map<TrackingCode, Report> byTrackingCode = new ConcurrentHashMap<>();
    private final List<ReportEvent> events = new ArrayList<>();

    @Override
    public Optional<Report> findById(ReportId id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<Report> findByTrackingCode(TrackingCode code) {
        return Optional.ofNullable(byTrackingCode.get(code));
    }

    @Override
    public void save(Report report, ReportEvent event) {
        byId.put(report.id(), report);
        byTrackingCode.put(report.trackingCode(), report);
        events.add(event);
    }

    public List<ReportEvent> events() {
        return List.copyOf(events);
    }
}
