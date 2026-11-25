package it.aegis.domain.reports;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Credenziale Base62 che consente il tracking pubblico senza esporre dati personali.
 * La rappresentazione testuale è redatta per evitare la comparsa del codice nei log.
 *
 * @param value sequenza Base62 che costituisce il codice di tracking
 */
public record TrackingCode(String value) {

    /** Espressione regolare che ammette solo caratteri alfanumerici Base62 non vuoti. */
    private static final Pattern BASE62 = Pattern.compile("[0-9A-Za-z]+");

    /**
     * Verifica che il codice sia presente e composto esclusivamente da caratteri Base62.
     *
     * @param value sequenza Base62 che costituisce il codice di tracking
     * @throws NullPointerException se {@code value} è null
     * @throws IllegalArgumentException se il valore non è Base62 o è vuoto
     */
    public TrackingCode {
        Objects.requireNonNull(value, "value");
        if (!BASE62.matcher(value).matches()) {
            throw new IllegalArgumentException("tracking code must be Base62");
        }
    }

    /**
     * Restituisce una rappresentazione redatta, in modo da non divulgare la credenziale nei log.
     *
     * @return la stringa fissa {@code [REDACTED]}, che non contiene il codice
     */
    @Override
    public String toString() {
        return "[REDACTED]";
    }
}
