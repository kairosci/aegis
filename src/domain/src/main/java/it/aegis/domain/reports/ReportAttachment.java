package it.aegis.domain.reports;

import java.util.Objects;

/**
 * Allegato in attesa di scansione. Il limite massimo è applicato da AttachmentPolicy.
 *
 * @param storageKey chiave che identifica l'oggetto nella memoria di archiviazione
 * @param contentType tipo di contenuto dichiarato per l'allegato
 * @param sizeBytes dimensione dell'allegato in byte
 * @param scanStatus stato della verifica di sicurezza dell'allegato
 */
public record ReportAttachment(
        String storageKey,
        String contentType,
        long sizeBytes,
        ScanStatus scanStatus) {

    /**
     * Verifica che i metadati obbligatori siano presenti e coerenti.
     *
     * @param storageKey chiave che identifica l'oggetto nella memoria di archiviazione
     * @param contentType tipo di contenuto dichiarato per l'allegato
     * @param sizeBytes dimensione dell'allegato in byte
     * @param scanStatus stato della verifica di sicurezza dell'allegato
     * @throws NullPointerException se uno dei metadati obbligatori è null
     * @throws IllegalArgumentException se la chiave è vuota o la dimensione non è positiva
     */
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
