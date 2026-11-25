package it.aegis.adapter.jpa.mapper;

import it.aegis.adapter.jpa.entity.ReportEntity;
import it.aegis.adapter.jpa.entity.ReportEventEntity;
import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.Report;
import it.aegis.domain.reports.ReportEvent;
import it.aegis.domain.reports.ReportEventType;
import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.ReportStatus;
import it.aegis.domain.reports.TrackingCode;
import java.util.List;

/**
 * Conversione esplicita tra righe persistenti e aggregato segnalazione.
 *
 * <p>In lettura lo stato non è copiato dalla colonna, ma ricostruito applicando
 * lo storico degli eventi alle transizioni validate dal dominio. La riga deve
 * essere coerente con lo stato risultante, altrimenti la ricostruzione viene
 * interrotta invece di restituire un aggregato non attendibile.
 */
public final class ReportMapper {

    /** Impedisce l'istanziazione di una classe che espone solo metodi statici. */
    private ReportMapper() {
    }

    /**
     * Costruisce l'aggregato dalla riga della segnalazione e dal suo storico.
     *
     * @param row riga della segnalazione
     * @param events eventi ordinati per istante di occorrenza
     * @return aggregato ricostruito tramite le transizioni di dominio
     * @throws IllegalStateException se lo storico non è completo o non è coerente con lo stato memorizzato
     */
    public static Report toDomain(ReportEntity row, List<ReportEventEntity> events) {
        Report report = Report.create(
                new ReportId(row.id()),
                new TrackingCode(row.trackingCode()),
                new UserId(row.authorId()),
                row.createdAt());
        for (ReportEventEntity event : events) {
            apply(report, event);
        }
        ReportStatus expected = statusOf(row.status());
        if (expected == null || report.status() != expected) {
            throw new IllegalStateException();
        }
        return report;
    }

    /**
     * Converte l'aggregato nella riga della tabella delle segnalazioni.
     *
     * @param report aggregato da persistere
     * @return riga con i valori dell'aggregato
     */
    public static ReportEntity toEntity(Report report) {
        return new ReportEntity(
                report.id().value(),
                report.trackingCode().value(),
                report.authorId().value(),
                report.createdAt(),
                report.status().name(),
                report.duplicateOf().map(ReportId::value).orElse(null));
    }

    /**
     * Converte l'evento di transizione nella riga dello storico.
     *
     * @param report aggregato a cui appartiene l'evento
     * @param event evento prodotto dalla transizione
     * @return riga dell'evento con il riferimento alla segnalazione
     */
    public static ReportEventEntity toEventEntity(Report report, ReportEvent event) {
        return new ReportEventEntity(
                report.id().value(),
                event.type().name(),
                event.resultingStatus().name(),
                event.actorId().value(),
                event.reason(),
                event.occurredAt(),
                report.duplicateOf().map(ReportId::value).orElse(null));
    }

    /**
     * Applica un evento letto dal database all'aggregato in ricostruzione.
     *
     * @param report aggregato in ricostruzione
     * @param event riga dell'evento da applicare
     * @throws IllegalStateException se l'evento è incompleto, incoerente o non ammesso dallo stato corrente
     */
    private static void apply(Report report, ReportEventEntity event) {
        ReportStatus target = statusOf(event.resultingStatus());
        ReportEventType type = typeOf(event.type());
        if (target == null
                || type == null
                || event.actorId() == null
                || event.reason() == null
                || event.occurredAt() == null) {
            throw new IllegalStateException();
        }
        if (type == ReportEventType.CREATED) {
            if (target != ReportStatus.IN_VERIFICATION
                    || report.status() != ReportStatus.IN_VERIFICATION) {
                throw new IllegalStateException();
            }
            return;
        }
        if (!type.name().equals(target.name())) {
            throw new IllegalStateException();
        }
        UserId actor = new UserId(event.actorId());
        if (type == ReportEventType.MARKED_DUPLICATE) {
            if (event.duplicateOf() == null) {
                throw new IllegalStateException();
            }
            report.transitionToDuplicate(
                    new ReportId(event.duplicateOf()), actor, event.reason(), event.occurredAt());
            return;
        }
        report.transition(target, actor, event.reason(), event.occurredAt());
    }

    /**
     * Interpreta il token di stato letto dal database.
     *
     * @param value token di stato, eventualmente assente
     * @return stato corrispondente, o {@code null} se il token non è noto
     */
    private static ReportStatus statusOf(String value) {
        if (value == null) {
            return null;
        }
        try {
            return ReportStatus.valueOf(value);
        } catch (IllegalArgumentException cause) {
            return null;
        }
    }

    /**
     * Interpreta il token di tipo di evento letto dal database.
     *
     * @param value token di tipo, eventualmente assente
     * @return tipo corrispondente, o {@code null} se il token non è noto
     */
    private static ReportEventType typeOf(String value) {
        if (value == null) {
            return null;
        }
        try {
            return ReportEventType.valueOf(value);
        } catch (IllegalArgumentException cause) {
            return null;
        }
    }
}
