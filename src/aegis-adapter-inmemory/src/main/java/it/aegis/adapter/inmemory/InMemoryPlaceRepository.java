package it.aegis.adapter.inmemory;

import it.aegis.application.port.PlaceRepository;
import it.aegis.config.AegisConfig;
import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.places.Place;
import it.aegis.domain.places.PlaceId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Adattatore in memoria per sviluppo locale. */
public final class InMemoryPlaceRepository implements PlaceRepository {

    private final AegisConfig config;
    private final Map<PlaceId, Place> places = new ConcurrentHashMap<>();

    public InMemoryPlaceRepository(AegisConfig config) {
        this.config = Objects.requireNonNull(config, "config");
    }

    public void add(Place place) {
        places.put(place.id(), place);
    }

    @Override
    public Optional<Place> findById(PlaceId id) {
        return Optional.ofNullable(places.get(id));
    }

    @Override
    public List<Place> search(GeoPoint centre, int radiusMetres) {
        if (radiusMetres <= 0) {
            throw new IllegalArgumentException("radiusMetres must be positive");
        }
        List<Place> matches = new ArrayList<>();
        for (Place place : places.values()) {
            if (distanceMetres(centre, place.position()) <= radiusMetres) {
                matches.add(place);
            }
        }
        return List.copyOf(matches);
    }

    private double distanceMetres(GeoPoint from, GeoPoint to) {
        double latitudeFrom = Math.toRadians(from.latitude());
        double latitudeTo = Math.toRadians(to.latitude());
        double deltaLatitude = latitudeTo - latitudeFrom;
        double deltaLongitude = Math.toRadians(to.longitude() - from.longitude());
        double haversine = Math.pow(Math.sin(deltaLatitude / 2.0), 2.0)
                + Math.cos(latitudeFrom) * Math.cos(latitudeTo)
                * Math.pow(Math.sin(deltaLongitude / 2.0), 2.0);
        return 2.0 * config.earthRadiusMetres() * Math.asin(Math.sqrt(haversine));
    }
}
