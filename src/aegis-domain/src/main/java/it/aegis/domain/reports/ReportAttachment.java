package it.aegis.domain.reports;

import java.util.Objects;

/** Allegato in attesa di scansione. Il limite massimo è applicato da AttachmentPolicy. */
public record ReportAttachment(
        String storageKey,
        String contentType,
        long sizeBytes,
        ScanStatus scanStatus) {

    public ReportAttachment {
        Objects.requireNonNull(storageKey, "storageKey");
        Objects.requireNonNull(contentType, "contentType");
        Objects.requireNonNull(scanStatus, "scanStatus");
        if (storageKey.isBlank()) {
            throw new IllegalArgumentException("storageKey must not be blank");
        }
        if (sizeBytes <= 0L) {
            throw new IllegalArgumentException("attachment size must be positive");
        }
    }
}
