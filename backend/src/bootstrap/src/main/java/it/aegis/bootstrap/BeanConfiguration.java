package it.aegis.bootstrap;

import it.aegis.adapter.inmemory.InMemoryAuthorizationPolicy;
import it.aegis.adapter.inmemory.InMemoryNotificationGateway;
import it.aegis.adapter.inmemory.InMemoryPlaceRepository;
import it.aegis.adapter.inmemory.InMemoryReportRepository;
import it.aegis.adapter.inmemory.InMemorySubmissionReservations;
import it.aegis.adapter.inmemory.RecordingNotificationChannel;
import it.aegis.adapter.inmemory.SecureRandomTrackingCodeGenerator;
import it.aegis.application.port.AuthorizationPolicy;
import it.aegis.application.port.NotificationChannel;
import it.aegis.application.port.NotificationOutbox;
import it.aegis.application.port.NotificationPort;
import it.aegis.application.port.PlaceRepository;
import it.aegis.application.port.ReportRepository;
import it.aegis.application.port.RoutingProvider;
import it.aegis.application.port.SubmissionReservationPort;
import it.aegis.application.port.TrackingCodeGenerator;
import it.aegis.application.usecase.FindPlace;
import it.aegis.application.usecase.ModerateReport;
import it.aegis.application.usecase.PlanRoute;
import it.aegis.application.usecase.ReportSubmission;
import it.aegis.application.usecase.SearchPlaces;
import it.aegis.application.usecase.TrackReport;
import it.aegis.config.AegisConfig;
import it.aegis.config.ConfigLoader;
import it.aegis.config.MessageSource;
import it.aegis.domain.accessibility.FreshnessWindow;
import it.aegis.domain.identity.UserId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.Typed;
import java.time.Clock;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Configurazione CDI dell'assemblaggio applicativo.
 *
 * <p>Produce la configurazione tipizzata, la sorgente dei messaggi, le porte
 * con implementazioni in memoria, l'orologio e i casi d'uso. Le dipendenze dei
 * casi d'uso sono passate al costruttore, senza iniezione di campi nelle classi
 * applicative. Il provider di routing resta una scelta aperta e la porta
 * prodotta segnala che non è ancora collegata.
 */
public class BeanConfiguration {

    /** Messaggio diagnostico usato quando il provider di routing non è collegato. */
    private static final String ROUTING_UNAVAILABLE = "routing provider not configured";

    /**
     * Crea la configurazione CDI senza stato proprio.
     */
    public BeanConfiguration() {
    }

    /**
     * Produce la configurazione tipizzata letta dalla directory dati esterna.
     *
     * @return configurazione caricata senza valori predefiniti silenziosi
     */
    @Produces
    @ApplicationScoped
    public AegisConfig config() {
        return ConfigLoader.load();
    }

    /**
     * Produce la sorgente dei messaggi localizzati dalla configurazione.
     *
     * @param config configurazione tipizzata che indica la directory e il bundle
     * @return sorgente che risolve i testi dalla directory dati
     */
    @Produces
    @Dependent
    public MessageSource messages(AegisConfig config) {
        return MessageSource.from(config);
    }

    /**
     * Produce il repository in memoria delle segnalazioni.
     *
     * @return archivio in memoria usato nello sviluppo locale
     */
    @Produces
    @ApplicationScoped
    public ReportRepository reportRepository() {
        return new InMemoryReportRepository();
    }

    /**
     * Produce il repository in memoria dei luoghi.
     *
     * @param config configurazione che fornisce il raggio terrestre
     * @return repository in memoria con il calcolo configurato
     */
    @Produces
    @ApplicationScoped
    public PlaceRepository placeRepository(AegisConfig config) {
        return new InMemoryPlaceRepository(config);
    }

    /**
     * Produce il generatore del codice di tracking.
     *
     * @param config configurazione con entropia e alfabeto del codice
     * @return generatore che usa byte casuali crittograficamente forti
     */
    @Produces
    @ApplicationScoped
    public TrackingCodeGenerator trackingCodeGenerator(AegisConfig config) {
        return new SecureRandomTrackingCodeGenerator(config);
    }

    /**
     * Produce il registro in memoria delle prenotazioni durevoli.
     *
     * @return registro in memoria usato nello sviluppo locale
     */
    @Produces
    @ApplicationScoped
    public SubmissionReservationPort submissionReservations() {
        return new InMemorySubmissionReservations();
    }

    /**
     * Produce il canale di notifica che registra le consegne senza effetti esterni.
     *
     * @return canale in memoria usato nello sviluppo locale
     */
    @Produces
    @ApplicationScoped
    public NotificationChannel notificationChannel() {
        return new RecordingNotificationChannel();
    }

    /**
     * Produce la politica di autorizzazione dagli identificativi configurati.
     *
     * @param config configurazione con gli elenchi di identificativi autorizzati
     * @return politica che nega l'accesso alle identità non presenti negli elenchi
     */
    @Produces
    @ApplicationScoped
    public AuthorizationPolicy authorizationPolicy(AegisConfig config) {
        return new InMemoryAuthorizationPolicy(
                userIds(config.submitterIds()), userIds(config.moderatorIds()));
    }

    /**
     * Produce l'orologio dal fuso orario configurato.
     *
     * @param config configurazione che indica il fuso orario
     * @return orologio di sistema associato al fuso configurato
     */
    @Produces
    @ApplicationScoped
    public Clock clock(AegisConfig config) {
        return Clock.system(config.timeZone());
    }

    /**
     * Produce il gateway in memoria come singola istanza applicativa.
     *
     * <p>Il tipo bean è limitato alla classe concreta, così le due porte
     * prodotte dai metodi dedicati restano le sole sorgenti di quelle interfacce.
     *
     * @return gateway condiviso tra scrittura e outbox
     */
    @Produces
    @ApplicationScoped
    @Typed(InMemoryNotificationGateway.class)
    public InMemoryNotificationGateway notificationGateway() {
        return new InMemoryNotificationGateway();
    }

    /**
     * Produce la porta di scrittura delle intenzioni dalla stessa istanza del gateway.
     *
     * @param gateway gateway applicativo condiviso
     * @return porta che accoda le intenzioni nello stesso gateway dell'outbox
     */
    @Produces
    @ApplicationScoped
    public NotificationPort notificationPort(InMemoryNotificationGateway gateway) {
        return gateway;
    }

    /**
     * Produce la porta di lettura e conferma dell'outbox dalla stessa istanza del gateway.
     *
     * @param gateway gateway applicativo condiviso
     * @return outbox che legge e conferma le intenzioni accodate dalla porta di scrittura
     */
    @Produces
    @ApplicationScoped
    public NotificationOutbox notificationOutbox(InMemoryNotificationGateway gateway) {
        return gateway;
    }

    /**
     * Produce la porta di routing finché il provider esterno non è collegato.
     *
     * @return porta che segnala l'assenza del provider senza produrre candidati
     */
    @Produces
    @ApplicationScoped
    public RoutingProvider routingProvider() {
        return (origin, destination) -> {
            throw new IllegalStateException(ROUTING_UNAVAILABLE);
        };
    }

    /**
     * Produce il caso d'uso di dettaglio di un luogo.
     *
     * @param places repository dei luoghi iniettato nel costruttore
     * @param config configurazione con la finestra di validità delle osservazioni
     * @param clock orologio applicativo
     * @return caso d'uso con la porta di lettura già risolta
     */
    @Produces
    @Dependent
    public FindPlace findPlace(PlaceRepository places, AegisConfig config, Clock clock) {
        return new FindPlace(places, new FreshnessWindow(config.freshnessWindow()), clock);
    }

    /**
     * Produce il caso d'uso di ricerca dei luoghi.
     *
     * @param places repository dei luoghi iniettato nel costruttore
     * @param config configurazione con la finestra di validità delle osservazioni
     * @param clock orologio applicativo
     * @return caso d'uso con la porta di lettura già risolta
     */
    @Produces
    @Dependent
    public SearchPlaces searchPlaces(PlaceRepository places, AegisConfig config, Clock clock) {
        return new SearchPlaces(places, new FreshnessWindow(config.freshnessWindow()), clock);
    }

    /**
     * Produce il caso d'uso di pianificazione dei percorsi.
     *
     * @param places repository dei luoghi iniettato nel costruttore
     * @param routing porta di routing iniettata nel costruttore
     * @return caso d'uso con le porte già risolte
     */
    @Produces
    @Dependent
    public PlanRoute planRoute(PlaceRepository places, RoutingProvider routing) {
        return new PlanRoute(places, routing);
    }

    /**
     * Produce il caso d'uso di tracking pubblico.
     *
     * @param reports repository delle segnalazioni iniettato nel costruttore
     * @return caso d'uso con la porta di lettura già risolta
     */
    @Produces
    @Dependent
    public TrackReport trackReport(ReportRepository reports) {
        return new TrackReport(reports);
    }

    /**
     * Produce il caso d'uso di invio di una segnalazione.
     *
     * @param reservations porta di prenotazione iniettata nel costruttore
     * @param reports repository delle segnalazioni iniettato nel costruttore
     * @param policy politica di autorizzazione iniettata nel costruttore
     * @param codes generatore del codice di tracking iniettato nel costruttore
     * @param clock orologio iniettato nel costruttore
     * @return caso d'uso con porte e orologio già risolti
     */
    @Produces
    @Dependent
    public ReportSubmission reportSubmission(
            SubmissionReservationPort reservations,
            ReportRepository reports,
            AuthorizationPolicy policy,
            TrackingCodeGenerator codes,
            Clock clock) {
        return new ReportSubmission(reservations, reports, policy, codes, clock);
    }

    /**
     * Produce il caso d'uso di moderazione di una segnalazione.
     *
     * @param reports repository delle segnalazioni iniettato nel costruttore
     * @param policy politica di autorizzazione iniettata nel costruttore
     * @param notifications porta di scrittura dell'outbox iniettata nel costruttore
     * @param clock orologio iniettato nel costruttore
     * @return caso d'uso con porte e orologio già risolti
     */
    @Produces
    @Dependent
    public ModerateReport moderateReport(
            ReportRepository reports,
            AuthorizationPolicy policy,
            NotificationPort notifications,
            Clock clock) {
        return new ModerateReport(reports, policy, notifications, clock);
    }

    /**
     * Convertire gli identificativi configurati nei tipi di dominio attesi dalla politica.
     *
     * @param identifiers identificativi letti dalla configurazione
     * @return insieme non modificabile di identificativi utente
     */
    private Set<UserId> userIds(Set<UUID> identifiers) {
        return identifiers.stream().map(UserId::new).collect(Collectors.toUnmodifiableSet());
    }
}
