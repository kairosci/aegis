package it.aegis.adapter.inmemory;

import it.aegis.application.port.TrackingCodeGenerator;
import it.aegis.config.AegisConfig;
import it.aegis.domain.reports.TrackingCode;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;

/** Genera il codice di tracking con entropia e alfabeto definiti nella configurazione. */
public final class SecureRandomTrackingCodeGenerator implements TrackingCodeGenerator {

    private final AegisConfig config;
    private final SecureRandom random = new SecureRandom();

    public SecureRandomTrackingCodeGenerator(AegisConfig config) {
        this.config = Objects.requireNonNull(config, "config");
    }

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
