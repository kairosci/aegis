package it.aegis.adapter.rest.error;

/**
 * Codici e chiavi del contratto uniforme degli errori HTTP.
 *
 * <p>I codici sono identificatori stabili del contratto API. Le chiavi indicano
 * il testo localizzato nel bundle configurato. Nessuna costante contiene un
 * messaggio destinato all'utente.</p>
 */
public final class ApiErrorContract {

    /** Codice della richiesta non valida. */
    public static final String BAD_REQUEST_CODE = "bad_request";

    /** Chiave del messaggio della richiesta non valida. */
    public static final String BAD_REQUEST_KEY = "error.bad_request";

    /** Codice della richiesta che richiede autenticazione. */
    public static final String UNAUTHORIZED_CODE = "unauthorized";

    /** Chiave del messaggio della richiesta che richiede autenticazione. */
    public static final String UNAUTHORIZED_KEY = "error.unauthorized";

    /** Codice dell'operazione non autorizzata. */
    public static final String FORBIDDEN_CODE = "forbidden";

    /** Chiave del messaggio dell'operazione non autorizzata. */
    public static final String FORBIDDEN_KEY = "error.forbidden";

    /** Codice della risorsa non trovata. */
    public static final String NOT_FOUND_CODE = "not_found";

    /** Chiave del messaggio della risorsa non trovata. */
    public static final String NOT_FOUND_KEY = "error.not_found";

    /** Codice del conflitto con lo stato corrente. */
    public static final String CONFLICT_CODE = "conflict";

    /** Chiave del messaggio del conflitto con lo stato corrente. */
    public static final String CONFLICT_KEY = "error.conflict";

    /** Codice dell'errore interno del servizio. */
    public static final String INTERNAL_CODE = "internal_error";

    /** Chiave del messaggio dell'errore interno del servizio. */
    public static final String INTERNAL_KEY = "error.internal";

    /** Impedisce la creazione di istanze di una classe che espone solo costanti. */
    private ApiErrorContract() {
    }
}
