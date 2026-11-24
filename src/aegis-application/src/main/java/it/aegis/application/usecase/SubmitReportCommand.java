package it.aegis.application.usecase;

import it.aegis.application.port.SubmissionKey;
import java.util.Objects;

/** Dati validati dell'invio prima della prenotazione durevole. */
public record SubmitReportCommand(SubmissionKey key, String category, String description) {

    public SubmitReportCommand {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(description, "description");
        if (category.isBlank() || description.isBlank()) {
            throw new IllegalArgumentException("category and description must not be blank");
        }
    }
}
