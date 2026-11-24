package it.aegis.domain.identity;

import java.util.Objects;
import java.util.UUID;

/** Identificatore di un account utente. */
public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
