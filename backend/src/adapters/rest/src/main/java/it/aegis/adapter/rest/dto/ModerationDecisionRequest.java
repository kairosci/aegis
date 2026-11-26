package it.aegis.adapter.rest.dto;

import it.aegis.adapter.rest.error.ApiErrorType;
import it.aegis.adapter.rest.error.ApiException;

/**
 * Corpo della richiesta di decisione di moderazione, validato al confine HTTP.
 *
 * <p>La decisione e la motivazione sono obbligatorie. Il riferimento al
 * duplicato è facoltativo a questo livello e viene richiesto dalla risorsa
 * quando la decisione lo richiede. I valori restano testo fino alla
 * conversione esplicita nei tipi del dominio.</p>
 *
 * @param decision nome della decisione di moderazione
 * @param reason motivazione della decisione
 * @param duplicateOf riferimento alla segnalazione originale, o {@code null} se non è un duplicato
 */
public record ModerationDecisionRequest(String decision, String reason, String duplicateOf) {

    /**
     * Verifica che decisione e motivazione siano presenti e significative.
     *
     * @throws ApiException se la decisione o la motivazione è assente, vuota o contiene solo spazi
     */
    public ModerationDecisionRequest {
        if (decision == null || decision.isBlank() || reason == null || reason.isBlank()) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
    }
}
