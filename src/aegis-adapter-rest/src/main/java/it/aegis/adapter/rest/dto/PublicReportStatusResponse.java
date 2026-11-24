package it.aegis.adapter.rest.dto;

/** Stato pubblico della segnalazione, senza dati identificativi dell'autore. */
public record PublicReportStatusResponse(String trackingCode, String status) {
}
