package it.aegis.application.usecase;

import it.aegis.application.port.PlaceRepository;
import it.aegis.application.port.RoutingProvider;
import it.aegis.domain.places.Place;
import it.aegis.domain.routing.RouteAssessor;
import it.aegis.domain.routing.RouteCandidate;
import it.aegis.domain.routing.RouteResult;
import it.aegis.domain.routing.RouteSegment;
import java.util.List;
import java.util.Objects;

/** Caso d'uso UC-01. Il risultato del provider è valutato con le evidenze del luogo. */
public final class PlanRoute {

    private final PlaceRepository places;
    private final RoutingProvider routing;
    private final RouteAssessor assessor = new RouteAssessor();

    public PlanRoute(PlaceRepository places, RoutingProvider routing) {
        this.places = Objects.requireNonNull(places, "places");
        this.routing = Objects.requireNonNull(routing, "routing");
    }

    public RouteResult plan(PlanRouteQuery query) {
        Objects.requireNonNull(query, "query");
        Place origin = places.findById(query.originId())
                .orElseThrow(() -> new IllegalArgumentException("origin place not found"));
        Place destination = places.findById(query.destinationId())
                .orElseThrow(() -> new IllegalArgumentException("destination place not found"));

        List<RouteCandidate> candidates =
                routing.calculate(origin.position(), destination.position());
        List<RouteSegment> segments = candidates.stream()
                .map(candidate -> assessor.assess(candidate, destination.features()))
                .toList();
        return new RouteResult(segments);
    }
}
