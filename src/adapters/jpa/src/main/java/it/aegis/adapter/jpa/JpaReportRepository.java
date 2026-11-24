package it.aegis.adapter.jpa;

import it.aegis.adapter.jpa.entity.ReportEntity;
import it.aegis.adapter.jpa.entity.ReportEventEntity;
import it.aegis.adapter.jpa.mapper.ReportMapper;
import it.aegis.application.port.ReportRepository;
import it.aegis.domain.reports.Report;
import it.aegis.domain.reports.ReportEvent;
import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.TrackingCode;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementazione JPA della porta di persistenza dell'aggregato segnalazione.
 *
 * <p>Il salvataggio scrive lo stato della segnalazione e l'evento della
 * transizione nella stessa unità di lavoro. La demarcazione transazionale resta
 * di competenza dell'assemblaggio applicativo.
 */
public final class JpaReportRepository implements ReportRepository {

    /** Interrogazione di una segnalazione dal codice di tracking pubblico. */
    private static final String REPORT_BY_CODE =
            "select report from ReportEntity report where report.trackingCode = :code";

    /** Interrogazione degli eventi di una segnalazione in ordine cronologico. */
    private static final String EVENTS_BY_REPORT =
            "select event from ReportEventEntity event where event.reportId = :reportId order by event.occurredAt";

    /** Gestore della persistenza iniettato dal contenitore. */
    private final EntityManager entityManager;

    /**
     * Crea il repository con il gestore della persistenza iniettato.
     *
     * @param entityManager gestore della persistenza del contenitore
     */
    @Inject
    public JpaReportRepository(EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    /**
     * Cerca una segnalazione per identificativo interno.
     *
     * @param id identificativo della segnalazione
     * @return segnalazione ricostruita dal suo storico, o vuoto se non esiste
     */
    @Override
    public Optional<Report> findById(ReportId id) {
        Objects.requireNonNull(id);
        ReportEntity row = entityManager.find(ReportEntity.class, id.value());
        if (row == null) {
            return Optional.empty();
        }
        return Optional.of(ReportMapper.toDomain(row, eventsOf(id)));
    }

    /**
     * Cerca una segnalazione tramite il codice di tracking pubblico.
     *
     * @param code codice di tracking da cercare
     * @return segnalazione ricostruita dal suo storico, o vuoto se il codice è sconosciuto
     */
    @Override
    public Optional<Report> findByTrackingCode(TrackingCode code) {
        Objects.requireNonNull(code);
        List<ReportEntity> rows = entityManager
                .createQuery(REPORT_BY_CODE, ReportEntity.class)
                .setParameter("code", code.value())
                .getResultList();
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        ReportEntity row = rows.get(0);
        return Optional.of(ReportMapper.toDomain(row, eventsOf(new ReportId(row.id()))));
    }

    /**
     * Salva lo stato aggiornato e l'evento che lo ha prodotto.
     *
     * @param report aggregato nello stato risultante
     * @param event evento che descrive la transizione applicata
     */
    @Override
    public void save(Report report, ReportEvent event) {
        Objects.requireNonNull(report);
        Objects.requireNonNull(event);
        entityManager.merge(ReportMapper.toEntity(report));
        entityManager.flush();
        entityManager.persist(ReportMapper.toEventEntity(report, event));
    }

    /**
     * Carica gli eventi della segnalazione ordinati per istante di occorrenza.
     *
     * @param id identificativo della segnalazione
     * @return elenco degli eventi persistenti
     */
    private List<ReportEventEntity> eventsOf(ReportId id) {
        return entityManager
                .createQuery(EVENTS_BY_REPORT, ReportEventEntity.class)
                .setParameter("reportId", id.value())
                .getResultList();
    }
}
