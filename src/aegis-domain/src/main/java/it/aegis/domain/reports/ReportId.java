package it.aegis.domain.reports;

import java.util.Objects;
import java.util.UUID;

/** Identificatore interno di una segnalazione. */
public record ReportId(UUID value) {

    public ReportId {
        Objects.requireNonNull(value, "value");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
