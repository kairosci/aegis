package it.aegis.adapter.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Riga della tabella dei luoghi.
 *
 * <p>La classe espone solo costruttori e accessori. Le coordinate sono memorizzate
 * come colonne numeriche separate, così la conversione verso il tipo di dominio
 * resta esplicita e verificabile.
 */
@Entity
@Table(name = SchemaNames.PLACE)
public final class PlaceEntity {

    /** Identificativo del luogo. */
    @Id
    @Column(name = SchemaNames.ID, nullable = false, updatable = false)
    private UUID id;

    /** Nome pubblico del luogo. */
    @Column(name = SchemaNames.NAME, nullable = false)
    private String name;

    /** Latitudine del luogo in gradi. */
    @Column(name = SchemaNames.LATITUDE, nullable = false)
    private double latitude;

    /** Longitudine del luogo in gradi. */
    @Column(name = SchemaNames.LONGITUDE, nullable = false)
    private double longitude;

    /** Costruttore senza argomenti richiesto dal provider JPA. */
    protected PlaceEntity() {
    }

    /**
     * Crea la riga di un luogo con le coordinate validate a monte.
     *
     * @param id identificativo del luogo
     * @param name nome pubblico del luogo
     * @param latitude latitudine in gradi
     * @param longitude longitudine in gradi
     */
    public PlaceEntity(UUID id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Restituisce l'identificativo del luogo.
     *
     * @return identificativo della riga
     */
    public UUID id() {
        return id;
    }

    /**
     * Restituisce il nome pubblico del luogo.
     *
     * @return nome memorizzato nella riga
     */
    public String name() {
        return name;
    }

    /**
     * Restituisce la latitudine del luogo.
     *
     * @return latitudine in gradi
     */
    public double latitude() {
        return latitude;
    }

    /**
     * Restituisce la longitudine del luogo.
     *
     * @return longitudine in gradi
     */
    public double longitude() {
        return longitude;
    }
}
