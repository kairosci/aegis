package it.aegis.domain.accessibility;

/** Stato osservato di una caratteristica di accessibilità. */
public enum AccessibilityStatus {

    /** La caratteristica è risultata disponibile al momento dell'osservazione. */
    AVAILABLE,

    /** La caratteristica è risultata non disponibile: costituisce quindi una prova esplicita di incompatibilità. */
    UNAVAILABLE,

    /** Lo stato non è noto, perché manca un'osservazione attendibile o la precedente è scaduta. */
    UNKNOWN
}
