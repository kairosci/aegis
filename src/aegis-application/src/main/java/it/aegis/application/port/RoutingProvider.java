package it.aegis.application.port;

import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.routing.RouteCandidate;
import java.util.List;

/** Porta sostituibile verso il provider di routing. */
public interface RoutingProvider {

    List<RouteCandidate> calculate(GeoPoint origin, GeoPoint destination);
}
