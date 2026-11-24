package it.aegis.application.usecase;

import it.aegis.domain.reports.ReportStatus;
import it.aegis.domain.reports.TrackingCode;
import java.util.Objects;

/**
 * Rappresentazione pubblica della segnalazione, priva di dati dell'autore.
 * È la sola rappresentazione da restituire a chi conosce il codice di tracking.
 *
 * @param code codice di tracking della segnalazione
 * @param status stato pubblico della segnalazione
 */
public record PublicReportStatus(TrackingCode code, ReportStatus status) {

    /**
     * Verifica che il codice e lo stato siano presenti.
     *
     * @throws NullPointerException se {@code code} o {@code status} è {@code null}
     */
    public PublicReportStatus {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(status, "status");
    }
}
