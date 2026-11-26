package it.aegis.application.usecase;

import it.aegis.domain.places.GeoPoint;
import java.util.Objects;

/**
 * Interrogazione di ricerca geospaziale con raggio esplicito.
 * Il raggio deve essere positivo: un valore nullo o negativo non descrive
 * un'area di ricerca valida.
 *
 * @param centre punto da cui misurare il raggio
 * @param radiusMetres raggio di ricerca in metri, maggiore di zero
 */
public record SearchPlacesQuery(GeoPoint centre, int radiusMetres) {

    /**
     * Verifica che il centro sia presente e che il raggio sia positivo.
     *
     * @throws NullPointerException se {@code centre} è {@code null}
     * @throws IllegalArgumentException se {@code radiusMetres} non è positivo
     */
    public SearchPlacesQuery {
        Objects.requireNonNull(centre, "centre");
        if (radiusMetres <= 0) {
            throw new IllegalArgumentException("radiusMetres must be positive");
        }
    }
}
