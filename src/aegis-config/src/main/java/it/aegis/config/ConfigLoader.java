package it.aegis.config;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

/** Carica la configurazione da AEGIS_CONFIG_DIR senza valori predefiniti. */
public final class ConfigLoader {

    private static final String CONFIG_DIR_ENV = "AEGIS_CONFIG_DIR";
    private static final String CONFIG_FILE = "application.properties";

    private static final String FRESHNESS_DAYS = "aegis.accessibility.freshness-days";
    private static final String ATTACHMENT_MEGABYTES = "aegis.reports.max-attachment-megabytes";
    private static final String TRACKING_ENTROPY_BITS = "aegis.tracking.entropy-bits";
    private static final String TRACKING_ALPHABET = "aegis.tracking.alphabet";
    private static final String EARTH_RADIUS_METRES = "aegis.geo.earth-radius-metres";

    private static final long BINARY_PREFIX_BASE = 1024L;

    private ConfigLoader() {
    }

    public static AegisConfig load() {
        String directory = System.getenv(CONFIG_DIR_ENV);
        if (directory == null || directory.isBlank()) {
            throw new IllegalStateException(CONFIG_DIR_ENV + " must be set");
        }
        Path file = Path.of(directory, CONFIG_FILE);
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException cause) {
            throw new IllegalStateException("cannot read " + file, cause);
        }

        long megabytes = requiredLong(properties, ATTACHMENT_MEGABYTES);
        long bytesPerMebibyte = BINARY_PREFIX_BASE * BINARY_PREFIX_BASE;
        return new AegisConfig(
                Duration.ofDays(requiredLong(properties, FRESHNESS_DAYS)),
                Math.multiplyExact(megabytes, bytesPerMebibyte),
                requiredInt(properties, TRACKING_ENTROPY_BITS),
                required(properties, TRACKING_ALPHABET),
                requiredDouble(properties, EARTH_RADIUS_METRES));
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("missing configuration value: " + key);
        }
        return value;
    }

    private static long requiredLong(Properties properties, String key) {
        try {
            return Long.parseLong(required(properties, key));
        } catch (NumberFormatException cause) {
            throw new IllegalStateException("invalid integer for " + key, cause);
        }
    }

    private static int requiredInt(Properties properties, String key) {
        try {
            return Integer.parseInt(required(properties, key));
        } catch (NumberFormatException cause) {
            throw new IllegalStateException("invalid integer for " + key, cause);
        }
    }

    private static double requiredDouble(Properties properties, String key) {
        try {
            return Double.parseDouble(required(properties, key));
        } catch (NumberFormatException cause) {
            throw new IllegalStateException("invalid decimal for " + key, cause);
        }
    }
}
