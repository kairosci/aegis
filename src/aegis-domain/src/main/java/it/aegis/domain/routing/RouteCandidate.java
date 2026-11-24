package it.aegis.domain.routing;

import it.aegis.domain.places.GeoPoint;
import java.util.List;
import java.util.Objects;

/** Risposta grezza del provider prima della valutazione delle evidenze. */
public record RouteCandidate(
        GeoPoint from,
        GeoPoint to,
        List<String> barrierCodes,
        boolean providerVerified) {

    public RouteCandidate {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        barrierCodes = List.copyOf(Objects.requireNonNull(barrierCodes, "barrierCodes"));
    }
}
