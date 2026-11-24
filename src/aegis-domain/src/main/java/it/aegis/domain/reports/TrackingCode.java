package it.aegis.domain.reports;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Credenziale Base62 che consente il tracking pubblico senza esporre dati personali.
 * La rappresentazione testuale è redatta per evitare la comparsa del codice nei log.
 */
public record TrackingCode(String value) {

    private static final Pattern BASE62 = Pattern.compile("[0-9A-Za-z]+");

    public TrackingCode {
        Objects.requireNonNull(value, "value");
        if (!BASE62.matcher(value).matches()) {
            throw new IllegalArgumentException("tracking code must be Base62");
        }
    }

    @Override
    public String toString() {
        return "[REDACTED]";
    }
}
