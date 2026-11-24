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

/**
 * Caso d'uso UC-01. Il risultato del provider è valutato con le evidenze del luogo.
 * Origine e destinazione devono essere luoghi censiti: le loro posizioni alimentano
 * il provider e le evidenze della destinazione determinano la compatibilità.
 */
public final class PlanRoute {

    /** Porta di lettura dei luoghi censiti. */
    private final PlaceRepository places;
    /** Provider sostituibile che calcola i percorsi candidati. */
    private final RoutingProvider routing;
    /** Valutatore delle evidenze di accessibilità della destinazione. */
    private final RouteAssessor assessor = new RouteAssessor();

    /**
     * Crea il caso d'uso con la porta di lettura e il provider da utilizzare.
     *
     * @param places porta di lettura dei luoghi
     * @param routing provider di routing
     * @throws NullPointerException se una delle dipendenze è {@code null}
     */
    public PlanRoute(PlaceRepository places, RoutingProvider routing) {
        this.places = Objects.requireNonNull(places, "places");
        this.routing = Objects.requireNonNull(routing, "routing");
    }

    /**
     * Pianifica i percorsi tra due luoghi e ne valuta la compatibilità.
     *
     * @param query identificativi del luogo di partenza e di arrivo
     * @return risultato con un segmento valutato per ogni candidato ricevuto
     * @throws NullPointerException se {@code query} è {@code null}
     * @throws IllegalArgumentException se il luogo di origine o di destinazione non esiste
     */
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
