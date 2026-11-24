package it.aegis.domain.places;

/**
 * Coordinate geografiche validate.
 *
 * <p>Gli intervalli ammessi sono fissati dalla convenzione geografica: la
 * latitudine è compresa tra -90 e 90 gradi, la longitudine tra -180 e 180.
 * Valori non finiti sono respinti.</p>
 *
 * @param latitude latitudine in gradi
 * @param longitude longitudine in gradi
 */
public record GeoPoint(double latitude, double longitude) {

    /** Ampiezza di un giro completo in gradi, usata per derivare i limiti. */
    private static final double FULL_TURN_DEGREES = 360.0;

    /** Valore assoluto massimo ammesso per la latitudine. */
    private static final double MAX_LATITUDE = FULL_TURN_DEGREES / 4.0;

    /** Valore assoluto massimo ammesso per la longitudine. */
    private static final double MAX_LONGITUDE = FULL_TURN_DEGREES / 2.0;

    /**
     * Verifica che le coordinate siano finite e comprese negli intervalli ammessi.
     *
     * @param latitude latitudine in gradi
     * @param longitude longitudine in gradi
     * @throws IllegalArgumentException se una coordinata non è finita o supera il limite ammesso
     */
    public GeoPoint {
        if (!Double.isFinite(latitude) || Math.abs(latitude) > MAX_LATITUDE) {
            throw new IllegalArgumentException("latitude outside the accepted range");
        }
        if (!Double.isFinite(longitude) || Math.abs(longitude) > MAX_LONGITUDE) {
            throw new IllegalArgumentException("longitude outside the accepted range");
        }
    }
}
