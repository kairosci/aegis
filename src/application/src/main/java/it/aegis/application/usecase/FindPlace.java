package it.aegis.application.usecase;

import it.aegis.application.port.PlaceRepository;
import it.aegis.domain.places.Place;
import it.aegis.domain.places.PlaceId;
import java.util.Objects;

/**
 * Caso d'uso di consultazione del dettaglio di un luogo.
 * Recupera il luogo tramite la porta di lettura e segnala come errore un
 * identificativo sconosciuto, anziché restituire un risultato vuoto.
 */
public final class FindPlace {

    /** Porta di lettura dei luoghi. */
    private final PlaceRepository places;

    /**
     * Crea il caso d'uso con la porta di lettura da utilizzare.
     *
     * @param places porta di lettura dei luoghi
     * @throws NullPointerException se {@code places} è {@code null}
     */
    public FindPlace(PlaceRepository places) {
        this.places = Objects.requireNonNull(places, "places");
    }

    /**
     * Restituisce il dettaglio del luogo richiesto.
     *
     * @param id identificativo del luogo
     * @return luogo corrispondente all'identificativo
     * @throws NullPointerException se {@code id} è {@code null}
     * @throws IllegalArgumentException se non esiste alcun luogo con quell'identificativo
     */
    public Place find(PlaceId id) {
        Objects.requireNonNull(id, "id");
        return places.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("place not found"));
    }
}
