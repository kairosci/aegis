package it.aegis.adapter.gtfs;

import it.aegis.application.port.TransitDataProvider;
import it.aegis.application.port.TransitStopSnapshot;
import it.aegis.config.AegisConfig;
import it.aegis.domain.places.GeoPoint;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Provider di fermate che legge un file GTFS dalla directory configurata.
 *
 * <p>La distribuzione deve assegnare la directory sorgente. Se la directory è
 * vuota, il file manca o l'intestazione non contiene le colonne
 * previste dal formato, l'adattatore restituisce uno snapshot vuoto. Le righe
 * con coordinate non valide o campi obbligatori mancanti vengono scartate. Lo
 * snapshot non contiene alcun campo di accessibilità.</p>
 */
public final class GtfsTransitDataProvider implements TransitDataProvider {

    /** Nome del file delle fermate previsto dal formato GTFS. */
    private static final String STOPS_FILE = "stops.txt";

    /** Nome della colonna che contiene l'identificatore stabile della fermata. */
    private static final String COLUMN_STOP_ID = "stop_id";

    /** Nome della colonna che contiene il nome della fermata. */
    private static final String COLUMN_STOP_NAME = "stop_name";

    /** Nome della colonna che contiene la latitudine. */
    private static final String COLUMN_STOP_LATITUDE = "stop_lat";

    /** Nome della colonna che contiene la longitudine. */
    private static final String COLUMN_STOP_LONGITUDE = "stop_lon";

    /** Carattere che separa i campi in una riga del file. */
    private static final char FIELD_SEPARATOR = ',';

    /** Carattere che delimita un campo citato. */
    private static final char QUOTE = '"';

    /** Carattere di indicazione dell'ordine dei byte all'inizio del file. */
    private static final char BYTE_ORDER_MARK = '\uFEFF';

    /** Directory esterna che contiene il file delle fermate, anche vuota. */
    private final String sourceDirectory;

    /**
     * Crea il provider con la configurazione esterna.
     *
     * @param config configurazione tipizzata che contiene la directory sorgente
     * @throws NullPointerException se {@code config} è {@code null}
     */
    public GtfsTransitDataProvider(AegisConfig config) {
        Objects.requireNonNull(config, "config");
        this.sourceDirectory = config.transit().sourceDirectory();
    }

    @Override
    public TransitStopSnapshot stops() {
        if (sourceDirectory.isBlank()) {
            return TransitStopSnapshot.EMPTY;
        }
        Path file;
        try {
            file = Path.of(sourceDirectory).resolve(STOPS_FILE);
        } catch (InvalidPathException cause) {
            return TransitStopSnapshot.EMPTY;
        }
        if (!Files.isRegularFile(file)) {
            return TransitStopSnapshot.EMPTY;
        }
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            return snapshot(reader);
        } catch (IOException cause) {
            return TransitStopSnapshot.EMPTY;
        }
    }

    /**
     * Legge l'intestazione e le righe per costruire lo snapshot.
     *
     * @param reader lettore del file delle fermate
     * @return snapshot con le sole fermate valide, vuoto se l'intestazione non è conforme
     * @throws IOException se la lettura del file fallisce
     */
    private static TransitStopSnapshot snapshot(BufferedReader reader) throws IOException {
        String headerLine = reader.readLine();
        if (headerLine == null) {
            return TransitStopSnapshot.EMPTY;
        }
        Optional<List<String>> header = csvLine(withoutByteOrderMark(headerLine));
        if (header.isEmpty()) {
            return TransitStopSnapshot.EMPTY;
        }
        Optional<int[]> columns = columns(header.get());
        if (columns.isEmpty()) {
            return TransitStopSnapshot.EMPTY;
        }
        Map<String, TransitStopSnapshot.Stop> stops = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null) {
            Optional<List<String>> cells = csvLine(line);
            if (cells.isEmpty()) {
                continue;
            }
            stop(cells.get(), columns.get()).ifPresent(value -> stops.putIfAbsent(value.identifier(), value));
        }
        return new TransitStopSnapshot(List.copyOf(stops.values()));
    }

    /**
     * Individua le posizioni delle colonne obbligatorie nell'intestazione.
     *
     * @param header intestazione già suddivisa in campi
     * @return posizioni di identificatore, nome, latitudine e longitudine, vuote se manca una colonna
     */
    private static Optional<int[]> columns(List<String> header) {
        int identifier = header.indexOf(COLUMN_STOP_ID);
        int name = header.indexOf(COLUMN_STOP_NAME);
        int latitude = header.indexOf(COLUMN_STOP_LATITUDE);
        int longitude = header.indexOf(COLUMN_STOP_LONGITUDE);
        if (identifier < 0 || name < 0 || latitude < 0 || longitude < 0) {
            return Optional.empty();
        }
        return Optional.of(new int[] {identifier, name, latitude, longitude});
    }

    /**
     * Costruisce una fermata validando campi obbligatori e coordinate.
     *
     * @param values campi della riga già analizzati
     * @param columns posizioni delle colonne obbligatorie
     * @return fermata validata, vuota se la riga non è utilizzabile
     */
    private static Optional<TransitStopSnapshot.Stop> stop(List<String> values, int[] columns) {
        int last = Math.max(Math.max(columns[0], columns[1]), Math.max(columns[2], columns[3]));
        if (last >= values.size()) {
            return Optional.empty();
        }
        String identifier = values.get(columns[0]).strip();
        String name = values.get(columns[1]).strip();
        if (identifier.isEmpty() || name.isEmpty()) {
            return Optional.empty();
        }
        try {
            GeoPoint position = new GeoPoint(
                    Double.parseDouble(values.get(columns[2]).strip()),
                    Double.parseDouble(values.get(columns[3]).strip()));
            return Optional.of(new TransitStopSnapshot.Stop(identifier, name, position));
        } catch (IllegalArgumentException cause) {
            return Optional.empty();
        }
    }

    /**
     * Analizza una riga CSV con campi citati e virgolette raddoppiate.
     *
     * @param line riga del file da analizzare
     * @return campi della riga, vuoti se le virgolette non sono bilanciate
     */
    private static Optional<List<String>> csvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (int index = 0; index < line.length(); index++) {
            char current = line.charAt(index);
            if (quoted) {
                if (current == QUOTE) {
                    if (index + 1 < line.length() && line.charAt(index + 1) == QUOTE) {
                        field.append(QUOTE);
                        index++;
                    } else {
                        quoted = false;
                    }
                } else {
                    field.append(current);
                }
            } else if (current == QUOTE && field.length() == 0) {
                quoted = true;
            } else if (current == FIELD_SEPARATOR) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(current);
            }
        }
        if (quoted) {
            return Optional.empty();
        }
        fields.add(field.toString());
        return Optional.of(fields);
    }

    /**
     * Rimuove l'indicazione dell'ordine dei byte dall'inizio dell'intestazione.
     *
     * @param line prima riga del file
     * @return riga senza il carattere iniziale di indicazione dell'ordine dei byte
     */
    private static String withoutByteOrderMark(String line) {
        if (!line.isEmpty() && line.charAt(0) == BYTE_ORDER_MARK) {
            return line.substring(1);
        }
        return line;
    }
}
