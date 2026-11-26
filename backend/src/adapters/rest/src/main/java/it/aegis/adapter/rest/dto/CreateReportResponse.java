package it.aegis.adapter.rest.dto;

/**
 * Codice di tracking restituito al solo autore della segnalazione.
 *
 * <p>Il codice consente all'autore di seguire lo stato della propria
 * segnalazione senza rendere noto l'identificativo interno del rapporto.</p>
 *
 * @param trackingCode codice di tracking generato per la segnalazione
 */
public record CreateReportResponse(String trackingCode) {
}
