package it.aegis.adapter.rest.resource;

import it.aegis.adapter.rest.dto.PlaceResponse;
import it.aegis.adapter.rest.mapper.PlaceMapper;
import it.aegis.application.usecase.FindPlace;
import it.aegis.application.usecase.SearchPlaces;
import it.aegis.application.usecase.SearchPlacesQuery;
import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.places.Place;
import it.aegis.domain.places.PlaceId;
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
@Path("/places")
@Produces(MediaType.APPLICATION_JSON)
public final class PlaceResource {

    /** Caso d'uso per il dettaglio di un singolo luogo. */
    private final FindPlace findPlace;

    /** Caso d'uso per la ricerca dei luoghi in un'area. */
    private final SearchPlaces searchPlaces;

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
     * <p>Un identificativo non valido o un luogo assente produce una risposta
     * non trovata. Il luogo trovato viene convertito esplicitamente in DTO.</p>
     *
     * @param id identificativo del luogo nel percorso della richiesta
     * @return risposta con il luogo richiesto, oppure non trovata
     */
    @GET
    @Path("{id}")
    public Response find(@PathParam("id") String id) {
        try {
            Place place = findPlace.find(new PlaceId(UUID.fromString(id)));
            return Response.ok(PlaceMapper.toResponse(place)).build();
        } catch (IllegalArgumentException cause) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Cerca i luoghi entro il raggio indicato intorno al centro geografico.
     *
     * <p>Coordinate o raggio non validi producono una risposta bad request.
     * I luoghi trovati vengono convertiti esplicitamente in DTO.</p>
     *
     * @param latitude latitudine del centro della ricerca, in gradi
     * @param longitude longitudine del centro della ricerca, in gradi
     * @param radiusMetres raggio massimo della ricerca, in metri
     * @return risposta con l'elenco dei luoghi trovati, oppure bad request
     */
    @GET
    public Response search(
            @QueryParam("lat") double latitude,
            @QueryParam("lon") double longitude,
            @QueryParam("radius") int radiusMetres) {
        try {
            GeoPoint centre = new GeoPoint(latitude, longitude);
            List<PlaceResponse> results = searchPlaces
                    .search(new SearchPlacesQuery(centre, radiusMetres))
                    .stream()
                    .map(PlaceMapper::toResponse)
                    .toList();
            return Response.ok(results).build();
        } catch (IllegalArgumentException cause) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
