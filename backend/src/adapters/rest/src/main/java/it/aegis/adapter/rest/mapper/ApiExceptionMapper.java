package it.aegis.adapter.rest.mapper;

import it.aegis.adapter.rest.dto.ErrorResponse;
import it.aegis.adapter.rest.error.ApiErrorType;
import it.aegis.adapter.rest.error.ApiException;
import it.aegis.config.AegisConfig;
import it.aegis.config.MessageSource;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Locale;
import java.util.Objects;

/**
 * Traduce le eccezioni del confine REST nella rappresentazione uniforme dell'errore.
 *
 * <p>Il mapper usa solo la classificazione pubblica dell'errore e il messaggio
 * letto dal bundle configurato. Il messaggio originale dell'eccezione e i dati
 * della richiesta non sono mai inclusi nella risposta.</p>
 */
@Provider
public final class ApiExceptionMapper implements ExceptionMapper<Exception> {

    /** Sorgente dei messaggi localizzati degli errori HTTP. */
    private final MessageSource messages;

    /** Lingua della richiesta correntemente elaborata. */
    @Context
    private Locale locale;

    /**
     * Crea il mapper risolvendo il bundle degli errori dalla configurazione.
     *
     * @param config configurazione applicativa tipizzata
     * @throws NullPointerException se {@code config} è {@code null}
     * @throws IllegalArgumentException se il percorso del bundle REST è vuoto
     */
    @Inject
    public ApiExceptionMapper(AegisConfig config) {
        Objects.requireNonNull(config, "config");
        this.messages = MessageSource.at(config, config.restMessageBundleBasePath());
    }

    /**
     * Crea la risposta HTTP uniforme per l'eccezione ricevuta.
     *
     * <p>La lingua arriva dal contesto JAX-RS. Se il contesto non è disponibile,
     * viene usata la lingua predefinita della piattaforma, senza modificare
     * il contenuto del codice di errore.</p>
     *
     * @param exception eccezione da tradurre
     * @return risposta con stato, codice e messaggio localizzato
     */
    @Override
    public Response toResponse(Exception exception) {
        ApiErrorType error = classify(exception);
        return Response.status(error.status())
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(error.code(), messages.get(error.messageKey(), responseLocale())))
                .build();
    }

    /**
     * Classifica l'eccezione senza esporne il messaggio interno.
     *
     * @param exception eccezione da classificare
     * @return tipo di errore pubblico associato all'eccezione
     */
    private ApiErrorType classify(Exception exception) {
        if (exception instanceof ApiException apiException) {
            return apiException.error();
        }
        if (exception instanceof WebApplicationException webException) {
            return fromStatus(webException.getResponse().getStatusInfo());
        }
        if (exception instanceof SecurityException) {
            return ApiErrorType.FORBIDDEN;
        }
        if (exception instanceof IllegalArgumentException) {
            return ApiErrorType.BAD_REQUEST;
        }
        if (exception instanceof IllegalStateException) {
            return ApiErrorType.CONFLICT;
        }
        return ApiErrorType.INTERNAL;
    }

    /**
     * Associa lo stato di un'eccezione JAX-RS a un tipo di errore pubblico.
     *
     * @param status stato della risposta JAX-RS
     * @return tipo di errore pubblico corrispondente allo stato noto
     */
    private ApiErrorType fromStatus(StatusType status) {
        if (status.getStatusCode() == Response.Status.BAD_REQUEST.getStatusCode()) {
            return ApiErrorType.BAD_REQUEST;
        }
        if (status.getStatusCode() == Response.Status.UNAUTHORIZED.getStatusCode()) {
            return ApiErrorType.UNAUTHORIZED;
        }
        if (status.getStatusCode() == Response.Status.FORBIDDEN.getStatusCode()) {
            return ApiErrorType.FORBIDDEN;
        }
        if (status.getStatusCode() == Response.Status.NOT_FOUND.getStatusCode()) {
            return ApiErrorType.NOT_FOUND;
        }
        if (status.getStatusCode() == Response.Status.CONFLICT.getStatusCode()) {
            return ApiErrorType.CONFLICT;
        }
        return ApiErrorType.INTERNAL;
    }

    /**
     * Restituisce la lingua da usare per la risoluzione del messaggio.
     *
     * @return lingua iniettata nel contesto JAX-RS, o lingua predefinita della piattaforma
     */
    private Locale responseLocale() {
        return locale == null ? Locale.getDefault() : locale;
    }
}
