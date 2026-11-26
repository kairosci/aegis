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

/**
 * Adattatore in memoria per l'aggregato segnalazione e lo storico degli eventi.
 *
 * <p>Mantiene due indici, per identificativo e per codice di tracking, e registra
 * gli eventi in ordine di salvataggio. È pensato per lo sviluppo locale e le
 * verifiche, non come sostituto della persistenza durevole.
 */
public final class InMemoryReportRepository implements ReportRepository {

    /** Segnalazioni indicizzate dal loro identificativo. */
    private final Map<ReportId, Report> byId = new ConcurrentHashMap<>();

    /** Segnalazioni indicizzate dal codice di tracking pubblico. */
    private final Map<TrackingCode, Report> byTrackingCode = new ConcurrentHashMap<>();

    /** Eventi delle segnalazioni, in ordine di salvataggio. */
    private final List<ReportEvent> events = new ArrayList<>();

    /** Crea un archivio vuoto con indici per identificativo e codice di tracking. */
    public InMemoryReportRepository() {
    }

    /**
     * Cerca una segnalazione dal suo identificativo.
     *
     * @param id identificativo da cercare
     * @return segnalazione trovata, o vuoto se l'identificativo non è noto
     */
    @Override
    public Optional<Report> findById(ReportId id) {
        return Optional.ofNullable(byId.get(id));
    }

    /**
     * Cerca una segnalazione dal suo codice di tracking pubblico.
     *
     * @param code codice di tracking da cercare
     * @return segnalazione trovata, o vuoto se il codice non è noto
     */
    @Override
    public Optional<Report> findByTrackingCode(TrackingCode code) {
        return Optional.ofNullable(byTrackingCode.get(code));
    }

    /**
     * Salva l'aggregato aggiornato e accoda l'evento che lo ha prodotto.
     *
     * @param report aggregato da indicizzare
     * @param event evento da aggiungere allo storico
     */
    @Override
    public void save(Report report, ReportEvent event) {
        byId.put(report.id(), report);
        byTrackingCode.put(report.trackingCode(), report);
        events.add(event);
    }

    /**
     * Restituisce una copia non modificabile dello storico degli eventi.
     *
     * @return eventi registrati, in ordine di salvataggio
     */
    public List<ReportEvent> events() {
        return List.copyOf(events);
    }
}
