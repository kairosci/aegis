package it.aegis.adapter.rest.dto;

import java.util.List;

/**
 * Risposta esplicita della pianificazione di un percorso.
 *
 * <p>La risposta contiene solo rappresentazioni HTTP dei tratti valutati.
 * Nessun tipo del dominio o del provider di routing viene esposto direttamente
 * al client.</p>
 *
 * @param segments tratti valutati che compongono il percorso
 */
public record RouteResponse(List<RouteSegmentResponse> segments) {

    /**
     * Crea una risposta con elenco immutabile di tratti.
     *
     * @param segments tratti valutati che compongono il percorso
     * @throws NullPointerException se {@code segments} è {@code null}
     */
    public RouteResponse {
        segments = List.copyOf(segments);
    }
}
