package it.aegis.application.usecase;

import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.ReportStatus;
import java.util.Objects;
import java.util.Optional;

/**
 * Decisione di moderazione con motivazione e riferimento obbligatorio per i duplicati.
 * La motivazione è passata alla transizione di dominio; il riferimento è richiesto solo quando
 * la decisione è {@link ReportStatus#MARKED_DUPLICATE}.
 *
 * @param reportId segnalazione da moderare
 * @param decision stato risultante della decisione
 * @param reason motivazione della moderazione, non vuota
 * @param duplicateOf riferimento alla segnalazione originale, vuoto se non è un duplicato
 */
public record ModerateReportCommand(
        ReportId reportId,
        ReportStatus decision,
        String reason,
        Optional<ReportId> duplicateOf) {

    /**
     * Verifica che la decisione sia completa e che i duplicati abbiano un riferimento.
     *
     * @throws NullPointerException se uno dei riferimenti è {@code null}
     * @throws IllegalArgumentException se la motivazione è vuota o se una decisione
     *                                  di duplicato non indica la segnalazione originale
     */
    public ModerateReportCommand {
        Objects.requireNonNull(reportId, "reportId");
        Objects.requireNonNull(decision, "decision");
        Objects.requireNonNull(reason, "reason");
        if (reason.isBlank()) {
            throw new IllegalArgumentException("reason must not be blank");
        }
        duplicateOf = Objects.requireNonNull(duplicateOf, "duplicateOf");
        if (decision == ReportStatus.MARKED_DUPLICATE && duplicateOf.isEmpty()) {
            throw new IllegalArgumentException("duplicate decision requires a reference");
        }
    }
}
