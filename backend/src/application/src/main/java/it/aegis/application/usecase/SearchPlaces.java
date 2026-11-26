package it.aegis.application.usecase;

import it.aegis.application.port.PlaceRepository;
import it.aegis.domain.places.Place;
import java.util.List;
import java.util.Objects;

/**
 * Caso d'uso di consultazione dei luoghi.
 * Trasmette alla porta di lettura i parametri già validati dalla query.
 */
public final class SearchPlaces {

    /** Porta di lettura dei luoghi. */
    private final PlaceRepository places;

    /**
     * Crea il caso d'uso con la porta di lettura da utilizzare.
     *
     * @param places porta di lettura dei luoghi
     * @throws NullPointerException se {@code places} è {@code null}
     */
    public SearchPlaces(PlaceRepository places) {
        this.places = Objects.requireNonNull(places, "places");
    }

    /**
     * Cerca i luoghi entro il raggio richiesto.
     *
     * @param query centro e raggio della ricerca geospaziale
     * @return elenco dei luoghi trovati, eventualmente vuoto
     * @throws NullPointerException se {@code query} è {@code null}
     */
    public List<Place> search(SearchPlacesQuery query) {
        Objects.requireNonNull(query, "query");
        return places.search(query.centre(), query.radiusMetres());
    }
}
