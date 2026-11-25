package it.aegis.application.port;

import java.util.Objects;

/**
 * Riferimento opaco a un oggetto conservato in uno storage privato.
 *
 * <p>Il riferimento non contiene il nome del file fornito dal client e non
 * concede accesso pubblico. È sufficiente per rimuovere l'oggetto in modo
 * esplicito quando l'operazione che lo ha creato non viene completata.</p>
 *
 * @param bucket bucket privato che contiene l'oggetto, non vuoto
 * @param objectKey chiave dell'oggetto nel bucket, non vuota
 */
public record StorageReference(String bucket, String objectKey) {

    /**
     * Verifica che bucket e chiave siano presenti.
     *
     * @throws NullPointerException se una componente è {@code null}
     * @throws IllegalArgumentException se bucket o chiave sono vuoti
     */
    public StorageReference {
        Objects.requireNonNull(bucket, "bucket");
        Objects.requireNonNull(objectKey, "objectKey");
        if (bucket.isBlank()) {
            throw new IllegalArgumentException("bucket must not be blank");
        }
        if (objectKey.isBlank()) {
            throw new IllegalArgumentException("objectKey must not be blank");
        }
    }
}
