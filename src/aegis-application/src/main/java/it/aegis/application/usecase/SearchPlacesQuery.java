package it.aegis.application.usecase;

import it.aegis.domain.places.GeoPoint;
import java.util.Objects;

/** Interrogazione di ricerca geospaziale con raggio esplicito. */
public record SearchPlacesQuery(GeoPoint centre, int radiusMetres) {

    public SearchPlacesQuery {
        Objects.requireNonNull(centre, "centre");
        if (radiusMetres <= 0) {
            throw new IllegalArgumentException("radiusMetres must be positive");
        }
    }
}
