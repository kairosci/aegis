package it.aegis.domain.identity;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/** Identità autenticata con i soli ruoli necessari alle decisioni applicative. */
public final class UserAccount {

    private final UserId id;
    private final Set<Role> roles;

    public UserAccount(UserId id, Set<Role> roles) {
        this.id = Objects.requireNonNull(id, "id");
        EnumSet<Role> copy = EnumSet.noneOf(Role.class);
        copy.addAll(Objects.requireNonNull(roles, "roles"));
        this.roles = Collections.unmodifiableSet(copy);
    }

    public UserId id() {
        return id;
    }

    public boolean hasRole(Role role) {
        return roles.contains(Objects.requireNonNull(role, "role"));
    }
}
