package it.aegis.domain.places;

/** Coordinate geografiche validate. */
public record GeoPoint(double latitude, double longitude) {

    private static final double FULL_TURN_DEGREES = 360.0;
    private static final double MAX_LATITUDE = FULL_TURN_DEGREES / 4.0;
    private static final double MAX_LONGITUDE = FULL_TURN_DEGREES / 2.0;

    public GeoPoint {
        if (!Double.isFinite(latitude) || Math.abs(latitude) > MAX_LATITUDE) {
            throw new IllegalArgumentException("latitude outside the accepted range");
        }
        if (!Double.isFinite(longitude) || Math.abs(longitude) > MAX_LONGITUDE) {
            throw new IllegalArgumentException("longitude outside the accepted range");
        }
    }
}
