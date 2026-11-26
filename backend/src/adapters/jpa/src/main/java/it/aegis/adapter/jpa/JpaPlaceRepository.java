package it.aegis.adapter.jpa;

import it.aegis.adapter.jpa.entity.AccessibilityFeatureEntity;
import it.aegis.adapter.jpa.entity.PlaceEntity;
import it.aegis.adapter.jpa.mapper.PlaceMapper;
import it.aegis.application.port.PlaceRepository;
import it.aegis.config.AegisConfig;
import it.aegis.domain.accessibility.FreshnessWindow;
import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.places.Place;
import it.aegis.domain.places.PlaceId;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementazione JPA della porta di lettura dei luoghi.
 *
 * <p>La ricerca per raggio usa le coordinate delle righe e il raggio terrestre
 * fornito dalla configurazione tipizzata. La finestra di freschezza delle
 * osservazioni è passata al mapper, che delega al dominio la classificazione
 * degli stati di accessibilità.
 */
public final class JpaPlaceRepository implements PlaceRepository {

    /** Interrogazione delle osservazioni associate a un luogo. */
    private static final String FEATURES_BY_PLACE =
            "select feature from AccessibilityFeatureEntity feature where feature.id.placeId = :placeId";

    /** Interrogazione di tutte le righe di luogo, filtrate poi per distanza. */
    private static final String ALL_PLACES = "select place from PlaceEntity place";

    /** Gestore della persistenza iniettato dal contenitore. */
    private final EntityManager entityManager;

    /** Configurazione tipizzata da cui derivare raggio terrestre e freschezza. */
    private final AegisConfig config;

    /** Finestra di validità delle osservazioni di accessibilità. */
    private final FreshnessWindow window;

    /** Orologio usato come riferimento temporale per la freschezza. */
    private final Clock clock;

    /**
     * Crea il repository con le dipendenze iniettate.
     *
     * @param entityManager gestore della persistenza del contenitore
     * @param config configurazione applicativa tipizzata
     * @param clock orologio di riferimento per la validità delle osservazioni
     */
    @Inject
    public JpaPlaceRepository(EntityManager entityManager, AegisConfig config, Clock clock) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.config = Objects.requireNonNull(config);
        this.clock = Objects.requireNonNull(clock);
        this.window = new FreshnessWindow(config.freshnessWindow());
    }

    /**
     * Cerca un luogo per identificativo.
     *
     * @param id identificativo del luogo
     * @return luogo ricostruito, o vuoto se l'identificativo non è noto
     */
    @Override
    public Optional<Place> findById(PlaceId id) {
        Objects.requireNonNull(id);
        PlaceEntity row = entityManager.find(PlaceEntity.class, id.value());
        if (row == null) {
            return Optional.empty();
        }
        return Optional.of(toDomain(row, clock.instant()));
    }

    /**
     * Cerca i luoghi la cui distanza dal centro non supera il raggio indicato.
     *
     * @param centre punto da cui misurare le distanze
     * @param radiusMetres raggio massimo in metri, maggiore di zero
     * @return elenco dei luoghi entro il raggio, eventualmente vuoto
     * @throws IllegalArgumentException se {@code radiusMetres} non è positivo
     */
    @Override
    public List<Place> search(GeoPoint centre, int radiusMetres) {
        Objects.requireNonNull(centre);
        if (radiusMetres <= 0) {
            throw new IllegalArgumentException();
        }
        Instant now = clock.instant();
        List<Place> matches = new ArrayList<>();
        for (PlaceEntity row : entityManager.createQuery(ALL_PLACES, PlaceEntity.class).getResultList()) {
            GeoPoint position = new GeoPoint(row.latitude(), row.longitude());
            if (distanceMetres(centre, position) <= radiusMetres) {
                matches.add(toDomain(row, now));
            }
        }
        return List.copyOf(matches);
    }

    /**
     * Costruisce il luogo di dominio caricando le osservazioni collegate.
     *
     * @param row riga del luogo
     * @param now istante di riferimento per la validità delle osservazioni
     * @return luogo con caratteristiche validate
     */
    private Place toDomain(PlaceEntity row, Instant now) {
        List<AccessibilityFeatureEntity> features = entityManager
                .createQuery(FEATURES_BY_PLACE, AccessibilityFeatureEntity.class)
                .setParameter("placeId", row.id())
                .getResultList();
        return PlaceMapper.toDomain(row, features, window, now);
    }

    /**
     * Calcola la distanza tra due punti con la formula dell'emisenoverso.
     *
     * @param from punto di partenza
     * @param to punto di arrivo
     * @return distanza in metri calcolata con il raggio configurato
     */
    private double distanceMetres(GeoPoint from, GeoPoint to) {
        double latitudeFrom = Math.toRadians(from.latitude());
        double latitudeTo = Math.toRadians(to.latitude());
        double deltaLatitude = latitudeTo - latitudeFrom;
        double deltaLongitude = Math.toRadians(to.longitude() - from.longitude());
        double haversine = Math.pow(Math.sin(deltaLatitude / 2.0), 2.0)
                + Math.cos(latitudeFrom) * Math.cos(latitudeTo)
                * Math.pow(Math.sin(deltaLongitude / 2.0), 2.0);
        return 2.0 * config.earthRadiusMetres() * Math.asin(Math.sqrt(haversine));
    }
}
