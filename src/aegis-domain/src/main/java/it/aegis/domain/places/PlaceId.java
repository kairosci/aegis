package it.aegis.domain.places;

import java.util.Objects;
import java.util.UUID;

/** Identificatore di un luogo, fermata o stazione. */
public record PlaceId(UUID value) {

    public PlaceId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
