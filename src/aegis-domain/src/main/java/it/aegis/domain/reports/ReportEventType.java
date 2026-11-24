package it.aegis.domain.reports;

/** Tipi di evento registrati nello storico append-only. */
public enum ReportEventType {
    CREATED,
    CONFIRMED,
    REJECTED,
    MARKED_DUPLICATE,
    RESOLVED
}
