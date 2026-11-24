package it.aegis.application.usecase;

import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.ReportStatus;
import java.util.Objects;
import java.util.Optional;

/** Decisione di moderazione con motivazione e riferimento obbligatorio per i duplicati. */
public record ModerateReportCommand(
        ReportId reportId,
        ReportStatus decision,
        String reason,
        Optional<ReportId> duplicateOf) {

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
