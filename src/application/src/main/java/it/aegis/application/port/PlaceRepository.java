package it.aegis.application.port;

import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.places.Place;
import it.aegis.domain.places.PlaceId;
import java.util.List;
import java.util.Optional;

/**
 * Porta di lettura dei luoghi.
 * I casi d'uso di consultazione dipendono da questo contratto e non
 * dall'implementazione di persistenza scelta dagli adapter.
 */
public interface PlaceRepository {

    /**
     * Cerca un luogo per identificativo.
     *
     * @param id identificativo del luogo
     * @return il luogo trovato, o {@code Optional.empty()} se non esiste
     */
    Optional<Place> findById(PlaceId id);

    /**
     * Cerca i luoghi entro un raggio da un centro geografico.
     *
     * @param centre punto da cui misurare il raggio
     * @param radiusMetres raggio di ricerca in metri
     * @return elenco dei luoghi trovati, eventualmente vuoto
     */
    List<Place> search(GeoPoint centre, int radiusMetres);
}
