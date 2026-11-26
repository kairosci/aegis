package it.aegis.application.usecase;

import it.aegis.domain.places.PlaceId;
import java.util.Objects;

/**
 * Richiesta di pianificazione tra due luoghi censiti.
 * Entrambi gli identificativi sono obbligatori perché la pianificazione usa
 * la posizione dell'origine e le evidenze della destinazione.
 *
 * @param originId identificativo del luogo di partenza
 * @param destinationId identificativo del luogo di arrivo
 */
public record PlanRouteQuery(PlaceId originId, PlaceId destinationId) {

    /**
     * Verifica che entrambi gli identificativi siano presenti.
     *
     * @throws NullPointerException se uno degli identificativi è {@code null}
     */
    public PlanRouteQuery {
        Objects.requireNonNull(originId, "originId");
        Objects.requireNonNull(destinationId, "destinationId");
    }
}
