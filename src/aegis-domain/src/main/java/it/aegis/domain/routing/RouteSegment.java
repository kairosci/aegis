package it.aegis.domain.routing;

import it.aegis.domain.places.GeoPoint;
import java.util.List;
import java.util.Objects;

/** Tratto di percorso con esito di compatibilità derivato dalle evidenze. */
public record RouteSegment(
        GeoPoint from,
        GeoPoint to,
        RouteCompatibility compatibility,
        List<String> barrierCodes) {

    public RouteSegment {
        Objects.requireNonNull(from, "from");
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(compatibility, "compatibility");
        barrierCodes = List.copyOf(Objects.requireNonNull(barrierCodes, "barrierCodes"));
    }
}
