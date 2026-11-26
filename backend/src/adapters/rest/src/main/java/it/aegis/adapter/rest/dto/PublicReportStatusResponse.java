package it.aegis.adapter.rest.dto;

/**
 * Stato pubblico della segnalazione, senza dati identificativi dell'autore.
 *
 * <p>La risposta contiene soltanto il codice di tracking e lo stato corrente,
 * informazioni sufficienti per il tracking pubblico.</p>
 *
 * @param trackingCode codice di tracking della segnalazione
 * @param status stato corrente come nome del valore di dominio
 */
public record PublicReportStatusResponse(String trackingCode, String status) {
}
