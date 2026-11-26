package it.aegis.domain.reports;

import it.aegis.domain.identity.UserId;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Aggregato della segnalazione. Mantiene l'identità interna, il codice pubblico
 * di tracking, l'autore e l'istante di creazione; le transizioni possono cambiare
 * solo lo stato e l'eventuale riferimento al duplicato. Ogni transizione valida
 * lo stato corrente e restituisce l'evento da salvare nella stessa unità di lavoro,
 * così la persistenza non può registrare uno stato senza il relativo evento.
 */
public final class Report {

    /** Identificatore immutabile della segnalazione. */
    private final ReportId id;

    /** Codice di tracking pubblico della segnalazione. */
    private final TrackingCode trackingCode;

    /** Identificatore dell'utente che ha inviato la segnalazione. */
    private final UserId authorId;

    /** Istante di creazione della segnalazione. */
    private final Instant createdAt;

    /** Stato corrente della segnalazione, modificato solo dalle transizioni validate. */
    private ReportStatus status;

    /** Segnalazione di riferimento quando questa è stata marcata come duplicato. */
    private ReportId duplicateOf;

    /**
     * Inizializza una nuova segnalazione nello stato di verifica.
     *
     * @param id identificatore della segnalazione
     * @param trackingCode codice di tracking pubblico
     * @param authorId identificatore dell'utente autore
     * @param createdAt istante di creazione
     * @throws NullPointerException se uno degli argomenti è null
     */
    private Report(ReportId id, TrackingCode trackingCode, UserId authorId, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.trackingCode = Objects.requireNonNull(trackingCode, "trackingCode");
        this.authorId = Objects.requireNonNull(authorId, "authorId");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.status = ReportStatus.IN_VERIFICATION;
    }

    /**
     * Crea una segnalazione nel solo stato iniziale {@link ReportStatus#IN_VERIFICATION}.
     * L'evento iniziale viene creato dal caso d'uso di invio e persistito insieme
     * all'aggregato, non da questo factory.
     *
     * @param id identificatore della segnalazione
     * @param trackingCode codice di tracking pubblico
     * @param authorId identificatore dell'utente autore
     * @param createdAt istante di creazione
     * @return la segnalazione appena creata
     * @throws NullPointerException se uno degli argomenti è null
     */
    public static Report create(ReportId id, TrackingCode trackingCode, UserId authorId, Instant createdAt) {
        return new Report(id, trackingCode, authorId, createdAt);
    }

    /**
     * Restituisce l'identificatore interno stabile della segnalazione.
     * Questo identificatore serve ai riferimenti tra record e non sostituisce
     * il codice di tracking esposto per il tracciamento pubblico.
     *
     * @return identificatore immutabile della segnalazione
     */
    public ReportId id() {
        return id;
    }

    /**
     * Restituisce il codice opaco usato dall'autore per consultare lo stato pubblico.
     * Il codice non contiene l'identificativo dell'autore né dettagli della segnalazione.
     *
     * @return codice di tracking della segnalazione
     */
    public TrackingCode trackingCode() {
        return trackingCode;
    }

    /**
     * Restituisce l'identità dell'account che ha inviato la segnalazione.
     * Il valore resta nel modello interno e non fa parte dello stato pubblico restituito
     * tramite il codice di tracking.
     *
     * @return identificatore dell'utente che ha inviato la segnalazione
     */
    public UserId authorId() {
        return authorId;
    }

    /**
     * Restituisce l'istante originario di creazione assegnato dal caso d'uso di invio.
     * Le decisioni di moderazione non modificano questo valore.
     *
     * @return istante in cui la segnalazione è stata creata
     */
    public Instant createdAt() {
        return createdAt;
    }

    /**
     * Restituisce lo stato corrente raggiunto attraverso una transizione validata.
     * Per cambiare lo stato usare {@link #transition} o {@link #transitionToDuplicate};
     * non esiste un setter che permetta di aggirare la macchina a stati.
     *
     * @return stato corrente
     */
    public ReportStatus status() {
        return status;
    }

    /**
     * Restituisce l'identificativo della segnalazione originale quando questa
     * segnalazione è nello stato {@link ReportStatus#MARKED_DUPLICATE}.
     * Negli altri stati il riferimento è assente.
     *
     * @return riferimento marcato come duplicato, o un Optional vuoto se la segnalazione non è un duplicato
     */
    public Optional<ReportId> duplicateOf() {
        return Optional.ofNullable(duplicateOf);
    }

    /**
     * Applica una transizione semplice, registrando l'evento corrispondente allo stato raggiunto.
     *
     * @param next stato da raggiungere
     * @param actorId identificatore dell'utente che esegue la transizione
     * @param reason motivazione della transizione
     * @param occurredAt istante in cui la transizione avviene
     * @return evento immutabile da persistere con la transizione
     * @throws NullPointerException se uno degli argomenti è null
     * @throws IllegalArgumentException se si tenta di raggiungere lo stato di duplicato con questo metodo
     * @throws IllegalStateException se la transizione non è ammessa dallo stato corrente
     */
    public ReportEvent transition(ReportStatus next, UserId actorId, String reason, Instant occurredAt) {
        requireTransitionInputs(next, actorId, reason, occurredAt);
        if (next == ReportStatus.MARKED_DUPLICATE) {
            throw new IllegalArgumentException("use transitionToDuplicate for MARKED_DUPLICATE");
        }
        return apply(ReportEventType.valueOf(next.name()), next, actorId, reason, occurredAt);
    }

    /**
     * Marca la segnalazione come duplicato, registrando il riferimento e l'evento corrispondente.
     *
     * @param reference segnalazione originale di cui questa è un duplicato
     * @param actorId identificatore dell'utente che esegue la transizione
     * @param reason motivazione della transizione
     * @param occurredAt istante in cui la transizione avviene
     * @return evento immutabile da persistere con la transizione
     * @throws NullPointerException se uno degli argomenti è null
     * @throws IllegalStateException se lo stato corrente non ammette la marcatura come duplicato
     */
    public ReportEvent transitionToDuplicate(
            ReportId reference, UserId actorId, String reason, Instant occurredAt) {
        Objects.requireNonNull(reference, "reference");
        requireTransitionInputs(ReportStatus.MARKED_DUPLICATE, actorId, reason, occurredAt);
        ReportEvent event = apply(
                ReportEventType.MARKED_DUPLICATE, ReportStatus.MARKED_DUPLICATE,
                actorId, reason, occurredAt);
        this.duplicateOf = reference;
        return event;
    }

    /**
     * Verifica gli argomenti comuni a tutte le transizioni e l'ammissibilità rispetto allo stato corrente.
     *
     * @param next stato da raggiungere
     * @param actorId identificatore dell'utente che esegue la transizione
     * @param reason motivazione della transizione
     * @param occurredAt istante in cui la transizione avviene
     * @throws NullPointerException se uno degli argomenti è null
     * @throws IllegalStateException se la transizione non è ammessa dallo stato corrente
     */
    private void requireTransitionInputs(
            ReportStatus next, UserId actorId, String reason, Instant occurredAt) {
        Objects.requireNonNull(next, "next");
        Objects.requireNonNull(actorId, "actorId");
        Objects.requireNonNull(reason, "reason");
        Objects.requireNonNull(occurredAt, "occurredAt");
        if (!status.allows(next)) {
            throw new IllegalStateException("transition not allowed: " + status + " -> " + next);
        }
    }

    /**
     * Crea l'evento di transizione e aggiorna lo stato corrente dell'aggregato.
     *
     * @param type tipo di evento da registrare
     * @param next stato da raggiungere
     * @param actorId identificatore dell'utente che esegue la transizione
     * @param reason motivazione della transizione
     * @param occurredAt istante in cui la transizione avviene
     * @return evento immutabile corrispondente alla transizione applicata
     * @throws NullPointerException se uno degli argomenti è null
     */
    private ReportEvent apply(
            ReportEventType type, ReportStatus next, UserId actorId, String reason, Instant occurredAt) {
        ReportEvent event = new ReportEvent(type, next, actorId, reason, occurredAt);
        this.status = next;
        return event;
    }
}
