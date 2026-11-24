package it.aegis.application.usecase;

import it.aegis.application.port.PlaceRepository;
import it.aegis.domain.places.Place;
import it.aegis.domain.places.PlaceId;
import java.util.Objects;

/** Caso d'uso di consultazione del dettaglio di un luogo. */
public final class FindPlace {

    private final PlaceRepository places;

    public FindPlace(PlaceRepository places) {
        this.places = Objects.requireNonNull(places, "places");
    }

    public Place find(PlaceId id) {
        Objects.requireNonNull(id, "id");
        return places.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("place not found"));
    }
}
