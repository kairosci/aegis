package it.aegis.adapter.storage;

import it.aegis.application.port.StoragePort;
import it.aegis.application.port.StorageReference;
import it.aegis.config.AegisConfig;
import it.aegis.config.MessageSource;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Storage privato che usa l'API HTTP di un servizio compatibile con S3.
 *
 * <p>La chiave dell'oggetto è derivata dal prefisso configurato,
 * dall'identificativo della prenotazione e dall'impronta del contenuto. Il nome
 * del file fornito dal client serve solo a dichiarare il tipo di contenuto,
 * viene confrontato con l'elenco ammesso e non è mai registrato né loggato.
 * Nessuna chiamata rende pubblico l'oggetto.</p>
 */
public final class S3CompatibleStorageAdapter implements StoragePort {

    /** Chiave del messaggio per configurazione assente. */
    private static final String KEY_UNCONFIGURED = "storage.unconfigured";

    /** Chiave del messaggio per identificativo di prenotazione non valido. */
    private static final String KEY_INVALID_SUBMISSION = "storage.invalid-submission";

    /** Chiave del messaggio per estensione non riconosciuta. */
    private static final String KEY_UNSUPPORTED_CONTENT_TYPE = "storage.content-type-unsupported";

    /** Chiave del messaggio per tipo di contenuto non ammesso. */
    private static final String KEY_NOT_ALLOWED_CONTENT_TYPE = "storage.content-type-not-allowed";

    /** Chiave del messaggio per errore di trasporto o stato HTTP non previsto. */
    private static final String KEY_TRANSPORT = "storage.transport-failure";

    /** Chiave del messaggio per riferimento non valido. */
    private static final String KEY_INVALID_REFERENCE = "storage.invalid-reference";

    /** Chiave del messaggio per algoritmo di impronta non disponibile. */
    private static final String KEY_DIGEST_UNAVAILABLE = "storage.digest-unavailable";

    /** Schemi URI ammessi per l'endpoint configurato. */
    private static final Set<String> SUPPORTED_SCHEMES = Set.of("http", "https");

    /** Stati HTTP accettati per la scrittura di un oggetto. */
    private static final Set<Integer> ACCEPTED_WRITE_STATUSES = Set.of(200);

    /** Stati HTTP accettati per la rimozione di un oggetto. */
    private static final Set<Integer> ACCEPTED_DELETE_STATUSES = Set.of(200, 204);

    /** Nome dell'header che dichiara il tipo di contenuto. */
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    /** Nome dell'header che impone la privacy dell'oggetto. */
    private static final String HEADER_ACL = "x-amz-acl";

    /** Valore dell'header che impone la privacy dell'oggetto. */
    private static final String PRIVATE_ACL = "private";

    /** Nome dell'header che trasporta la credenziale. */
    private static final String HEADER_AUTHORIZATION = "Authorization";

    /** Prefisso dello schema di autorizzazione. */
    private static final String AUTHORIZATION_SCHEME = "Bearer ";

    /** Separatore dell'estensione in un nome di file. */
    private static final char EXTENSION_SEPARATOR = '.';

    /** Separatore dei segmenti in una chiave di oggetto. */
    private static final char KEY_SEPARATOR = '/';

    /** Algoritmo usato per derivare la parte variabile della chiave. */
    private static final String DIGEST_ALGORITHM = "SHA-256";

    /** Forma dei segmenti ammessi in un identificativo di prenotazione. */
    private static final Pattern SAFE_SEGMENT = Pattern.compile("[A-Za-z0-9_-]+");

    /** Forma della chiave di oggetto prodotta dall'adattatore. */
    private static final Pattern SAFE_KEY = Pattern.compile("[A-Za-z0-9_-]+(?:/[A-Za-z0-9_-]+)*");

    /** Associazione fissa tra estensione e tipo di contenuto dichiarato. */
    private static final Map<String, String> CONTENT_TYPES_BY_EXTENSION = Map.of(
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "webp", "image/webp");

    /** Client HTTP usato per tutte le richieste verso lo storage. */
    private final HttpClient client;

    /** Endpoint del servizio, validato durante la costruzione. */
    private final URI endpoint;

    /** Bucket privato che riceve gli oggetti. */
    private final String bucket;

    /** Prefisso applicato a ogni chiave prodotta. */
    private final String objectPrefix;

    /** Credenziale inviata in ogni richiesta e mai esposta all'esterno. */
    private final String token;

    /** Tipi di contenuto ammessi dalla configurazione. */
    private final Set<String> allowedContentTypes;

    /** Sorgente dei messaggi di errore dell'adattatore. */
    private final MessageSource messages;

    /**
     * Crea l'adattatore con la configurazione esterna e un client HTTP.
     *
     * @param config configurazione tipizzata che contiene i parametri dello storage
     * @param client client HTTP da usare per le richieste
     * @throws NullPointerException se {@code config} o {@code client} è {@code null}
     * @throws StorageAdapterException se endpoint, bucket, prefisso, credenziale o elenco dei tipi non sono utilizzabili
     */
    public S3CompatibleStorageAdapter(AegisConfig config, HttpClient client) {
        Objects.requireNonNull(config, "config");
        this.client = Objects.requireNonNull(client, "client");
        this.messages = MessageSource.at(config.adapterMessageBundleBasePath());
        AegisConfig.StorageSettings settings = config.storage();
        this.bucket = settings.bucket();
        this.objectPrefix = settings.objectPrefix();
        this.token = settings.token();
        this.allowedContentTypes = settings.allowedContentTypes();
        if (settings.endpoint().isBlank() || bucket.isBlank() || objectPrefix.isBlank()
                || token.isBlank() || allowedContentTypes.isEmpty()
                || !SAFE_KEY.matcher(objectPrefix).matches()) {
            throw new StorageAdapterException(KEY_UNCONFIGURED, messages);
        }
        this.endpoint = endpoint(settings.endpoint());
    }

    @Override
    public StorageReference stage(byte[] content, String fileName, String submissionId) {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(fileName, "fileName");
        String submission = submission(submissionId);
        String contentType = declaredContentType(fileName);
        byte[] copy = content.clone();
        String key = objectPrefix + KEY_SEPARATOR + submission + KEY_SEPARATOR + digest(copy);
        StorageReference reference = new StorageReference(bucket, key);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(objectUri(key))
                .header(HEADER_CONTENT_TYPE, contentType)
                .header(HEADER_ACL, PRIVATE_ACL)
                .header(HEADER_AUTHORIZATION, AUTHORIZATION_SCHEME + token)
                .PUT(HttpRequest.BodyPublishers.ofByteArray(copy))
                .build();
        send(request, ACCEPTED_WRITE_STATUSES);
        return reference;
    }

    @Override
    public void delete(StorageReference reference) {
        Objects.requireNonNull(reference, "reference");
        if (!bucket.equals(reference.bucket()) || !SAFE_KEY.matcher(reference.objectKey()).matches()) {
            throw new StorageAdapterException(KEY_INVALID_REFERENCE, messages);
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(objectUri(reference.objectKey()))
                .header(HEADER_AUTHORIZATION, AUTHORIZATION_SCHEME + token)
                .DELETE()
                .build();
        send(request, ACCEPTED_DELETE_STATUSES);
    }

    /**
     * Valida l'endpoint configurato come URI assoluto HTTP o HTTPS senza query.
     *
     * @param value valore letto dalla configurazione esterna
     * @return URI validato
     * @throws StorageAdapterException se il valore non è un endpoint utilizzabile
     */
    private URI endpoint(String value) {
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
            throw new StorageAdapterException(KEY_UNCONFIGURED, messages, cause);
        }
    }

    /**
     * Verifica che l'identificativo della prenotazione sia un segmento di chiave sicuro.
     *
     * @param value identificativo ricevuto dal caso d'uso
     * @return identificativo validato
     * @throws StorageAdapterException se il valore è vuoto o contiene caratteri non ammessi
     */
    private String submission(String value) {
        if (value == null || !SAFE_SEGMENT.matcher(value).matches()) {
            throw new StorageAdapterException(KEY_INVALID_SUBMISSION, messages);
        }
        return value;
    }

    /**
     * Determina il tipo di contenuto dichiarato dal nome del file e lo confronta
     * con l'elenco ammesso dalla configurazione.
     *
     * @param fileName nome dichiarato dal client, usato solo per l'estensione
     * @return tipo di contenuto ammesso
     * @throws StorageAdapterException se l'estensione non è nota o il tipo non è ammesso
     */
    private String declaredContentType(String fileName) {
        int separator = fileName.lastIndexOf(EXTENSION_SEPARATOR);
        if (separator < 0 || separator == fileName.length() - 1) {
            throw new StorageAdapterException(KEY_UNSUPPORTED_CONTENT_TYPE, messages);
        }
        String extension = fileName.substring(separator + 1).toLowerCase(Locale.ROOT);
        String declared = CONTENT_TYPES_BY_EXTENSION.get(extension);
        if (declared == null) {
            throw new StorageAdapterException(KEY_UNSUPPORTED_CONTENT_TYPE, messages);
        }
        if (!allowedContentTypes.contains(declared)) {
            throw new StorageAdapterException(KEY_NOT_ALLOWED_CONTENT_TYPE, messages);
        }
        return declared;
    }

    /**
     * Calcola l'impronta del contenuto usata come parte variabile della chiave.
     *
     * @param content copia dei byte da conservare
     * @return impronta esadecimale del contenuto
     * @throws StorageAdapterException se l'algoritmo di impronta non è disponibile
     */
    private String digest(byte[] content) {
        try {
            byte[] hash = MessageDigest.getInstance(DIGEST_ALGORITHM).digest(content);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException cause) {
            throw new StorageAdapterException(KEY_DIGEST_UNAVAILABLE, messages, cause);
        }
    }

    /**
     * Compone l'URI assoluto di un oggetto a partire da endpoint, bucket e chiave validati.
     *
     * @param key chiave dell'oggetto nella forma ammessa
     * @return URI della richiesta
     */
    private URI objectUri(String key) {
        String base = endpoint.toString();
        while (base.endsWith(String.valueOf(KEY_SEPARATOR))) {
            base = base.substring(0, base.length() - 1);
        }
        return URI.create(base + KEY_SEPARATOR + bucket + KEY_SEPARATOR + key);
    }

    /**
     * Invia una richiesta e verifica che lo stato rientri in quelli accettati.
     *
     * @param request richiesta già composta
     * @param acceptedStatuses insieme degli stati HTTP accettati
     * @throws StorageAdapterException se il trasporto fallisce o lo stato non è accettato
     */
    private void send(HttpRequest request, Set<Integer> acceptedStatuses) {
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            if (!acceptedStatuses.contains(response.statusCode())) {
                throw new StorageAdapterException(KEY_TRANSPORT, messages);
            }
        } catch (InterruptedException cause) {
            Thread.currentThread().interrupt();
            throw new StorageAdapterException(KEY_TRANSPORT, messages, cause);
        } catch (IOException cause) {
            throw new StorageAdapterException(KEY_TRANSPORT, messages, cause);
        }
    }
}
