package it.aegis.adapter.rest.dto;

import it.aegis.adapter.rest.error.ApiErrorType;
import it.aegis.adapter.rest.error.ApiException;

/**
 * Corpo della richiesta di creazione segnalazione, validato al confine HTTP.
 *
 * <p>I campi restano testi grezzi: la conversione nei tipi del dominio è
 * responsabilità del caso d'uso. Il compattatore del record respinge i valori
 * assenti o vuoti prima che raggiungano la logica applicativa, senza inserire
 * testi di errore nel codice.</p>
 *
 * @param key chiave di idempotenza fornita dal client
 * @param category categoria dichiarata della segnalazione
 * @param description descrizione testuale della segnalazione
 */
public record CreateReportRequest(String key, String category, String description) {

    /**
     * Verifica che tutti i campi della richiesta siano presenti e significativi.
     *
     * @throws ApiException se un campo è assente, vuoto o contiene solo spazi
     */
    public CreateReportRequest {
        if (key == null || category == null || description == null
                || key.isBlank() || category.isBlank() || description.isBlank()) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
    }
}
