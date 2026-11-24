package it.aegis.application.port;

import it.aegis.domain.reports.Report;
import it.aegis.domain.reports.ReportEvent;
import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.TrackingCode;
import java.util.Optional;

/**
 * Porta di persistenza dell'aggregato segnalazione e del suo evento.
 * Stato ed evento vengono salvati insieme, così che ogni transizione sia
 * accompagnata dalla registrazione dell'evento che l'ha prodotta.
 */
public interface ReportRepository {

    /**
     * Cerca una segnalazione per identificativo interno.
     *
     * @param id identificativo della segnalazione
     * @return la segnalazione trovata, o {@code Optional.empty()} se non esiste
     */
    Optional<Report> findById(ReportId id);

    /**
     * Cerca una segnalazione tramite il codice di tracking pubblico.
     *
     * @param code codice di tracking da cercare
     * @return la segnalazione trovata, o {@code Optional.empty()} se il codice è sconosciuto
     */
    Optional<Report> findByTrackingCode(TrackingCode code);

    /**
     * Salva l'aggregato aggiornato insieme all'evento della transizione.
     *
     * @param report aggregato nello stato risultante
     * @param event evento che descrive la transizione applicata
     */
    void save(Report report, ReportEvent event);
}
