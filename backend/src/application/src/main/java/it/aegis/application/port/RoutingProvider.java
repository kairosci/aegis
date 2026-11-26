package it.aegis.application.port;

import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.routing.RouteCandidate;
import java.util.List;

/**
 * Porta sostituibile verso il provider di routing.
 * Il caso d'uso valuta i candidati restituiti senza dipendere dal provider
 * che li ha calcolati.
 */
public interface RoutingProvider {

    /**
     * Calcola i percorsi candidati tra due posizioni geografiche.
     *
     * @param origin posizione di partenza
     * @param destination posizione di arrivo
     * @return elenco dei percorsi candidati forniti dal provider
     */
    List<RouteCandidate> calculate(GeoPoint origin, GeoPoint destination);
}
