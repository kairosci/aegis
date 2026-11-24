package it.aegis.domain.routing;

import java.util.List;
import java.util.Objects;

/** Percorso con stati derivati dai tratti, senza campi di stato duplicati. */
public final class RouteResult {

    private final List<RouteSegment> segments;

    public RouteResult(List<RouteSegment> segments) {
        this.segments = List.copyOf(Objects.requireNonNull(segments, "segments"));
    }

    public List<RouteSegment> segments() {
        return segments;
    }

    public boolean isVerified() {
        return segments.stream().allMatch(segment ->
                segment.compatibility() != RouteCompatibility.UNVERIFIED);
    }

    public boolean hasCompatibleOption() {
        return segments.stream().anyMatch(segment ->
                segment.compatibility() == RouteCompatibility.COMPATIBLE);
    }
}
