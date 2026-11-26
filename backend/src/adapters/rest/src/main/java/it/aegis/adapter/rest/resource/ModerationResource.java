package it.aegis.adapter.rest.resource;

import it.aegis.adapter.rest.RestContract;
import it.aegis.adapter.rest.dto.ModerationDecisionRequest;
import it.aegis.adapter.rest.error.ApiErrorType;
import it.aegis.adapter.rest.error.ApiException;
import it.aegis.application.usecase.ModerateReport;
import it.aegis.application.usecase.ModerateReportCommand;
import it.aegis.domain.identity.Role;
import it.aegis.domain.identity.UserId;
import it.aegis.domain.reports.ReportId;
import it.aegis.domain.reports.ReportStatus;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.Optional;
import java.util.UUID;

/**
 * Risorsa HTTP delle decisioni di moderazione delle segnalazioni.
 *
 * <p>La risorsa verifica il ruolo amministratore sul server, converte i dati
 * grezzi della richiesta nei tipi del dominio e invoca il caso d'uso di
 * moderazione. Il metodo che modifica lo stato è demarcato come transazionale.</p>
 */
@Path(RestContract.REPORTS_PATH)
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class ModerationResource {

    /** Caso d'uso che applica la decisione di moderazione. */
    private final ModerateReport moderateReport;

    /**
     * Crea l'istanza usata dal proxy CDI, senza dipendenze applicative.
     *
     * <p>Il costruttore annotato fornisce il caso d'uso quando il container
     * crea la risorsa per servire una richiesta.</p>
     */
    public ModerationResource() {
        this.moderateReport = null;
    }

    /**
     * Crea la risorsa con il caso d'uso necessario.
     *
     * @param moderateReport caso d'uso che applica la decisione di moderazione
     */
    @Inject
    public ModerationResource(ModerateReport moderateReport) {
        this.moderateReport = moderateReport;
    }

    /**
     * Applica una decisione di moderazione alla segnalazione indicata.
     *
     * <p>La richiesta viene validata al confine, il ruolo amministratore viene
     * richiesto dal contesto di sicurezza e la transizione è eseguita dal caso
     * d'uso. Le eccezioni di autorizzazione, validazione e stato sono
     * classificate come errori pubblici dal mapper.</p>
     *
     * @param id identificativo della segnalazione nel percorso della richiesta
     * @param request corpo della richiesta con decisione, motivazione e riferimento opzionale
     * @param securityContext contesto di sicurezza da cui ricavare l'amministratore
     * @return risposta ok senza corpo dopo la decisione applicata
     * @throws ApiException se la richiesta non è valida o l'amministratore non è autorizzato
     */
    @POST
    @Path(RestContract.DECISION_PATH)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response decide(
            @PathParam(RestContract.ID_PARAM) String id,
            ModerationDecisionRequest request,
            @Context SecurityContext securityContext) {
        UserId adminId = requireAdministrator(securityContext);
        if (request == null) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
        ReportId reportId = parseReportId(id);
        ReportStatus decision = parseDecision(request.decision());
        Optional<ReportId> duplicateOf = parseDuplicate(request.duplicateOf());
        if (decision == ReportStatus.MARKED_DUPLICATE && duplicateOf.isEmpty()) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
        ModerateReportCommand command = new ModerateReportCommand(
                reportId, decision, request.reason(), duplicateOf);
        try {
            moderateReport.moderate(command, adminId);
        } catch (SecurityException cause) {
            throw ApiException.of(ApiErrorType.FORBIDDEN);
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        } catch (IllegalStateException cause) {
            throw ApiException.of(ApiErrorType.CONFLICT);
        }
        return Response.ok().build();
    }

    /**
     * Verifica che il contesto di sicurezza identifichi un amministratore.
     *
     * <p>L'assenza di autenticazione è distinta dal ruolo non autorizzato. Il
     * nome del principal deve essere un identificativo valido; in caso
     * contrario l'identità non viene accettata.</p>
     *
     * @param context contesto di sicurezza della richiesta
     * @return identificativo dell'amministratore autenticato
     * @throws ApiException se manca un principal, se il ruolo non è amministratore
     *         o se il nome del principal non è un identificativo valido
     */
    private UserId requireAdministrator(SecurityContext context) {
        if (context == null || context.getUserPrincipal() == null) {
            throw ApiException.of(ApiErrorType.UNAUTHORIZED);
        }
        if (!context.isUserInRole(Role.ADMINISTRATOR.name())) {
            throw ApiException.of(ApiErrorType.FORBIDDEN);
        }
        try {
            return new UserId(UUID.fromString(context.getUserPrincipal().getName()));
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.FORBIDDEN);
        }
    }

    /**
     * Converte l'identificativo della segnalazione ricevuto nel percorso.
     *
     * @param value rappresentazione testuale dell'identificativo
     * @return identificativo del dominio
     * @throws ApiException se il valore non è un identificativo valido
     */
    private ReportId parseReportId(String value) {
        try {
            return new ReportId(UUID.fromString(value));
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
    }

    /**
     * Converte il nome della decisione ricevuto nel corpo della richiesta.
     *
     * @param value nome della decisione di moderazione
     * @return stato risultante della decisione
     * @throws ApiException se il nome non corrisponde a una decisione nota
     */
    private ReportStatus parseDecision(String value) {
        try {
            return ReportStatus.valueOf(value.trim());
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
    }

    /**
     * Converte l'eventuale riferimento alla segnalazione originale.
     *
     * <p>Un valore assente o costituito solo da spazi è interpretato come
     * assenza di riferimento. Un valore presente deve essere un identificativo
     * valido.</p>
     *
     * @param value rappresentazione testuale del riferimento, o {@code null}
     * @return riferimento opzionale alla segnalazione originale
     * @throws ApiException se il riferimento presente non è un identificativo valido
     */
    private Optional<ReportId> parseDuplicate(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(new ReportId(UUID.fromString(value.trim())));
        } catch (IllegalArgumentException cause) {
            throw ApiException.of(ApiErrorType.BAD_REQUEST);
        }
    }
}
