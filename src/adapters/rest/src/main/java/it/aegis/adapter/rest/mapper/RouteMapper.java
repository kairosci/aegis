package it.aegis.adapter.rest.mapper;

import it.aegis.adapter.rest.dto.RouteResponse;
import it.aegis.adapter.rest.dto.RouteSegmentResponse;
import it.aegis.domain.routing.RouteResult;
import it.aegis.domain.routing.RouteSegment;
import java.util.List;

/**
 * Conversione esplicita dal risultato del routing alla risposta HTTP.
 *
 * <p>Il mapper si limita a proiettare stati e coordinate nei DTO del contratto
 * REST. Non introduce regole di compatibilità e non modifica il risultato del
 * caso d'uso.</p>
 */
public final class RouteMapper {

    /** Impedisce la creazione di istanze di una classe di sola utilità. */
    private RouteMapper() {
    }

    /**
     * Converte il risultato valutato nella risposta HTTP.
     *
     * @param result risultato del caso d'uso da convertire
     * @return rappresentazione HTTP dei tratti valutati
     */
    public static RouteResponse toResponse(RouteResult result) {
        List<RouteSegmentResponse> segments = result.segments().stream()
                .map(RouteMapper::toSegmentResponse)
                .toList();
        return new RouteResponse(segments);
    }

    /**
     * Converte un singolo tratto nella rappresentazione HTTP.
     *
     * @param segment tratto del dominio da convertire
     * @return rappresentazione HTTP del tratto
     */
    private static RouteSegmentResponse toSegmentResponse(RouteSegment segment) {
        return new RouteSegmentResponse(
                segment.from().latitude(),
                segment.from().longitude(),
                segment.to().latitude(),
                segment.to().longitude(),
                segment.compatibility().name(),
                segment.barrierCodes());
    }
}
