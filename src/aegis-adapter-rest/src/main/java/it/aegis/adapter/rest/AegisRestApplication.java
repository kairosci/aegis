package it.aegis.adapter.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/** Espone le risorse HTTP sotto il prefisso versionato. */
@ApplicationPath("/api")
public final class AegisRestApplication extends Application {
}
