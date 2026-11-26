package it.aegis.adapter.inmemory;

import it.aegis.application.port.TrackingCodeGenerator;
import it.aegis.config.AegisConfig;
import it.aegis.domain.reports.TrackingCode;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * Genera il codice di tracking con entropia e alfabeto definiti nella configurazione.
 *
 * <p>I byte casuali sono prodotti da {@link SecureRandom}; il valore zero viene scartato
 * per evitare un codice vuoto e la conversione in base utilizza l'alfabeto configurato.
 */
public final class SecureRandomTrackingCodeGenerator implements TrackingCodeGenerator {

    /** Configurazione che determina entropia, alfabeto e lunghezza del codice. */
    private final AegisConfig config;

    /** Sorgente casuale crittograficamente forte usata per i byte del codice. */
    private final SecureRandom random = new SecureRandom();

    /**
     * Crea il generatore usando la configurazione applicativa.
     *
     * @param config configurazione con entropia e alfabeto del codice di tracking
     * @throws NullPointerException se {@code config} è {@code null}
     */
    public SecureRandomTrackingCodeGenerator(AegisConfig config) {
        this.config = Objects.requireNonNull(config, "config");
    }

    /**
     * Genera un codice di tracking non vuoto usando l'alfabeto configurato.
     *
     * @return codice ottenuto dai byte casuali e convertito nell'alfabeto configurato
     */
    @Override
    public TrackingCode generate() {
        byte[] bytes = new byte[config.trackingTokenBytes()];
        BigInteger base = BigInteger.valueOf(config.trackingBase());
        BigInteger value;
        do {
            random.nextBytes(bytes);
            value = new BigInteger(1, bytes);
        } while (value.signum() == 0);

        StringBuilder encoded = new StringBuilder();
        while (value.signum() > 0) {
            encoded.append(config.trackingAlphabet().charAt(value.mod(base).intValue()));
            value = value.divide(base);
        }
        return new TrackingCode(encoded.reverse().toString());
    }
}
