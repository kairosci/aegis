package it.aegis.adapter.rest.resource;

import it.aegis.adapter.rest.dto.CreateReportRequest;
import it.aegis.adapter.rest.dto.CreateReportResponse;
import it.aegis.adapter.rest.dto.PublicReportStatusResponse;
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
import jakarta.transaction.Transactional;
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

/** Risorsa HTTP delle segnalazioni, con scope di richiesta CDI e confine transazionale sulla creazione. */
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ReportResource {

    /** Caso d'uso per l'invio di una nuova segnalazione. */
    private final ReportSubmission submissions;

    /** Caso d'uso per il tracking pubblico di una segnalazione. */
    private final TrackReport tracking;

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
     * tradotte in risposte forbidden, bad request e conflict. Questo metodo è
     * il confine transazionale dell'adapter.</p>
     *
     * @param request corpo della richiesta con chiave, categoria e descrizione
     * @param securityContext contesto di sicurezza da cui ricavare l'autore
     * @return risposta created con il codice di tracking destinato all'autore
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(
            CreateReportRequest request,
            @Context SecurityContext securityContext) {
        UserId author = requireAuthor(securityContext);
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
            return Response.status(Response.Status.FORBIDDEN).build();
        } catch (IllegalArgumentException cause) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (IllegalStateException cause) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    /**
     * Restituisce lo stato pubblico della segnalazione indicata nel percorso.
     *
     * <p>Un codice non valido o senza corrispondenza produce una risposta non
     * trovata, senza rivelare dati dell'autore.</p>
     *
     * @param code codice di tracking presente nel percorso della richiesta
     * @return risposta con lo stato pubblico, oppure non trovata
     */
    @GET
    @Path("{code}")
    public Response track(@PathParam("code") String code) {
        try {
            PublicReportStatus status = tracking.track(new TrackingCode(code));
            return Response.ok(new PublicReportStatusResponse(
                    status.code().value(), status.status().name())).build();
        } catch (IllegalArgumentException cause) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Verifica che il contesto di sicurezza identifichi un utente registrato.
     *
     * <p>Il nome del principal deve essere un identificativo valido; in caso
     * contrario l'identità non viene accettata.</p>
     *
     * @param context contesto di sicurezza della richiesta
     * @return identificativo dell'utente autenticato
     * @throws SecurityException se manca un principal registrato o il suo nome non è un identificativo valido
     */
    private UserId requireAuthor(SecurityContext context) {
        if (context.getUserPrincipal() == null
                || !context.isUserInRole(Role.REGISTERED_USER.name())) {
            throw new SecurityException("registered user required");
        }
        try {
            return new UserId(UUID.fromString(context.getUserPrincipal().getName()));
        } catch (IllegalArgumentException cause) {
            throw new SecurityException("invalid authenticated principal");
        }
    }
}
