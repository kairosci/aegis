package it.aegis.domain.reports;

import it.aegis.domain.identity.UserId;
import java.time.Instant;
import java.util.Objects;

/**
 * Evento immutabile dello storico di una segnalazione.
 *
 * @param type tipo di evento registrato
 * @param resultingStatus stato raggiunto dall'evento
 * @param actorId identificatore dell'utente che ha eseguito la transizione
 * @param reason motivazione della transizione
 * @param occurredAt istante in cui l'evento si è verificato
 */
public record ReportEvent(
        ReportEventType type,
        ReportStatus resultingStatus,
        UserId actorId,
        String reason,
        Instant occurredAt) {

    /**
     * Verifica che tutti i dati dell'evento siano presenti.
     *
     * @param type tipo di evento registrato
     * @param resultingStatus stato raggiunto dall'evento
     * @param actorId identificatore dell'utente che ha eseguito la transizione
     * @param reason motivazione della transizione
     * @param occurredAt istante in cui l'evento si è verificato
     * @throws NullPointerException se uno degli argomenti è null
     */
    public ReportEvent {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(resultingStatus, "resultingStatus");
        Objects.requireNonNull(actorId, "actorId");
        Objects.requireNonNull(reason, "reason");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }
}
