package it.aegis.domain.places;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificatore di un luogo, fermata o stazione.
 *
 * @param value valore UUID che identifica il luogo
 */
public record PlaceId(UUID value) {

    /**
     * Verifica che il valore dell'identificatore sia presente.
     *
     * @param value valore UUID che identifica il luogo
     * @throws NullPointerException se {@code value} è null
     */
    public PlaceId {
        Objects.requireNonNull(value, "value");
    }

    /**
     * Restituisce la rappresentazione testuale del valore UUID.
     *
     * @return il valore dell'identificatore sotto forma di stringa
     */
    @Override
    public String toString() {
        return value.toString();
    }
}
