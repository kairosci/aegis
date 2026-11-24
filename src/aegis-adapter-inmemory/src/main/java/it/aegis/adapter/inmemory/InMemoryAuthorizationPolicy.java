package it.aegis.adapter.inmemory;

import it.aegis.application.port.AuthorizationPolicy;
import it.aegis.domain.identity.UserId;
import java.util.Set;

/**
 * Autorizzazione in memoria basata su insiemi espliciti. Non è una sostituzione
 * della sessione autenticata richiesta dal runtime.
 */
public final class InMemoryAuthorizationPolicy implements AuthorizationPolicy {

    private final Set<UserId> submitters;
    private final Set<UserId> moderators;

    public InMemoryAuthorizationPolicy(Set<UserId> submitters, Set<UserId> moderators) {
        this.submitters = Set.copyOf(submitters);
        this.moderators = Set.copyOf(moderators);
    }

    @Override
    public boolean canSubmit(UserId userId) {
        return submitters.contains(userId);
    }

    @Override
    public boolean canModerate(UserId userId) {
        return moderators.contains(userId);
    }
}
