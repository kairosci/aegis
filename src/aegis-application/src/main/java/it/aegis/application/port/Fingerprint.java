package it.aegis.application.port;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.regex.Pattern;

/** Impronta canonica della richiesta, calcolata prima di ogni effetto esterno. */
public record Fingerprint(String sha256Hex) {

    private static final String DIGEST_ALGORITHM = "SHA-256";
    private static final Pattern SHA256_HEX = hexDigestPattern();

    public Fingerprint {
        Objects.requireNonNull(sha256Hex, "sha256Hex");
        if (!SHA256_HEX.matcher(sha256Hex).matches()) {
            throw new IllegalArgumentException("fingerprint must be a SHA-256 hex digest");
        }
    }

    /** Nome dell'algoritmo usato per costruire e verificare l'impronta. */
    public static String algorithm() {
        return DIGEST_ALGORITHM;
    }

    private static Pattern hexDigestPattern() {
        try {
            int hexLength = MessageDigest.getInstance(DIGEST_ALGORITHM).getDigestLength() * 2;
            return Pattern.compile("[0-9a-f]{" + hexLength + "}");
        } catch (NoSuchAlgorithmException cause) {
            throw new IllegalStateException(DIGEST_ALGORITHM + " is required by the platform", cause);
        }
    }
}
