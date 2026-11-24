package it.aegis.adapter.rest.resource;

import it.aegis.adapter.rest.RestContract;
import it.aegis.adapter.rest.dto.CreateReportRequest;
import it.aegis.adapter.rest.dto.CreateReportResponse;
import it.aegis.adapter.rest.dto.PublicReportStatusResponse;
import it.aegis.adapter.rest.error.ApiErrorType;
import it.aegis.adapter.rest.error.ApiException;
import it.aegis.application.port.SubmissionKey;
import it.aegis.application.usecase.PublicReportStatus;
import it.aegis.application.usecase.ReportSubmission;
import it.aegis.application.usecase.SubmitReportCommand;
import it.aegis.application.usecase.TrackReport;
import it.aegis.domain.identity.Role;
import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.TrackingCode;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.UUID;

/**
 * Risorsa HTTP delle segnalazioni.
 *
 * <p>La risorsa valida gli input prima di invocare i casi d'uso e traduce gli
 * esiti in risposte HTTP uniformi. Non contiene regole di dominio né testi di
 * errore destinati all'utente.</p>
 */
@Path(RestContract.REPORTS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ReportResource {

    /** Caso d'uso per l'invio di una nuova segnalazione. */
    private final ReportSubmission submissions;

    /** Caso d'uso per il tracking pubblico di una segnalazione. */
    private final TrackReport tracking;

    /**
     * Crea l'istanza usata dal proxy CDI, senza dipendenze applicative.
     *
     * <p>Il costruttore annotato fornisce i casi d'uso quando il container
     * crea la risorsa per servire una richiesta.</p>
     */
    public ReportResource() {
        this.submissions = null;
        this.tracking = null;
    }

    /**
     * Crea la risorsa con i casi d'uso necessari.
     *
     * @param submissions caso d'uso per l'invio di una nuova segnalazione
     * @param tracking caso d'uso per il tracking pubblico di una segnalazione
     */
    @Inject
    public ReportResource(ReportSubmission submissions, TrackReport tracking) {
        this.submissions = submissions;
        this.tracking = tracking;
    }

    /**
     * Crea una segnalazione per l'utente autenticato.
     *
     * <p>La richiesta viene tradotta in un comando prima di invocare il caso
     * d'uso. Le eccezioni di autorizzazione, validazione e conflitto sono
     * classificate come errori pubblici dal mapper.</p>
     *
     * @param request corpo della richiesta con chiave, categoria e descrizione
     * @param securityContext contesto di sicurezza da cui ricavare l'autore
     * @return risposta created con il codice di tracking destinato all'autore
     * @throws ApiException se la richiesta non è valida o l'autore non è autorizzato
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(
            CreateReportRequest request,
            @Context SecurityContext securityContext) {
        UserId author = requireAuthor(securityContext);
        if (request == null) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
        try {
            SubmitReportCommand command = new SubmitReportCommand(
                    new SubmissionKey(request.key()),
                    request.category(),
                    request.description());
            TrackingCode code = submissions.submit(command, author);
            return Response.status(Response.Status.CREATED)
                    .entity(new CreateReportResponse(code.value()))
                    .build();
        } catch (SecurityException cause) {
            throw ApiException.of(ApiErrorType.FORBIDDEN);
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        } catch (IllegalStateException cause) {
            throw ApiException.of(ApiErrorType.CONFLICT);
        }
    }

    /**
     * Restituisce lo stato pubblico della segnalazione indicata nel percorso.
     *
     * <p>Un codice non valido o senza corrispondenza produce un errore classificato
     * come risorsa non trovata, senza rivelare dati dell'autore.</p>
     *
     * @param code codice di tracking presente nel percorso della richiesta
     * @return risposta con lo stato pubblico
     * @throws ApiException se il codice non è valido o non è noto
     */
    @GET
    @Path(RestContract.CODE_PATH)
    public Response track(@PathParam(RestContract.CODE_PARAM) String code) {
        try {
            PublicReportStatus status = tracking.track(new TrackingCode(code));
            return Response.ok(new PublicReportStatusResponse(
                    status.code().value(), status.status().name())).build();
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.NOT_FOUND);
        }
    }

    /**
     * Verifica che il contesto di sicurezza identifichi un utente registrato.
     *
     * <p>L'assenza di autenticazione è distinta dal ruolo non autorizzato. Il
     * nome del principal deve essere un identificativo valido; in caso
     * contrario l'identità non viene accettata.</p>
     *
     * @param context contesto di sicurezza della richiesta
     * @return identificativo dell'utente autenticato
     * @throws ApiException se manca un principal, se il ruolo non è registrato
     *         o se il nome del principal non è un identificativo valido
     */
    private UserId requireAuthor(SecurityContext context) {
        if (context == null || context.getUserPrincipal() == null) {
            throw ApiException.of(ApiErrorType.UNAUTHORIZED);
        }
        if (!context.isUserInRole(Role.REGISTERED_USER.name())) {
            throw ApiException.of(ApiErrorType.FORBIDDEN);
        }
        try {
            return new UserId(UUID.fromString(context.getUserPrincipal().getName()));
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.FORBIDDEN);
        }
    }
}
