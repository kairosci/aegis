package it.aegis.domain.routing;

/** Esito di verifica di un tratto, distinto dall'esito del solo provider. */
public enum RouteCompatibility {
    COMPATIBLE,
    INCOMPATIBLE,
    UNKNOWN,
    UNVERIFIED
}
