package it.aegis.domain.reports;

/** Esito della verifica di un allegato prima della pubblicazione. */
public enum ScanStatus {

    /** La scansione non è ancora stata eseguita. */
    PENDING,

    /** La scansione è terminata senza rilevare contenuti pericolosi. */
    CLEAN,

    /** La scansione ha dato esito negativo per la presenza di contenuti pericolosi. */
    REJECTED
}
