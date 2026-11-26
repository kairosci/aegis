package it.aegis.domain.routing;

/** Esito di verifica di un tratto, distinto dall'esito del solo provider. */
public enum RouteCompatibility {

    /** Il tratto è verificato e supportato da almeno un'evidenza di disponibilità. */
    COMPATIBLE,

    /** Il tratto presenta barriere o un'evidenza esplicita di indisponibilità. */
    INCOMPATIBLE,

    /** Il tratto è verificato dal provider, ma mancano evidenze utilizzabili come prova. */
    UNKNOWN,

    /** Il provider non ha verificato il tratto, indipendentemente dalle evidenze. */
    UNVERIFIED
}
