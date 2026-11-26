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

/**
 * Adattatore in memoria per sviluppo locale.
 *
 * <p>I luoghi sono mantenuti in una mappa concorrente e la ricerca per distanza
 * usa il raggio terrestre definito nella configurazione.
 */
public final class InMemoryPlaceRepository implements PlaceRepository {

    /** Configurazione che fornisce il raggio terrestre usato nel calcolo delle distanze. */
    private final AegisConfig config;

    /** Luoghi indicizzati dal loro identificativo. */
    private final Map<PlaceId, Place> places = new ConcurrentHashMap<>();

    /**
     * Crea il repository con la configurazione da cui leggere il raggio terrestre.
     *
     * @param config configurazione applicativa tipizzata
     * @throws NullPointerException se {@code config} è {@code null}
     */
    public InMemoryPlaceRepository(AegisConfig config) {
        this.config = Objects.requireNonNull(config, "config");
    }

    /**
     * Aggiunge o sostituisce un luogo nella mappa locale.
     *
     * @param place luogo da rendere disponibile alla ricerca
     */
    public void add(Place place) {
        places.put(place.id(), place);
    }

    /**
     * Cerca un luogo dal suo identificativo.
     *
     * @param id identificativo da cercare
     * @return luogo trovato, o vuoto se l'identificativo non è noto
     */
    @Override
    public Optional<Place> findById(PlaceId id) {
        return Optional.ofNullable(places.get(id));
    }

    /**
     * Cerca i luoghi entro il raggio indicato dal punto dato.
     *
     * @param centre punto da cui misurare le distanze
     * @param radiusMetres raggio massimo in metri, maggiore di zero
     * @return copia non modificabile dei luoghi la cui distanza non supera il raggio
     * @throws IllegalArgumentException se {@code radiusMetres} non è positivo
     */
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

    /**
     * Calcola la distanza tra due punti con la formula dell'emisenoverso.
     *
     * @param from punto di partenza
     * @param to punto di arrivo
     * @return distanza in metri, calcolata con il raggio configurato
     */
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
