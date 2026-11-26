package it.aegis.adapter.rest.error;

import jakarta.ws.rs.core.Response;

/**
 * Classificazione pubblica degli errori HTTP del confine REST.
 *
 * <p>Ogni valore associa uno stato HTTP, un codice stabile del contratto e la
 * chiave del messaggio localizzato. Le eccezioni interne restano fuori da
 * questa classificazione e non vengono mai restituite al client.</p>
 */
public enum ApiErrorType {

    /** Richiesta non valida per forma o contenuto dei parametri. */
    BAD_REQUEST(Response.Status.BAD_REQUEST, ApiErrorContract.BAD_REQUEST_CODE, ApiErrorContract.BAD_REQUEST_KEY),

    /** Richiesta che richiede un'identità autenticata. */
    UNAUTHORIZED(Response.Status.UNAUTHORIZED, ApiErrorContract.UNAUTHORIZED_CODE, ApiErrorContract.UNAUTHORIZED_KEY),

    /** Identità autenticata senza il permesso richiesto. */
    FORBIDDEN(Response.Status.FORBIDDEN, ApiErrorContract.FORBIDDEN_CODE, ApiErrorContract.FORBIDDEN_KEY),

    /** Risorsa non disponibile o non accessibile con i parametri forniti. */
    NOT_FOUND(Response.Status.NOT_FOUND, ApiErrorContract.NOT_FOUND_CODE, ApiErrorContract.NOT_FOUND_KEY),

    /** Operazione in conflitto con lo stato corrente della risorsa. */
    CONFLICT(Response.Status.CONFLICT, ApiErrorContract.CONFLICT_CODE, ApiErrorContract.CONFLICT_KEY),

    /** Errore interno senza dettagli esposti al client. */
    INTERNAL(Response.Status.INTERNAL_SERVER_ERROR, ApiErrorContract.INTERNAL_CODE, ApiErrorContract.INTERNAL_KEY);

    /** Stato HTTP associato all'errore. */
    private final Response.Status status;

    /** Codice stabile del contratto API. */
    private final String code;

    /** Chiave del messaggio localizzato nel bundle configurato. */
    private final String messageKey;

    /**
     * Crea un tipo di errore con stato, codice e chiave del messaggio.
     *
     * @param status stato HTTP associato all'errore
     * @param code codice stabile del contratto API
     * @param messageKey chiave del messaggio localizzato nel bundle configurato
     */
    private ApiErrorType(Response.Status status, String code, String messageKey) {
        this.status = status;
        this.code = code;
        this.messageKey = messageKey;
    }

    /**
     * Restituisce lo stato HTTP associato all'errore.
     *
     * @return stato HTTP da usare nella risposta
     */
    public Response.Status status() {
        return status;
    }

    /**
     * Restituisce il codice stabile del contratto API.
     *
     * @return codice da includere nella risposta di errore
     */
    public String code() {
        return code;
    }

    /**
     * Restituisce la chiave del messaggio localizzato.
     *
     * @return chiave da risolvere nella sorgente messaggi configurata
     */
    public String messageKey() {
        return messageKey;
    }
}
