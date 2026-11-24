package it.aegis.adapter.rest.dto;

import java.util.Objects;

/** Corpo della richiesta di creazione segnalazione, validato al confine HTTP. */
public record CreateReportRequest(String key, String category, String description) {

    public CreateReportRequest {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(description, "description");
        if (key.isBlank() || category.isBlank() || description.isBlank()) {
            throw new IllegalArgumentException("request fields must not be blank");
        }
    }
}
