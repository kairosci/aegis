package it.aegis.adapter.rest.resource;

import it.aegis.adapter.rest.RestContract;
import it.aegis.adapter.rest.dto.PlaceResponse;
import it.aegis.adapter.rest.error.ApiErrorType;
import it.aegis.adapter.rest.error.ApiException;
import it.aegis.adapter.rest.mapper.PlaceMapper;
import it.aegis.application.usecase.FindPlace;
import it.aegis.application.usecase.SearchPlaces;
import it.aegis.application.usecase.SearchPlacesQuery;
import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.places.Place;
import it.aegis.domain.places.PlaceId;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

/** Risorsa HTTP di sola lettura dei luoghi. */
@Path(RestContract.PLACES_PATH)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class PlaceResource {

    /** Nome del parametro di query della latitudine del centro di ricerca. */
    private static final String LATITUDE_PARAM = "lat";

    /** Nome del parametro di query della longitudine del centro di ricerca. */
    private static final String LONGITUDE_PARAM = "lon";

    /** Nome del parametro di query del raggio di ricerca. */
    private static final String RADIUS_PARAM = "radius";

    /** Caso d'uso per il dettaglio di un singolo luogo. */
    private final FindPlace findPlace;

    /** Caso d'uso per la ricerca dei luoghi in un'area. */
    private final SearchPlaces searchPlaces;

    /**
     * Crea l'istanza usata dal proxy CDI, senza dipendenze applicative.
     *
     * <p>Il costruttore annotato fornisce i casi d'uso quando il container
     * crea la risorsa per servire una richiesta.</p>
     */
    public PlaceResource() {
        this.findPlace = null;
        this.searchPlaces = null;
    }

    /**
     * Crea la risorsa con i casi d'uso necessari.
     *
     * @param findPlace caso d'uso per il dettaglio di un singolo luogo
     * @param searchPlaces caso d'uso per la ricerca dei luoghi in un'area
     */
    @Inject
    public PlaceResource(FindPlace findPlace, SearchPlaces searchPlaces) {
        this.findPlace = findPlace;
        this.searchPlaces = searchPlaces;
    }

    /**
     * Restituisce il dettaglio del luogo indicato nel percorso.
     *
     * <p>Un identificativo non valido o un luogo assente produce un errore di
     * confine classificato come risorsa non trovata. Il luogo trovato viene
     * convertito esplicitamente in DTO.</p>
     *
     * @param id identificativo del luogo nel percorso della richiesta
     * @return risposta con il luogo richiesto
     * @throws ApiException se l'identificativo non è valido o il luogo non esiste
     */
    @GET
    @Path(RestContract.ID_PATH)
    public Response find(@PathParam(RestContract.ID_PARAM) String id) {
        try {
            Place place = findPlace.find(new PlaceId(UUID.fromString(id)));
            return Response.ok(PlaceMapper.toResponse(place)).build();
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.NOT_FOUND);
        }
    }

    /**
     * Cerca i luoghi entro il raggio indicato intorno al centro geografico.
     *
     * <p>Coordinate o raggio non validi producono un errore di confine
     * classificato come richiesta non valida. I luoghi trovati vengono
     * convertiti esplicitamente in DTO.</p>
     *
     * @param latitude latitudine del centro della ricerca, in gradi
     * @param longitude longitudine del centro della ricerca, in gradi
     * @param radiusMetres raggio massimo della ricerca, in metri
     * @return risposta con l'elenco dei luoghi trovati
     * @throws ApiException se le coordinate o il raggio non sono validi
     */
    @GET
    public Response search(
            @QueryParam(LATITUDE_PARAM) double latitude,
            @QueryParam(LONGITUDE_PARAM) double longitude,
            @QueryParam(RADIUS_PARAM) int radiusMetres) {
        try {
            GeoPoint centre = new GeoPoint(latitude, longitude);
            List<PlaceResponse> results = searchPlaces
                    .search(new SearchPlacesQuery(centre, radiusMetres))
                    .stream()
                    .map(PlaceMapper::toResponse)
                    .toList();
            return Response.ok(results).build();
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
    }
}
