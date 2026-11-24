# ADR-0004: contratto di identità e autorizzazione

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0004 |
| Titolo | Contratto di identità e autorizzazione |
| Stato | Proposto |
| Data | 16 novembre 2026 |
| Responsabile | Responsabile tecnico, da designare |
| Revisori | Responsabile prodotto e responsabile privacy, da designare |
| Requisiti correlati | FR-07, FR-08, FR-09, FR-12, FR-13, NFR-04, NFR-05, NFR-06, UC-03, UC-04, UC-05, UC-06, OPEN-04, OPEN-05, OPEN-08, OPEN-12 ed ENG-02 |

## Contesto e problema

RAD mantiene pubblica la consultazione e richiede controlli di autenticazione e ruolo sulle operazioni riservate. L’invio di segnalazioni può essere anonimo o autenticato, il tracking deve mostrare soltanto lo stato consentito e le policy restano aperte in OPEN-04. OPEN-05 deve ancora approvare finalità, accessi, conservazione e cancellazione dei dati personali.

SDD propone un sottosistema Identity, ma non definisce un contratto comune per l’autorizzazione delle richieste, l’identità dei processi automatici, la protezione del tracking o il comportamento in caso di identità e permessi non validi. Queste regole devono essere coerenti tra interfaccia, API e worker senza anticipare decisioni di prodotto o privacy.

## Driver decisionali

| Driver | Criterio |
|---|---|
| Autorizzazione verificabile | Il server controlla identità, ruolo e permesso per ogni operazione protetta. |
| Accesso minimo | Ogni identità umana o tecnica riceve solo le capacità necessarie al proprio compito. |
| Consultazione pubblica | La lettura pubblica non richiede una sessione autenticata. |
| Protezione del tracking | La credenziale consente accesso limitato alla singola segnalazione e non espone dati identificativi. |
| Audit attribuibile | Le decisioni di moderazione e le operazioni manuali sono attribuite a un’identità autenticata. |
| Scelte reversibili | Il dominio dipende da porte di identità e autorizzazione, non da API di uno specifico provider. |
| Privacy da approvare | Dati, finalità, conservazione e cancellazione seguono OPEN-05 e non sono definiti da questo ADR. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| Identity provider gestito dietro porte applicative | Riduce la gestione delle credenziali e mantiene sostituibile l’integrazione. | Dipendenza da disponibilità, contratto, trattamento dati, configurazione e costi del fornitore. | Può supportare FR-13 e UC-05 se ruoli e autorizzazioni sono verificati lato server. La selezione resta aperta in OPEN-04 e OPEN-08. |
| Provider di identità gestito dal progetto dietro porte applicative | Offre controllo operativo e mantiene separato il dominio. | Richiede gestione sicura di credenziali, aggiornamenti, disponibilità, recupero account e incidenti. | Può supportare FR-13 e UC-05 se il modello operativo e le verifiche sono approvati. |
| Autenticazione implementata direttamente nel dominio applicativo | Riduce componenti esterni iniziali e può adattarsi a requisiti circoscritti. | Aumenta il rischio e la responsabilità per memorizzazione, recupero, protezione e ciclo di vita delle credenziali. | Non è giustificata senza requisiti, capacità operative e revisione di sicurezza approvati. |

## Proposta per approvazione

Definire autenticazione e autorizzazione attraverso porte applicative sostituibili. Il server applica una policy esplicita a ogni richiesta protetta e rifiuta l’operazione se identità, ruolo o permesso richiesto mancano o non sono validi.

- La consultazione pubblica di luoghi e percorsi non dipende da una sessione autenticata, fatte salve le restrizioni approvate per i dati esposti.
- `ModerateReport` richiede un’identità umana autenticata con permesso di moderazione. Ogni transizione registra l’identità dell’attore e il timestamp nella stessa operazione atomica che persiste evento e stato.
- L’avvio manuale della sincronizzazione in UC-06 richiede un’identità con permesso operativo. Le esecuzioni pianificate usano un’identità tecnica distinta, con capacità limitate alla sincronizzazione configurata e senza riutilizzare credenziali personali.
- Per `CreateReport`, la modalità anonima o autenticata resta da decidere in OPEN-04. Il server applica comunque validazione, controlli antiabuso e le condizioni di consenso definite prima di accettare l’invio.
- Il codice di tracking è una credenziale dedicata a una sola segnalazione e concede solo i campi autorizzati per UC-04. Il sistema non memorizza né registra il valore in chiaro. Generazione, memorizzazione protetta, revoca, eventuale recupero e risposta a codici errati devono essere specificati e provati prima del rilascio. Non si assumono algoritmi, lunghezze o soglie in assenza di una decisione di sicurezza.
- Una richiesta non autenticata o non autorizzata non modifica dati. Risposte e log non rivelano se un identificativo di segnalazione riservato esiste. Il dettaglio della risposta e le misure anti enumerazione restano da verificare con i criteri di OPEN-04 e OPEN-08.
- I log non contengono credenziali, codici di tracking o dati personali non necessari. Accesso, conservazione e cancellazione dei log seguono OPEN-05 e OPEN-08.

La proposta non seleziona un provider d’identità e non decide se l’invio sia anonimo, quali ruoli siano assegnati alle persone, i campi esposti dal tracking o i tempi di conservazione. Tali decisioni richiedono approvazione dei responsabili OPEN-04 e OPEN-05.

## Decisione

Non approvata. Responsabile prodotto, responsabile tecnico e responsabile privacy devono approvare modalità di invio, modello dei ruoli, permessi, accesso al tracking e trattamento dei dati prima di configurare un provider o attivare le funzioni protette.

## Conseguenze attese se approvata

- I casi d’uso protetti applicano autorizzazione lato server e comportamento di default deny.
- Interfaccia e API condividono lo stesso contratto di permesso, evitando che la sola visibilità di un comando sia trattata come autorizzazione.
- Moderazione e avvio manuale della sincronizzazione sono attribuibili a identità distinte e autorizzate.
- Le esecuzioni pianificate non dipendono da credenziali personali.
- La credenziale di tracking è separata dall’identità dell’utente e limita l’accesso ai dati del singolo caso secondo una policy ancora da approvare.
- La scelta del provider e i dettagli delle credenziali richiedono valutazione operativa e di sicurezza prima dell’implementazione.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Matrice ruoli e permessi OPEN-04 | Ogni operazione protetta ha attori autorizzati, permesso e campi accessibili approvati | Responsabile prodotto e responsabile privacy, da designare | In attesa |
| Prova di accesso negato | Richieste anonime e identità prive del permesso non modificano dati né ottengono risposte riservate | Responsabile tecnico, da designare | Non eseguito |
| Prova delle transizioni di moderazione | Solo amministratori con permesso di moderazione applicano le transizioni approvate in OPEN-12 e l’evento identifica l’attore | Responsabile tecnico, da designare | Non eseguito |
| Prova di tracking | La credenziale accede solo ai campi approvati della segnalazione associata e non è esposta in log o risposte non autorizzate | Responsabile tecnico e responsabile sicurezza, da designare | Non eseguito |
| Prova delle identità operative | Avvio manuale e sincronizzazione pianificata usano identità e permessi distinti | Responsabile operativo, da designare | Non eseguito |
| Revisione del ciclo di vita dei dati | Credenziali, identità, eventi e log rispettano le regole approvate in OPEN-05 | Responsabile privacy e responsabile operativo, da designare | In attesa |

Riesaminare la proposta quando sono approvate le policy OPEN-04 e OPEN-05, quando si seleziona il provider d’identità o quando cambiano i flussi di invio, tracking, moderazione e sincronizzazione.
