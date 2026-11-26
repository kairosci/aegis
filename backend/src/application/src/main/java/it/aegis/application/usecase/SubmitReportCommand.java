package it.aegis.application.usecase;

import it.aegis.application.port.SubmissionKey;
import java.util.Objects;

/**
 * Dati validati dell'invio prima della prenotazione durevole.
 * Categoria e descrizione non vuote sono incluse nell'impronta che identifica
 * l'invio ai fini dell'idempotenza.
 *
 * @param key chiave di idempotenza fornita dal client
 * @param category categoria della segnalazione, non vuota
 * @param description descrizione della segnalazione, non vuota
 */
public record SubmitReportCommand(SubmissionKey key, String category, String description) {

    /**
     * Verifica che chiave, categoria e descrizione siano presenti e non vuote.
     *
     * @throws NullPointerException se uno dei riferimenti è {@code null}
     * @throws IllegalArgumentException se categoria o descrizione è vuota o solo spazi
     */
    public SubmitReportCommand {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(description, "description");
        if (category.isBlank() || description.isBlank()) {
            throw new IllegalArgumentException("category and description must not be blank");
        }
    }
}
