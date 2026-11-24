# ADR-0005: contratto di operabilità e ripristino

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0005 |
| Titolo | Contratto di operabilità e ripristino |
| Stato | Proposto |
| Data | 16 novembre 2026 |
| Responsabile | Responsabile operativo, da designare |
| Revisori | Responsabile tecnico e responsabile dati, da designare |
| Requisiti correlati | FR-07, FR-08, FR-12, NFR-04, NFR-06, NFR-08, NFR-09, NFR-10, NFR-11, NFR-14, NFR-15, NFR-16, UC-03, UC-05, OPEN-02, OPEN-04, OPEN-05, OPEN-07, OPEN-08, ENG-02, ENG-06 ed ENG-08 |

## Contesto e problema

RAD richiede metriche, allarmi, backup automatici e prove di ripristino. SDD descrive importazioni asincrone, pubblicazione atomica, pulizia cache recuperabile e notifiche con retry. OPEN-07 lascia da concordare carichi, performance, disponibilità e obiettivi di ripristino. OPEN-08 lascia da selezionare runtime e servizi e da definire retry, backoff, allarmi e procedure operative.

La documentazione non definisce ancora come distinguere un ritardo recuperabile da un lavoro bloccato, come riprendere notifiche dopo un arresto, quali segnali devono sostenere l’esercizio e come verificare che un ripristino riporti database e file a uno stato coerente. Inoltre, FR-07 richiede che i retry non creino segnalazioni duplicate, ma non specifica come recuperare una richiesta quando il commit riesce e la risposta si perde. I valori operativi richiedono un responsabile e misure in un ambiente concordato.

## Driver decisionali

| Driver | Criterio |
|---|---|
| Recuperabilità | Un arresto non perde lavori già accettati e ogni ripresa è idempotente o riconciliabile. |
| Integrità | Il ripristino comprende dati relazionali, oggetti e stato necessario a continuare le operazioni senza riferimenti inconsistenti. |
| Diagnosi | I segnali permettono di localizzare errori e ritardi senza esporre dati personali o segreti. |
| Allarmi azionabili | Ogni allarme identifica condizione, impatto, responsabile e procedura di risposta. |
| Obiettivi approvati | Soglie, finestre di misura, tempi di ripristino e volumi derivano da OPEN-07 e non sono inventati. |
| Operabilità sostenibile | Runtime, servizi gestiti, canali e capacità sono scelti in base a competenze, costi e responsabilità effettive. |
| Evidenza verificabile | Ripristino, recupero delle code e risposta agli incidenti sono provati e registrati prima del pilota. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| Servizi gestiti dietro porte operative | Riduce la gestione di database, storage e osservabilità e può fornire funzioni di backup. | Dipendenza da provider, localizzazione, costo, limiti, contratto e capacità di esportare e ripristinare i dati. | Può supportare NFR-09 e NFR-10 se le funzioni sono provate e gli obiettivi sono approvati. Scelta aperta in OPEN-08. |
| Servizi autogestiti | Offre controllo su topologia e strumenti e può essere adatto a vincoli specifici. | Richiede reperibilità, patching, capacità, procedure e prove di backup gestite dal progetto. | Può supportare NFR-09 e NFR-10 se esistono responsabilità e capacità operative documentate. |
| Solo log applicativi e backup del database | Riduce il lavoro iniziale e conserva alcune informazioni diagnostiche. | Non copre metriche e allarmi azionabili, object storage, recupero dei worker o coerenza dei dati dopo il ripristino. | Non soddisfa i criteri di NFR-09 e NFR-10 né le condizioni di recupero previste per ENG-08. |

Per i retry della creazione di una segnalazione sono state considerate tre strategie:

| Opzione | Vantaggi | Costi e rischi | Valutazione |
|---|---|---|---|
| Nessuna chiave di idempotenza | Semplice da implementare. | Una risposta persa dopo il commit può indurre l’utente a creare una seconda segnalazione. | Non soddisfa FR-07 e il recupero previsto da UC-03. |
| Deduplicazione euristica sul contenuto | Non richiede una chiave inviata dal client. | Segnalazioni legittime con contenuto simile possono essere unite o rifiutate. | Non distingue in modo affidabile un retry da una nuova operazione. |
| Chiave di idempotenza e impronta del payload | Identifica il retry della stessa operazione e consente di rilevare il riuso della chiave con contenuto diverso. | Richiede regole esplicite per ambito, durata, persistenza, concorrenza e riproduzione della risposta. | Proposta per soddisfare FR-07. Le regole restano soggette a OPEN-04, OPEN-05 e OPEN-08. |

## Proposta per approvazione

Definire un contratto operativo indipendente dagli strumenti. Ogni servizio deve produrre segnali sufficienti per rilevare guasti, misurare gli obiettivi approvati e seguire una procedura di risposta documentata.

Per la consegna delle notifiche sono state considerate tre strategie:

- Invio diretto dopo il commit. È semplice, ma un arresto tra il commit del caso e l’invio può perdere la notifica senza lasciare un lavoro recuperabile.
- Outbox transazionale. L’intenzione di invio viene persistita nella stessa transazione del report e dell’evento iniziale oppure della transizione e del relativo evento di moderazione. Se il provider non risponde in modo conclusivo, il worker conserva l’esito ambiguo per riconciliazione. Questa strategia è la proposta perché evita la finestra di perdita tra il commit e la registrazione dell’intenzione, senza dichiarare consegna esattamente una volta.
- Transazione distribuita con il provider. Richiederebbe supporto transazionale compatibile dal provider e accoppierebbe disponibilità e tecnologia esterna alla transazione applicativa. Non è selezionata come baseline.

Per le richieste di creazione, una prenotazione durevole e univoca viene registrata prima di effetti esterni e coordina una singola operazione logica. La stessa chiave con lo stesso payload restituisce lo stato o l’esito della medesima operazione senza ripeterne gli effetti. La stessa chiave con payload diverso è rifiutata come conflitto. Chiavi diverse identificano operazioni distinte anche quando il contenuto coincide. L’impronta comprende tutti i campi normalizzati e il digest verificato dell’eventuale allegato. La chiave non concede accesso ai dati: ogni riproduzione di un risultato o di un codice di tracking resta subordinata all’autorizzazione definita in OPEN-04 e OPEN-05. Scope, durata, formato canonico, transizioni terminali, coordinamento del recupero e contenuto riproducibile sono da approvare in OPEN-08.

- Registrare log strutturati con identificativo di correlazione, operazione, esito e riferimenti tecnici necessari alla diagnosi. Escludere credenziali, codici di tracking, descrizioni delle segnalazioni e dati personali non necessari. Accesso e conservazione seguono OPEN-05 e OPEN-08.
- Raccogliere almeno disponibilità e latenza delle API, errori per operazione, ritardo e fallimenti delle sincronizzazioni, lavori di pulizia cache pendenti, anzianità e dimensione delle code e risultato delle notifiche. Le metriche non devono usare identificativi personali o etichette ad alta cardinalità. Finestre, soglie e obiettivi sono approvati in OPEN-07.
- Associare a ogni allarme un impatto osservabile, un responsabile, una procedura e un criterio di chiusura. Verificare gli allarmi con eventi controllati prima del pilota. Canali e reperibilità sono scelti in OPEN-08.
- Persistire i lavori asincroni accettati prima dell’esecuzione. Retry, backoff, timeout e numero massimo di tentativi sono configurabili per tipo di lavoro e approvati in OPEN-08. Dopo l’esaurimento dei tentativi, il lavoro resta consultabile e recuperabile da un operatore. Non eliminarlo né segnalarlo come riuscito senza evidenza.
- Per le notifiche consentite da FR-12, registrare l’intenzione di invio in un outbox nella stessa transazione che persiste il report e l’evento iniziale oppure la transizione e il relativo evento di moderazione. Un worker legge le intenzioni solo dopo il commit e registra gli esiti senza includere segreti o dati personali non necessari. Destinatari, contenuto, accesso, conservazione e cancellazione sono soggetti alle decisioni OPEN-04 e OPEN-05.
- Rendere idempotente l’elaborazione delle intenzioni. Se l’esito del provider è ambiguo, conservare uno stato da riconciliare prima di un nuovo tentativo. Il sistema non dichiara consegna esattamente una volta. Se il provider non offre idempotenza o verifica dell’esito, il rischio di invio duplicato e la regola di recupero devono essere valutati e approvati in OPEN-08. Retry, backoff, timeout, tentativi massimi e azione operativa restano da definire. Il meccanismo concreto di persistenza e il provider restano da selezionare.
- Definire backup e ripristino congiuntamente per database e object storage. Registrare copertura, data, esito e integrità delle copie. La procedura di ripristino riconcilia i riferimenti tra record e oggetti e verifica che le policy di accesso continuino ad applicarsi.
- Prima del pilota, eseguire una prova documentata di ripristino in ambiente isolato. Verificare integrità relazionale e degli oggetti, ripresa dei lavori pendenti, comportamento delle cache e assenza di notifiche duplicate. Confrontare i tempi osservati con gli obiettivi approvati in OPEN-07.
- Mantenere runbook revisionati per incidenti, ripristino, migrazioni e rollback. Ogni procedura indica prerequisiti, responsabile, passi verificabili, criteri di arresto e registrazione dell’esito.

La proposta non seleziona piattaforme, servizi, strumenti, canali, valori di soglia, frequenze di backup o tempi di conservazione. Queste scelte richiedono i responsabili e le evidenze previste da OPEN-05, OPEN-07 e OPEN-08.

## Decisione

Non approvata. Il responsabile operativo e il responsabile tecnico devono selezionare gli ambienti e i servizi, approvare gli obiettivi misurabili e assegnare le responsabilità. Il responsabile dati e il responsabile privacy devono rivedere integrità, accesso e conservazione dei dati. Le procedure devono essere provate prima di dichiarare completato ENG-08.

## Conseguenze attese se approvata

- Ogni segnale operativo ha uno scopo diagnostico o di allarme e un responsabile associato.
- Un lavoro pendente o esaurito resta visibile e recuperabile, invece di essere perso o considerato completato senza evidenza.
- Le notifiche e gli altri effetti esterni hanno un percorso di riconciliazione dopo un arresto o una risposta ambigua.
- Backup e ripristino coprono sia database sia oggetti e verificano i riferimenti tra i due archivi.
- I target di disponibilità, performance, carico e ripristino sono misurati e approvati prima del pilota.
- Le procedure operative diventano artefatti revisionabili e sono verificate in un ambiente concordato.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Obiettivi OPEN-07 approvati | Carichi, soglie, finestre di misura, disponibilità e obiettivi di ripristino hanno unità, ambito, responsabile e fonte | Responsabile operativo, da designare | In attesa |
| Inventario di segnali e allarmi | Ogni segnale ha definizione, scopo, trattamento dei dati e allarmi associati con procedura e responsabile | Responsabile operativo e responsabile privacy, da designare | In attesa |
| Prova di recupero dei worker | Interruzione, retry, esaurimento dei tentativi e ripresa sono osservabili. Il lavoro non è perso e gli effetti possibili duplicati sono gestiti secondo la regola approvata | Responsabile tecnico e operativo, da designare | Non eseguito |
| Prova dell’outbox notifiche | Intenzione e modifica di dominio sono persistite nella stessa transazione. Un arresto dopo il commit lascia l’invio recuperabile. Gli esiti ambigui non sono registrati come consegnati senza evidenza né ritentati senza applicare la regola approvata | Responsabile tecnico e operativo, da designare | Non eseguito |
| Prova di idempotenza delle segnalazioni | Simulare commit riuscito e risposta persa, poi ripetere la richiesta con la stessa chiave e payload. Verificare una sola segnalazione, un solo evento iniziale, un solo collegamento degli allegati e una sola intenzione di notifica. La risposta riprodotta rispetta l’autorizzazione approvata | Responsabile tecnico e privacy, da designare | Non eseguito |
| Prova di prenotazione, conflitto e concorrenza | Verificare che la prenotazione univoca preceda upload e notifiche. La stessa chiave con payload diverso è rifiutata. Richieste concorrenti con la stessa chiave non avviano effetti paralleli. Un arresto dopo la prenotazione è recuperato tramite l’intento registrato. Le regole di scadenza e riuso seguono OPEN-08 | Responsabile tecnico, da designare | Non eseguito |
| Prova di ripristino con database e oggetti | Integrità verificata, riferimenti coerenti e tempi confrontati con gli obiettivi approvati | Responsabile operativo e responsabile dati, da designare | Non eseguito |
| Prova di allarme e runbook | Un evento controllato genera l’allarme atteso e il responsabile completa la procedura registrandone l’esito | Responsabile operativo, da designare | Non eseguito |
| Revisione di backup e cancellazione | Copie e procedure rispettano accesso e ciclo di vita approvati in OPEN-05 | Responsabile privacy e responsabile operativo, da designare | In attesa |

Riesaminare la proposta quando cambiano ambienti, provider, obiettivi operativi, architettura delle code o policy di conservazione e cancellazione.
