package it.aegis.domain.reports;

import java.util.Objects;

/** Regola di accettazione dell'allegato con limite fornito dalla configurazione. */
public final class AttachmentPolicy {

    private final long maxBytes;

    public AttachmentPolicy(long maxBytes) {
        if (maxBytes <= 0L) {
            throw new IllegalArgumentException("maxBytes must be positive");
        }
        this.maxBytes = maxBytes;
    }

    public void verify(ReportAttachment attachment) {
        Objects.requireNonNull(attachment, "attachment");
        if (attachment.sizeBytes() > maxBytes) {
            throw new IllegalArgumentException("attachment exceeds the configured limit");
        }
    }

    public long maxBytes() {
        return maxBytes;
    }
}
