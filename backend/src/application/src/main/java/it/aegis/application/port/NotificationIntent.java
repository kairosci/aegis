package it.aegis.application.port;

import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.ReportStatus;
import java.util.Objects;

/**
 * Intenzione di notifica persistita prima del commit del caso d'uso.
 * Destinatario e segnalazione sono identificati da tipi di dominio, così da non
 * esporre dati dell'autore nel canale di consegna.
 *
 * @param recipient destinatario della notifica
 * @param reportId segnalazione a cui la notifica si riferisce
 * @param status stato della segnalazione al momento dell'intenzione
 */
public record NotificationIntent(UserId recipient, ReportId reportId, ReportStatus status) {

    /**
     * Verifica che tutti i riferimenti dell'intenzione siano presenti.
     *
     * @throws NullPointerException se uno dei riferimenti è {@code null}
     */
    public NotificationIntent {
        Objects.requireNonNull(recipient, "recipient");
        Objects.requireNonNull(reportId, "reportId");
        Objects.requireNonNull(status, "status");
    }
}
