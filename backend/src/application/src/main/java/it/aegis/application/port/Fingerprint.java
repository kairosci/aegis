package it.aegis.application.port;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Impronta canonica della richiesta, calcolata prima di ogni effetto esterno.
 * Identifica il contenuto rilevante dell'invio ai fini dell'idempotenza, senza
 * riportare i dati grezzi della richiesta.
 *
 * @param sha256Hex digest SHA-256 in formato esadecimale minuscolo
 */
public record Fingerprint(String sha256Hex) {

    /** Identificatore dell'algoritmo di digest richiesto dalla piattaforma. */
    private static final String DIGEST_ALGORITHM = "SHA-256";
    /** Espressione regolare che accetta un digest esadecimale della lunghezza prevista. */
    private static final Pattern SHA256_HEX = hexDigestPattern();

    /**
     * Verifica che il valore fornito sia un digest esadecimale valido.
     *
     * @throws NullPointerException se {@code sha256Hex} è {@code null}
     * @throws IllegalArgumentException se il valore non è un digest SHA-256 esadecimale
     */
    public Fingerprint {
        Objects.requireNonNull(sha256Hex, "sha256Hex");
        if (!SHA256_HEX.matcher(sha256Hex).matches()) {
            throw new IllegalArgumentException("fingerprint must be a SHA-256 hex digest");
        }
    }

    /**
     * Restituisce il nome dell'algoritmo usato per costruire e verificare l'impronta.
     *
     * @return identificatore dell'algoritmo di digest richiesto dalla piattaforma
     */
    public static String algorithm() {
        return DIGEST_ALGORITHM;
    }

    /**
     * Costruisce l'espressione regolare del digest a partire dalla lunghezza
     * dell'algoritmo disponibile sulla piattaforma.
     *
     * @return espressione regolare per un digest esadecimale minuscolo
     * @throws IllegalStateException se la piattaforma non fornisce l'algoritmo richiesto
     */
    private static Pattern hexDigestPattern() {
        try {
            int hexLength = MessageDigest.getInstance(DIGEST_ALGORITHM).getDigestLength() * 2;
            return Pattern.compile("[0-9a-f]{" + hexLength + "}");
        } catch (NoSuchAlgorithmException cause) {
            throw new IllegalStateException(DIGEST_ALGORITHM + " is required by the platform", cause);
        }
    }
}
