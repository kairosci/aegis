package it.aegis.application.port;

import it.aegis.domain.identity.UserId;

/**
 * Decisioni di autorizzazione lato server per le operazioni riservate.
 * La verifica deve avvenire lato server prima di ogni effetto esterno o modifica
 * dell'aggregato, senza fidarsi di eventuali restrizioni dell'interfaccia utente.
 */
public interface AuthorizationPolicy {

    /**
     * Verifica che l'utente autenticato possa inviare una segnalazione.
     *
     * @param userId identificativo dell'utente autenticato
     * @return {@code true} se l'invio è consentito, {@code false} altrimenti
     */
    boolean canSubmit(UserId userId);

    /**
     * Verifica che l'utente autenticato possa moderare una segnalazione.
     *
     * @param userId identificativo dell'utente autenticato
     * @return {@code true} se la moderazione è consentita, {@code false} altrimenti
     */
    boolean canModerate(UserId userId);
}
