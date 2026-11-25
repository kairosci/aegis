package it.aegis.adapter.rest.resource;

import it.aegis.adapter.rest.RestContract;
import it.aegis.adapter.rest.error.ApiErrorType;
import it.aegis.adapter.rest.error.ApiException;
import it.aegis.adapter.rest.mapper.RouteMapper;
import it.aegis.application.usecase.PlanRoute;
import it.aegis.application.usecase.PlanRouteQuery;
import it.aegis.domain.places.PlaceId;
import it.aegis.domain.routing.RouteResult;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.UUID;

/**
 * Risorsa HTTP di pianificazione dei percorsi.
 *
 * <p>La risorsa riceve gli identificativi dei luoghi come parametri di query,
 * li converte in tipi del dominio e restituisce un DTO esplicito con i tratti
 * valutati dal caso d'uso.</p>
 */
@Path(RestContract.ROUTES_PATH)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class RouteResource {

    /** Caso d'uso che pianifica e valuta il percorso. */
    private final PlanRoute planRoute;

    /**
     * Crea l'istanza usata dal proxy CDI, senza dipendenze applicative.
     *
     * <p>Il costruttore annotato fornisce il caso d'uso quando il container
     * crea la risorsa per servire una richiesta.</p>
     */
    public RouteResource() {
        this.planRoute = null;
    }

    /**
     * Crea la risorsa con il caso d'uso necessario.
     *
     * @param planRoute caso d'uso che pianifica e valuta il percorso
     */
    @Inject
    public RouteResource(PlanRoute planRoute) {
        this.planRoute = planRoute;
    }

    /**
     * Pianifica un percorso tra i luoghi indicati nei parametri di query.
     *
     * <p>Identificativi assenti o non validi producono un errore di richiesta.
     * Luoghi non censiti producono un errore di risorsa non trovata. Il
     * risultato viene convertito esplicitamente in un DTO prima della risposta.</p>
     *
     * @param origin identificativo del luogo di partenza
     * @param destination identificativo del luogo di arrivo
     * @return risposta con il percorso valutato
     * @throws ApiException se i parametri non sono validi o i luoghi non esistono
     */
    @GET
    public Response plan(
            @QueryParam(RestContract.ORIGIN_PARAM) String origin,
            @QueryParam(RestContract.DESTINATION_PARAM) String destination) {
        PlanRouteQuery query = new PlanRouteQuery(parsePlaceId(origin), parsePlaceId(destination));
        try {
            RouteResult result = planRoute.plan(query);
            return Response.ok(RouteMapper.toResponse(result)).build();
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.NOT_FOUND);
        }
    }

    /**
     * Converte un identificativo di luogo ricevuto nella query.
     *
     * @param value rappresentazione testuale dell'identificativo
     * @return identificativo del dominio
     * @throws ApiException se il valore è assente o non è un identificativo valido
     */
    private PlaceId parsePlaceId(String value) {
        if (value == null || value.isBlank()) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
        try {
            return new PlaceId(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
    }
}
