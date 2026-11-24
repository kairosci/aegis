package it.aegis.adapter.rest.error;

import java.util.Objects;

/**
 * Eccezione di confine che trasporta solo la classificazione pubblica dell'errore.
 *
 * <p>L'istanza non conserva il messaggio dell'eccezione interna né dati della
 * richiesta. Il mapper può quindi produrre una risposta uniforme senza divulgare
 * dettagli di implementazione o informazioni personali.</p>
 */
public final class ApiException extends RuntimeException {

    /** Identificatore di serializzazione della classe di eccezione. */
    private static final long serialVersionUID = 1L;

    /** Classificazione pubblica associata all'errore. */
    private final ApiErrorType error;

    /**
     * Crea l'eccezione con la sola classificazione pubblica.
     *
     * @param error classificazione pubblica associata all'errore
     * @throws NullPointerException se {@code error} è {@code null}
     */
    private ApiException(ApiErrorType error) {
        super(error.code(), null, false, false);
        this.error = Objects.requireNonNull(error);
    }

    /**
     * Crea un'eccezione di confine per la classificazione indicata.
     *
     * @param error classificazione pubblica associata all'errore
     * @return eccezione da sollevare al confine HTTP
     * @throws NullPointerException se {@code error} è {@code null}
     */
    public static ApiException of(ApiErrorType error) {
        return new ApiException(error);
    }

    /**
     * Restituisce la classificazione pubblica dell'errore.
     *
     * @return tipo di errore associato all'eccezione
     */
    public ApiErrorType error() {
        return error;
    }
}
