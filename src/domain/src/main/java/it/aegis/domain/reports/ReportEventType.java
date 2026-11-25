package it.aegis.domain.reports;

/** Tipi di evento registrati nello storico append-only. */
public enum ReportEventType {

    /** Segnalazione creata e inserita nello stato iniziale. */
    CREATED,

    /** Segnalazione confermata dopo la verifica. */
    CONFIRMED,

    /** Segnalazione respinta perché non ritenuta valida. */
    REJECTED,

    /** Segnalazione marcata come duplicato di un'altra segnalazione. */
    MARKED_DUPLICATE,

    /** Segnalazione risolta dopo la lavorazione. */
    RESOLVED
}
