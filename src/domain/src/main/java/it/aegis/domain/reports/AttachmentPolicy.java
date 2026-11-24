package it.aegis.domain.reports;

import java.util.Objects;

/** Regola di accettazione dell'allegato con limite fornito dalla configurazione. */
public final class AttachmentPolicy {

    /** Numero massimo di byte accettato per un allegato. */
    private final long maxBytes;

    /**
     * Crea una regola con il limite configurato.
     *
     * @param maxBytes numero massimo di byte accettato per un allegato
     * @throws IllegalArgumentException se il limite non è positivo
     */
    public AttachmentPolicy(long maxBytes) {
        if (maxBytes <= 0L) {
            throw new IllegalArgumentException("maxBytes must be positive");
        }
        this.maxBytes = maxBytes;
    }

    /**
     * Verifica che l'allegato rientri nel limite configurato.
     *
     * @param attachment allegato da verificare
     * @throws NullPointerException se {@code attachment} è null
     * @throws IllegalArgumentException se la dimensione dell'allegato supera il limite
     */
    public void verify(ReportAttachment attachment) {
        Objects.requireNonNull(attachment, "attachment");
        if (attachment.sizeBytes() > maxBytes) {
            throw new IllegalArgumentException("attachment exceeds the configured limit");
        }
    }

    /**
     * Restituisce il limite di dimensione configurato.
     *
     * @return numero massimo di byte ammesso per un allegato
     */
    public long maxBytes() {
        return maxBytes;
    }
}
