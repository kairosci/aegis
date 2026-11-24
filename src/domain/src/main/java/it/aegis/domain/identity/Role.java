package it.aegis.domain.identity;

/** Ruoli applicativi previsti dal RAD. */
public enum Role {

    /** Utente registrato, abilitato alle operazioni riservate agli utenti autenticati. */
    REGISTERED_USER,

    /** Amministratore, abilitato alle operazioni di moderazione e configurazione. */
    ADMINISTRATOR
}
