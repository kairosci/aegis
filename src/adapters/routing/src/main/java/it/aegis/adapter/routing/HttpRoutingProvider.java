package it.aegis.adapter.routing;

import it.aegis.application.port.RoutingProvider;
import it.aegis.config.AegisConfig;
import it.aegis.config.MessageSource;
import it.aegis.domain.places.GeoPoint;
import it.aegis.domain.routing.RouteCandidate;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Provider di routing che invia richieste HTTP e converte la risposta JSON.
 *
 * <p>L'endpoint e la credenziale arrivano dalla configurazione esterna. Ogni
 * risposta è validata al confine prima di diventare un tipo di dominio: uno
 * schema non conforme, un errore di trasporto o una configurazione assente
 * producono un errore tipizzato con messaggio letto dal bundle. La credenziale
 * non viene mai inclusa in messaggi o log.</p>
 */
public final class HttpRoutingProvider implements RoutingProvider {

    /** Chiave del messaggio per configurazione assente. */
    private static final String KEY_UNCONFIGURED = "routing.unconfigured";

    /** Chiave del messaggio per errore di trasporto o stato HTTP non previsto. */
    private static final String KEY_TRANSPORT = "routing.transport-failure";

    /** Chiave del messaggio per risposta non conforme al contratto. */
    private static final String KEY_RESPONSE_CONTRACT = "routing.response-contract";

    /** Schemi URI ammessi per l'endpoint configurato. */
    private static final Set<String> SUPPORTED_SCHEMES = Set.of("http", "https");

    /** Codice di stato HTTP che indica una risposta positiva. */
    private static final int SUCCESS_STATUS = 200;

    /** Nome dell'header che dichiara il formato accettato. */
    private static final String HEADER_ACCEPT = "Accept";

    /** Nome dell'header che trasporta la credenziale. */
    private static final String HEADER_AUTHORIZATION = "Authorization";

    /** Prefisso dello schema di autorizzazione. */
    private static final String AUTHORIZATION_SCHEME = "Bearer ";

    /** Valore dell'header che dichiara il formato JSON. */
    private static final String MEDIA_TYPE_JSON = "application/json";

    /** Nome del parametro che trasporta la latitudine di partenza. */
    private static final String QUERY_ORIGIN_LATITUDE = "originLatitude";

    /** Nome del parametro che trasporta la longitudine di partenza. */
    private static final String QUERY_ORIGIN_LONGITUDE = "originLongitude";

    /** Nome del parametro che trasporta la latitudine di arrivo. */
    private static final String QUERY_DESTINATION_LATITUDE = "destinationLatitude";

    /** Nome del parametro che trasporta la longitudine di arrivo. */
    private static final String QUERY_DESTINATION_LONGITUDE = "destinationLongitude";

    /** Nome del campo che contiene l'elenco dei percorsi. */
    private static final String FIELD_ROUTES = "routes";

    /** Nome del campo che contiene l'estremo di partenza di un tratto. */
    private static final String FIELD_FROM = "from";

    /** Nome del campo che contiene l'estremo di arrivo di un tratto. */
    private static final String FIELD_TO = "to";

    /** Nome del campo che contiene la latitudine di un punto. */
    private static final String FIELD_LATITUDE = "latitude";

    /** Nome del campo che contiene la longitudine di un punto. */
    private static final String FIELD_LONGITUDE = "longitude";

    /** Nome del campo che contiene i codici delle barriere. */
    private static final String FIELD_BARRIER_CODES = "barrierCodes";

    /** Nome del campo che indica se il provider ha verificato il tratto. */
    private static final String FIELD_PROVIDER_VERIFIED = "providerVerified";

    /** Client HTTP usato per tutte le richieste verso il provider. */
    private final HttpClient client;

    /** URL base del servizio, validato durante la costruzione. */
    private final URI baseUrl;

    /** Credenziale inviata in ogni richiesta e mai esposta all'esterno. */
    private final String token;

    /** Sorgente dei messaggi di errore dell'adattatore. */
    private final MessageSource messages;

    /**
     * Crea l'adattatore con la configurazione esterna e un client HTTP.
     *
     * @param config configurazione tipizzata che contiene endpoint e credenziale
     * @param client client HTTP da usare per le richieste
     * @throws NullPointerException se {@code config} o {@code client} è {@code null}
     * @throws RoutingAdapterException se endpoint o credenziale non sono stati forniti
     */
    public HttpRoutingProvider(AegisConfig config, HttpClient client) {
        Objects.requireNonNull(config, "config");
        this.client = Objects.requireNonNull(client, "client");
        this.messages = MessageSource.at(config.adapterMessageBundleBasePath());
        String configuredBaseUrl = config.routing().baseUrl();
        this.token = config.routing().token();
        if (configuredBaseUrl.isBlank() || token.isBlank()) {
            throw new RoutingAdapterException(KEY_UNCONFIGURED, messages);
        }
        this.baseUrl = baseUrl(configuredBaseUrl);
    }

    @Override
    /**
     * Richiede al provider le rotte tra i due punti geografici.
     *
     * @param origin punto di partenza
     * @param destination punto di arrivo
     * @return rotte candidate restituite dal provider
     * @throws RoutingAdapterException se la richiesta o la risposta non è valida
     */
    public List<RouteCandidate> calculate(GeoPoint origin, GeoPoint destination) {
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(destination, "destination");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestUri(origin, destination))
                .header(HEADER_ACCEPT, MEDIA_TYPE_JSON)
                .header(HEADER_AUTHORIZATION, AUTHORIZATION_SCHEME + token)
                .GET()
                .build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            throw new RoutingAdapterException(KEY_TRANSPORT, messages, cause);
        } catch (IOException cause) {
            throw new RoutingAdapterException(KEY_TRANSPORT, messages, cause);
        }
        if (response.statusCode() != SUCCESS_STATUS) {
            throw new RoutingAdapterException(KEY_TRANSPORT, messages);
        }
        return candidates(response.body());
    }

    /**
     * Valida l'URL base configurato come URI assoluto HTTP o HTTPS senza query.
     *
     * @param value valore letto dalla configurazione esterna
     * @return URI validato
     * @throws RoutingAdapterException se il valore non è un endpoint utilizzabile
     */
    private URI baseUrl(String value) {
        try {
            URI parsed = URI.create(value);
            if (!parsed.isAbsolute()
                    || !SUPPORTED_SCHEMES.contains(parsed.getScheme())
                    || parsed.getRawQuery() != null
                    || parsed.getRawFragment() != null) {
                throw new IllegalArgumentException();
            }
            return parsed;
        } catch (IllegalArgumentException cause) {
            throw new RoutingAdapterException(KEY_UNCONFIGURED, messages, cause);
        }
    }

    /**
     * Compone l'URI della richiesta con le coordinate validate ricevute dal caso d'uso.
     *
     * @param origin posizione di partenza
     * @param destination posizione di arrivo
     * @return URI completo di parametri di query
     */
    private URI requestUri(GeoPoint origin, GeoPoint destination) {
        String query = QUERY_ORIGIN_LATITUDE + "=" + origin.latitude()
                + "&" + QUERY_ORIGIN_LONGITUDE + "=" + origin.longitude()
                + "&" + QUERY_DESTINATION_LATITUDE + "=" + destination.latitude()
                + "&" + QUERY_DESTINATION_LONGITUDE + "=" + destination.longitude();
        return URI.create(baseUrl + "?" + query);
    }

    /**
     * Analizza il corpo della risposta e costruisce i candidati di dominio.
     *
     * @param body corpo della risposta HTTP
     * @return elenco immutabile di percorsi candidati
     * @throws RoutingAdapterException se il documento non rispetta il contratto atteso
     */
    private List<RouteCandidate> candidates(String body) {
        JsonObject root;
        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            root = reader.readObject();
        } catch (JsonException | IllegalStateException cause) {
            throw new RoutingAdapterException(KEY_RESPONSE_CONTRACT, messages, cause);
        }
        JsonArray routes;
        try {
            routes = root.getJsonArray(FIELD_ROUTES);
        } catch (ClassCastException cause) {
            throw new RoutingAdapterException(KEY_RESPONSE_CONTRACT, messages, cause);
        }
        if (routes == null) {
            throw new RoutingAdapterException(KEY_RESPONSE_CONTRACT, messages);
        }
        List<RouteCandidate> result = new ArrayList<>();
        for (JsonValue value : routes) {
            result.add(candidate(value));
        }
        return List.copyOf(result);
    }

    /**
     * Converte un elemento della risposta in un candidato validato.
     *
     * @param value valore JSON che rappresenta un percorso
     * @return candidato di dominio con coordinate e barriere validate
     * @throws RoutingAdapterException se manca un campo obbligatorio o il tipo non è atteso
     */
    private RouteCandidate candidate(JsonValue value) {
        try {
            if (value.getValueType() != JsonValue.ValueType.OBJECT) {
                throw new IllegalArgumentException();
            }
            JsonObject object = value.asJsonObject();
            GeoPoint from = point(object.getJsonObject(FIELD_FROM));
            GeoPoint to = point(object.getJsonObject(FIELD_TO));
            JsonArray barriers = object.getJsonArray(FIELD_BARRIER_CODES);
            if (barriers == null) {
                throw new IllegalArgumentException();
            }
            List<String> codes = new ArrayList<>();
            for (JsonValue barrier : barriers) {
                if (barrier.getValueType() != JsonValue.ValueType.STRING) {
                    throw new IllegalArgumentException();
                }
                codes.add(((JsonString) barrier).getString());
            }
            boolean verified = object.getBoolean(FIELD_PROVIDER_VERIFIED);
            return new RouteCandidate(from, to, codes, verified);
        } catch (JsonException | ClassCastException | IllegalArgumentException | NullPointerException cause) {
            throw new RoutingAdapterException(KEY_RESPONSE_CONTRACT, messages, cause);
        }
    }

    /**
     * Converte un oggetto JSON in una posizione geografica validata.
     *
     * @param object oggetto JSON che contiene latitudine e longitudine
     * @return punto geografico validato
     * @throws IllegalArgumentException se l'oggetto o un campo obbligatorio manca
     */
    private GeoPoint point(JsonObject object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
        JsonNumber latitude = object.getJsonNumber(FIELD_LATITUDE);
        JsonNumber longitude = object.getJsonNumber(FIELD_LONGITUDE);
        if (latitude == null || longitude == null) {
            throw new IllegalArgumentException();
        }
        return new GeoPoint(latitude.doubleValue(), longitude.doubleValue());
    }
}
