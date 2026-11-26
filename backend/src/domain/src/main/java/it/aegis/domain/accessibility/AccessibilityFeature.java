package it.aegis.domain.accessibility;

import java.time.Instant;
import java.util.Objects;

/**
 * Caratteristica osservata di un luogo con fonte, stato e data di osservazione.
 *
 * <p>Il valore registrato non costituisce di per sé una prova di accessibilità:
 * per essere usato come evidenza deve provenire da un'osservazione compresa
 * nella finestra di validità configurata e avere stato disponibile.</p>
 *
 * @param code codice identificativo della caratteristica osservata
 * @param status stato rilevato al momento dell'osservazione
 * @param source fonte che ha fornito l'osservazione
 * @param observedAt istante in cui l'osservazione è stata effettuata
 */
public record AccessibilityFeature(
        String code,
        AccessibilityStatus status,
        String source,
        Instant observedAt) {

    /**
     * Verifica che i dati obbligatori siano presenti e che il codice sia significativo.
     *
     * @param code codice identificativo della caratteristica osservata
     * @param status stato rilevato al momento dell'osservazione
     * @param source fonte che ha fornito l'osservazione
     * @param observedAt istante in cui l'osservazione è stata effettuata
     * @throws NullPointerException se {@code code}, {@code source} o {@code observedAt} sono null
     * @throws IllegalArgumentException se {@code code} è vuoto o contiene solo spazi
     */
    public AccessibilityFeature {
        Objects.requireNonNull(code, "code");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(observedAt, "observedAt");
        if (code.isBlank()) {
            throw new IllegalArgumentException("code must not be blank");
        }
    }

    /**
     * Classifica come sconosciuto ogni stato assente o osservato fuori dalla finestra di validità.
     *
     * <p>Lo stato fornito è conservato solo quando l'osservazione cade nella
     * finestra calcolata rispetto a {@code now}; in caso contrario l'istanza
     * risultante ha stato sconosciuto e non può essere usata come evidenza.</p>
     *
     * @param code codice identificativo della caratteristica osservata
     * @param status stato rilevato, che può essere assente
     * @param source fonte che ha fornito l'osservazione
     * @param observedAt istante in cui l'osservazione è stata effettuata
     * @param window finestra di validità entro cui l'osservazione è ancora rilevante
     * @param now istante di riferimento per la verifica della validità
     * @return l'osservazione con lo stato effettivo, sconosciuto quando non è attendibile
     * @throws NullPointerException se {@code observedAt} o {@code window} sono null
     * @throws IllegalArgumentException se {@code code} è vuoto o contiene solo spazi
     */
    public static AccessibilityFeature observed(
            String code,
            AccessibilityStatus status,
            String source,
            Instant observedAt,
            FreshnessWindow window,
            Instant now) {
        Objects.requireNonNull(observedAt, "observedAt");
        Objects.requireNonNull(window, "window");
        AccessibilityStatus effective = status != null && window.covers(observedAt, now)
                ? status
                : AccessibilityStatus.UNKNOWN;
        return new AccessibilityFeature(code, effective, source, observedAt);
    }

    /**
     * Indica se questa osservazione può essere usata come prova di accessibilità.
     *
     * @return {@code true} solo se lo stato effettivo è {@link AccessibilityStatus#AVAILABLE}
     */
    public boolean isUsableAsEvidence() {
        return status == AccessibilityStatus.AVAILABLE;
    }
}
