package it.aegis.application.port;

import it.aegis.domain.identity.UserId;

/** Decisioni di autorizzazione lato server per le operazioni riservate. */
public interface AuthorizationPolicy {

    boolean canSubmit(UserId userId);

    boolean canModerate(UserId userId);
}
