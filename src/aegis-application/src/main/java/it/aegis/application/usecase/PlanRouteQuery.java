package it.aegis.application.usecase;

import it.aegis.domain.places.PlaceId;
import java.util.Objects;

/** Richiesta di pianificazione tra due luoghi censiti. */
public record PlanRouteQuery(PlaceId originId, PlaceId destinationId) {

    public PlanRouteQuery {
        Objects.requireNonNull(originId, "originId");
        Objects.requireNonNull(destinationId, "destinationId");
    }
}
