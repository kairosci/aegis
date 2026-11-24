package it.aegis.application.usecase;

import it.aegis.application.port.AuthorizationPolicy;
import it.aegis.application.port.Fingerprint;
import it.aegis.application.port.ReportRepository;
import it.aegis.application.port.ReservationOutcome;
import it.aegis.application.port.SubmissionReservationPort;
import it.aegis.application.port.TrackingCodeGenerator;
import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.Report;
import it.aegis.domain.reports.ReportEvent;
import it.aegis.domain.reports.ReportEventType;
import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.ReportStatus;
import it.aegis.domain.reports.TrackingCode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Caso d'uso UC-03. La prenotazione durevole precede ogni effetto esterno.
 * Un retry con la stessa chiave e la stessa impronta restituisce il codice già
 * assegnato, senza generare una seconda segnalazione.
 */
public final class ReportSubmission {

    /** Separatore canonico tra categoria e descrizione nella stringa da improntare. */
    private static final String CANONICAL_SEPARATOR = "\u001f";

    /** Prenotazione durevole che serializza i retry prima degli effetti esterni. */
    private final SubmissionReservationPort reservations;
    /** Porta di persistenza dell'aggregato segnalazione e del suo evento. */
    private final ReportRepository reports;
    /** Politica di autorizzazione lato server per l'invio. */
    private final AuthorizationPolicy policy;
    /** Generatore della credenziale di tracking della segnalazione. */
    private final TrackingCodeGenerator codes;
    /** Orologio iniettato per datare la creazione e l'evento iniziale. */
    private final Clock clock;

    /**
     * Crea il caso d'uso con le porte e l'orologio da utilizzare.
     *
     * @param reservations porta di prenotazione durevole dell'invio
     * @param reports porta di persistenza dell'aggregato
     * @param policy politica di autorizzazione lato server
     * @param codes generatore del codice di tracking
     * @param clock orologio usato per datare creazione ed evento
     * @throws NullPointerException se una delle dipendenze è {@code null}
     */
    public ReportSubmission(
            SubmissionReservationPort reservations,
            ReportRepository reports,
            AuthorizationPolicy policy,
            TrackingCodeGenerator codes,
            Clock clock) {
        this.reservations = Objects.requireNonNull(reservations, "reservations");
        this.reports = Objects.requireNonNull(reports, "reports");
        this.policy = Objects.requireNonNull(policy, "policy");
        this.codes = Objects.requireNonNull(codes, "codes");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    /**
     * Invia una segnalazione dopo aver verificato l'autorizzazione e ottenuto
     * la prenotazione durevole. Se la prenotazione risulta già completata, viene
     * restituito il codice assegnato in precedenza.
     *
     * @param command dati validati dell'invio
     * @param authorId autore autenticato della segnalazione
     * @return codice di tracking della segnalazione creata o già prenotata
     * @throws NullPointerException se {@code command} o {@code authorId} è {@code null}
     * @throws SecurityException se l'autore non è autorizzato a inviare
     * @throws IllegalArgumentException se la chiave è riutilizzata con un contenuto diverso
     * @throws IllegalStateException se un'altra richiesta con la stessa chiave è in corso
     */
    public TrackingCode submit(SubmitReportCommand command, UserId authorId) {
        Objects.requireNonNull(command, "command");
        Objects.requireNonNull(authorId, "authorId");
        if (!policy.canSubmit(authorId)) {
            throw new SecurityException("submission not authorized");
        }

        Fingerprint fingerprint = fingerprintOf(command);
        ReservationOutcome outcome = reservations.reserve(command.key(), fingerprint, authorId);

        if (outcome instanceof ReservationOutcome.Completed completed) {
            return completed.code();
        }
        if (outcome instanceof ReservationOutcome.InProgress) {
            throw new IllegalStateException("submission already in progress");
        }
        if (outcome instanceof ReservationOutcome.Conflict) {
            throw new IllegalArgumentException("submission key reused with a different payload");
        }

        TrackingCode code = codes.generate();
        Instant now = clock.instant();
        Report report = Report.create(
                new ReportId(UUID.randomUUID()), code, authorId, now);
        ReportEvent event = new ReportEvent(
                ReportEventType.CREATED, ReportStatus.IN_VERIFICATION,
                authorId, ReportEventType.CREATED.name().toLowerCase(Locale.ROOT), now);
        reports.save(report, event);
        reservations.complete(command.key(), code);
        return code;
    }

    /**
     * Calcola l'impronta canonica dell'invio concatenando categoria e descrizione
     * con il separatore riservato, prima di qualsiasi effetto esterno.
     *
     * @param command dati validati dell'invio
     * @return impronta esadecimale del contenuto rilevante dell'invio
     * @throws IllegalStateException se la piattaforma non fornisce l'algoritmo richiesto
     */
    private Fingerprint fingerprintOf(SubmitReportCommand command) {
        String canonical = command.category() + CANONICAL_SEPARATOR + command.description();
        try {
            byte[] digest = MessageDigest.getInstance(Fingerprint.algorithm())
                    .digest(canonical.getBytes(StandardCharsets.UTF_8));
            return new Fingerprint(HexFormat.of().formatHex(digest));
        } catch (NoSuchAlgorithmException cause) {
            throw new IllegalStateException(Fingerprint.algorithm() + " is required by the platform", cause);
        }
    }
}
