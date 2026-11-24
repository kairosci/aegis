package it.aegis.adapter.rest.dto;

import java.util.Objects;

/**
 * Corpo della richiesta di creazione segnalazione, validato al confine HTTP.
 *
 * <p>I campi restano testi grezzi: la conversione nei tipi del dominio è
 * responsabilità del caso d'uso. Il compattatore del record respinge i valori
 * assenti o vuoti prima che raggiungano la logica applicativa.</p>
 *
 * @param key chiave di idempotenza fornita dal client
 * @param category categoria dichiarata della segnalazione
 * @param description descrizione testuale della segnalazione
 */
public record CreateReportRequest(String key, String category, String description) {

    /**
     * Verifica che tutti i campi della richiesta siano presenti e significativi.
     *
     * @throws NullPointerException se {@code key}, {@code category} o {@code description} sono null
     * @throws IllegalArgumentException se uno dei campi è vuoto o contiene solo spazi
     */
    public CreateReportRequest {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(description, "description");
        if (key.isBlank() || category.isBlank() || description.isBlank()) {
            throw new IllegalArgumentException("request fields must not be blank");
        }
    }
}
