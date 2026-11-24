package it.aegis.application.port;

import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.places.Place;
import it.aegis.domain.places.PlaceId;
import java.util.List;
import java.util.Optional;

/** Porta di lettura dei luoghi. */
public interface PlaceRepository {

    Optional<Place> findById(PlaceId id);

    List<Place> search(GeoPoint centre, int radiusMetres);
}
