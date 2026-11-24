package it.aegis.adapter.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Espone le risorse HTTP sotto il prefisso versionato.
 *
 * <p>La classe definisce soltanto il punto di ingresso JAX-RS e il prefisso
 * comune dei percorsi: non contiene regole di dominio né effetti esterni.</p>
 */
@ApplicationPath("/api")
public final class AegisRestApplication extends Application {

    /** Crea il punto di ingresso JAX-RS senza configurazione aggiuntiva. */
    public AegisRestApplication() {
    }
}
