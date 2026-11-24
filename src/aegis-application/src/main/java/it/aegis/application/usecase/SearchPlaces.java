package it.aegis.application.usecase;

import it.aegis.application.port.PlaceRepository;
import it.aegis.domain.places.Place;
import java.util.List;
import java.util.Objects;

/** Caso d'uso di consultazione dei luoghi. */
public final class SearchPlaces {

    private final PlaceRepository places;

    public SearchPlaces(PlaceRepository places) {
        this.places = Objects.requireNonNull(places, "places");
    }

    public List<Place> search(SearchPlacesQuery query) {
        Objects.requireNonNull(query, "query");
        return places.search(query.centre(), query.radiusMetres());
    }
}
