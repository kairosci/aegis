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

/** Risorsa HTTP delle segnalazioni. Valida gli input prima di invocare i casi d'uso. */
@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
public final class ReportResource {

    private final ReportSubmission submissions;
    private final TrackReport tracking;

    @Inject
    public ReportResource(ReportSubmission submissions, TrackReport tracking) {
        this.submissions = submissions;
        this.tracking = tracking;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
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
