package it.aegis.adapter.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Espone le risorse HTTP sotto il prefisso versionato.
 *
 * <p>La classe definisce soltanto il punto di ingresso JAX-RS e il prefisso
 * comune dei percorsi: non contiene regole di dominio né effetti esterni. Il
 * prefisso è una costante di protocollo condivisa dalle risorse.</p>
 */
@ApplicationPath(AegisRestApplication.API_BASE_PATH)
public final class AegisRestApplication extends Application {

    /** Prefisso versionato comune a tutte le risorse HTTP dell'applicazione. */
    public static final String API_BASE_PATH = "/api/v1";

    /** Crea il punto di ingresso JAX-RS senza configurazione aggiuntiva. */
    public AegisRestApplication() {
    }
}
