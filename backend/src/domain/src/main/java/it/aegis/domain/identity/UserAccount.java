package it.aegis.domain.identity;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/** Identità autenticata con i soli ruoli necessari alle decisioni applicative. */
public final class UserAccount {

    /** Identificatore immutabile dell'account. */
    private final UserId id;

    /** Insieme immutabile dei ruoli attribuiti all'account. */
    private final Set<Role> roles;

    /**
     * Crea un account copiando i ruoli forniti in un insieme immutabile.
     *
     * @param id identificatore dell'account
     * @param roles ruoli attribuiti all'account
     * @throws NullPointerException se {@code id} o {@code roles} sono null
     */
    public UserAccount(UserId id, Set<Role> roles) {
        this.id = Objects.requireNonNull(id, "id");
        EnumSet<Role> copy = EnumSet.noneOf(Role.class);
        copy.addAll(Objects.requireNonNull(roles, "roles"));
        this.roles = Collections.unmodifiableSet(copy);
    }

    /**
     * Restituisce l'identificatore dell'account.
     *
     * @return l'identificatore immutabile dell'account
     */
    public UserId id() {
        return id;
    }

    /**
     * Verifica che all'account sia attribuito il ruolo indicato.
     *
     * @param role ruolo da verificare
     * @return {@code true} se il ruolo è presente nell'insieme dell'account
     * @throws NullPointerException se {@code role} è null
     */
    public boolean hasRole(Role role) {
        return roles.contains(Objects.requireNonNull(role, "role"));
    }
}
