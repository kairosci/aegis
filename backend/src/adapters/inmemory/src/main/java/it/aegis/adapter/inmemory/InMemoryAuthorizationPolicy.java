package it.aegis.adapter.inmemory;

import it.aegis.application.port.AuthorizationPolicy;
import it.aegis.domain.identity.UserId;
import java.util.Set;

/**
 * Autorizzazione in memoria basata su insiemi espliciti. Non è una sostituzione
 * della sessione autenticata richiesta dal runtime.
 *
 * <p>Gli insiemi vengono copiati al momento della costruzione, quindi modifiche
 * successive a quelli passati dal chiamante non cambiano le decisioni.
 */
public final class InMemoryAuthorizationPolicy implements AuthorizationPolicy {

    /** Identità autorizzate a inviare segnalazioni. */
    private final Set<UserId> submitters;

    /** Identità autorizzate a moderare le segnalazioni. */
    private final Set<UserId> moderators;

    /**
     * Crea la politica a partire dagli insiemi di identità autorizzate.
     *
     * @param submitters identità autorizzate all'invio di segnalazioni
     * @param moderators identità autorizzate alla moderazione
     * @throws NullPointerException se uno degli insiemi o uno dei loro elementi è {@code null}
     */
    public InMemoryAuthorizationPolicy(Set<UserId> submitters, Set<UserId> moderators) {
        this.submitters = Set.copyOf(submitters);
        this.moderators = Set.copyOf(moderators);
    }

    /**
     * Verifica se l'identità può inviare una segnalazione.
     *
     * @param userId identità da verificare
     * @return {@code true} se l'identità è presente nell'insieme degli inviatori
     */
    @Override
    public boolean canSubmit(UserId userId) {
        return submitters.contains(userId);
    }

    /**
     * Verifica se l'identità può moderare una segnalazione.
     *
     * @param userId identità da verificare
     * @return {@code true} se l'identità è presente nell'insieme dei moderatori
     */
    @Override
    public boolean canModerate(UserId userId) {
        return moderators.contains(userId);
    }
}
