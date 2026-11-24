# RAD

## Documento di Analisi dei Requisiti

| Dato | Valore |
|---|---|
| Progetto | AEGIS |
| Corso | Ingegneria del Software e Interazione Uomo-Macchina |
| Docenti | Prof.ssa Mariacarla Staffa |
| Candidati | Alessio Attilio, Michela Rita Brancaccio, Andrea Plini |
| Anno accademico | 2025/2026 |

## SOMMARIO

1. Introduzione
   - 1.1 Scopo del sistema
   - 1.2 Area di dominio
   - 1.3 Definizioni, acronimi e abbreviazioni
   - 1.4 Panoramica
2. Sistema corrente
3. Sistema proposto
   - 3.1 Panoramica
   - 3.2 Requisiti funzionali
   - 3.3 Requisiti non funzionali
   - 3.4 Vincoli
   - 3.5 Modelli di sistema
     - 3.5.1 Utenti del sistema
     - 3.5.2 Scenari
     - 3.5.3 Modelli dei casi d’uso
     - 3.5.4 Modello degli oggetti
     - 3.5.5 Diagramma delle classi
     - 3.5.6 Diagrammi di sequenza
4. Interfaccia utente
   - 4.1 Descrizione del prototipo realizzato
   - 4.2 Test di usabilità
   - 4.3 Valutazione dell’usabilità secondo ISO 9241-110
   - 4.4 Allegati
5. Glossario

---

## INTRODUZIONE

### 1.1 SCOPO DEL SISTEMA

La web app **AEGIS** aiuta le persone con disabilità motoria, visiva o uditiva a pianificare spostamenti urbani, consultare l’accessibilità di stazioni, fermate e mezzi del trasporto pubblico locale (TPL) e segnalare barriere architettoniche o guasti agli ausili (ad esempio ascensori fuori servizio, rampe ostruite). Il sistema mostra esplicitamente la fonte e la data di aggiornamento di ciascuna caratteristica. Qualsiasi dato assente o scaduto viene classificato come **sconosciuto** e non viene mai interpretato come prova di accessibilità.

Gli obiettivi principali sono trovare e confrontare percorsi accessibili, consultare le caratteristiche dei luoghi, inviare e tracciare segnalazioni tramite codice univoco e permettere agli amministratori autorizzati di moderarle mantenendo uno storico immutabile delle decisioni.

| Obiettivo | Criterio di verifica |
|---|---|
| Trovare e confrontare percorsi | Le alternative proposte distinguono chiaramente tratti compatibili, barriere note e tratti non verificati. |
| Consultare l’accessibilità | Ogni caratteristica espone stato attuale, fonte dei dati e data di aggiornamento. |
| Inviare e tracciare segnalazioni | L’invio restituisce un codice alfanumerico univoco utilizzabile per consultare lo stato della segnalazione. Le foto accettate non superano 5 MB. |
| Moderare segnalazioni | Ogni decisione amministrativa aggiorna lo stato registrando un evento storico non sovrascrivibile. |
| Usare i flussi essenziali con tecnologie assistive | I flussi principali devono superare i criteri applicabili di livello A e AA della baseline WCAG 2.1. |

*Tabella 1.1: Obiettivi del sistema e relativi criteri di verifica*

### 1.2 AREA DI DOMINIO

Il dominio comprende la pianificazione della mobilità urbana, i dati geospaziali del trasporto pubblico, il monitoraggio dell’accessibilità delle infrastrutture e il *crowdsourcing* moderato delle segnalazioni. L’area geografica di riferimento comprende la rete metropolitana, ferroviaria urbana e di superficie della città di Napoli, integrando i dataset ufficiali dell’azienda di trasporto locale con le segnalazioni georeferenziate degli utenti.

### 1.3 DEFINIZIONI, ACRONIMI E ABBREVIAZIONI

I termini di dominio e tecnici sono raccolti nel Glossario (capitolo 5). Le abbreviazioni usate per identificare gli elementi del documento sono le seguenti.

| Abbreviazione | Significato |
|---|---|
| FR | Requisito funzionale (*Functional Requirement*), identificato da FR-nn. |
| NFR | Requisito non funzionale (*Non-Functional Requirement*), identificato da NFR-nn. |
| UC | Caso d’uso (*Use Case*), identificato da UC-nn. |
| TPL | Trasporto Pubblico Locale. |
| GTFS | *General Transit Feed Specification*, formato dei dati ufficiali di trasporto. |
| WCAG | *Web Content Accessibility Guidelines*, livello di conformità AA. |
| GDPR | Regolamento Europeo sulla protezione dei dati personali. |
| RBAC | *Role-Based Access Control*, controllo degli accessi basato sui ruoli. |

*Tabella 1.2: Abbreviazioni usate nel documento*

### 1.4 PANORAMICA

Il documento è organizzato in 5 capitoli:

- **Capitolo 1** definisce scopo, obiettivi e area di dominio del sistema.
- **Capitolo 2** descrive la situazione attuale, assente di un sistema dedicato.
- **Capitolo 3** presenta il sistema proposto: requisiti funzionali e non funzionali, vincoli e modelli (utenti, scenari, casi d’uso, oggetti, classi e sequenze).
- **Capitolo 4** descrive l’interfaccia utente, il prototipo, i test di usabilità e la valutazione secondo ISO 9241-110.
- **Capitolo 5** raccoglie il glossario dei termini.

L’architettura e le scelte progettuali che realizzano questi requisiti sono documentate nel System Design Document (SDD).

## SISTEMA CORRENTE

Il progetto è di tipo **greenfield** (sviluppo ex novo), in quanto non esiste attualmente un sistema centralizzato e in tempo reale dedicato al monitoraggio collaborativo dell’accessibilità urbana nell’area di riferimento. Attualmente gli utenti si affidano a bollettini statici sui siti istituzionali (spesso non aggiornati sui guasti improvvisi) o a segnalazioni informali su canali social.

## SISTEMA PROPOSTO

### 3.1 PANORAMICA

La piattaforma offre consultazione pubblica con ricerca per area, mappa interattiva affiancata da vista testuale equivalente, dettagli di accessibilità e pianificazione di percorsi alternativi. Il sistema distingue le caratteristiche verificate (positive o negative) da quelle sconosciute o scadute. Le segnalazioni inviate dagli utenti seguono un ciclo di moderazione tracciato mediante uno storico *append-only*.

**Provenienza dei dati.** Le fermate, le linee e le informazioni di accessibilità di base (ad esempio l’accessibilità alla salita in carrozzina, ascensori e percorsi di collegamento) provengono dai dataset ufficiali in formato GTFS. Le caratteristiche non coperte dal GTFS (ad esempio percorsi tattili e annunci sonori) e i guasti temporanei derivano dalle segnalazioni confermate dagli amministratori. Ogni dato conserva fonte e data di osservazione.

**Accesso.** L’Ospite può pianificare percorsi, consultare luoghi, seguire segnalazioni tramite codice e registrarsi. L’invio di segnalazioni è riservato all’Utente registrato autenticato. Le funzioni personali e le operazioni amministrative richiedono autenticazione e autorizzazione.

### 3.2 REQUISITI FUNZIONALI

| ID | Responsabile dell’azione | Azione richiesta | Priorità | Caso d’uso |
|---|---|---|---|---|
| FR-01 | Sistema | Mostrare su mappa e in elenco i luoghi e le informazioni di accessibilità per l’area richiesta. | Must | UC-02 |
| FR-02 | Sistema | Mostrare caratteristiche quali ascensori, rampe, percorsi tattili e annunci sonori con stato, fonte, aggiornamento e validità. | Must | UC-01, UC-02 |
| FR-03 | Sistema | Classificare come sconosciuti i dati assenti, incoerenti o con data di aggiornamento più vecchia di 30 giorni, senza inferire accessibilità. La soglia di 30 giorni è una proposta di progetto da confermare con il responsabile dei dati prima del pilota. | Must | UC-01, UC-02 |
| FR-04 | Ospite o utente registrato e sistema | Acquisire origine, destinazione ed esigenze di accessibilità e calcolare e restituire percorsi alternativi. | Must | UC-01 |
| FR-05 | Sistema | Distinguere nei percorsi barriere note, tratti compatibili e non verificati. | Must | UC-01 |
| FR-06 | Ospite o utente registrato e sistema | Consentire la selezione di un luogo e mostrare dettagli di accessibilità e provenienza. | Must | UC-02 |
| FR-07 | Utente registrato | Inserire luogo, categoria, descrizione e data di osservazione del problema, allegando facoltativamente una foto di dimensione non superiore a 5 MB. La data di invio è registrata dal sistema. | Must | UC-03 |
| FR-08 | Sistema | Generare e restituire un codice di tracking univoco per consultare lo stato della segnalazione senza esporre dati personali. | Must | UC-03, UC-04 |
| FR-09 | Amministratore e sistema | Consentire conferma, rifiuto, risoluzione o unione di duplicati, registrando ogni decisione nello storico. | Must | UC-05 |
| FR-10 | Amministratore e sistema | Importare dataset ufficiali (formato GTFS) e registrare versione, esito e timestamp della sincronizzazione. | Should | UC-06 |
| FR-11 | Sistema | Aggiornare i dati visibili dell’area interessata al termine di ogni sincronizzazione validata. | Should | UC-06 |
| FR-12 | Sistema | Inviare notifiche agli utenti registrati che hanno dato il consenso (autori delle segnalazioni e utenti che seguono un luogo) al cambio di stato di una segnalazione. | Could | UC-05, UC-08 |
| FR-13 | Sistema | Autenticare gli utenti e verificare i ruoli per le operazioni riservate, mantenendo pubblici consultazione dei luoghi e tracking tramite codice. | Must | UC-03, UC-05, UC-06, UC-07, UC-08 |
| FR-14 | Ospite e sistema | Consentire la registrazione e l’accesso con credenziali personali e la gestione dei luoghi preferiti e dei consensi per le notifiche. | Should | UC-07, UC-08, UC-09 |
| FR-15 | Utente registrato e sistema | Consentire la consultazione dello storico delle proprie segnalazioni con il relativo stato. | Should | UC-08 |

*Tabella 3.1: Requisiti funzionali*

#### CRITERI DI ACCETTAZIONE FR

| ID | Criterio di accettazione |
|---|---|
| FR-01 | Per l’area urbana selezionata sono mostrati i luoghi censiti e i relativi dati di accessibilità disponibili. |
| FR-02 | Ogni caratteristica mostrata espone stato, fonte, data di osservazione e scadenza di validità. |
| FR-03 | Dati assenti, incoerenti o aggiornati da più di 30 giorni sono mostrati come sconosciuti e non sono usati per dichiarare compatibilità. |
| FR-04 | Una richiesta valida restituisce zero o più percorsi alternativi con tempi di percorrenza, cambi e stato di verifica. |
| FR-05 | Ogni tratto del percorso distingue barriere note, compatibilità nota e stato non verificato. |
| FR-06 | La selezione di un luogo mostra scheda di dettaglio, caratteristiche note, fonte e validità. |
| FR-07 | Solo un Utente registrato autenticato può inviare una segnalazione. L’invio valido salva segnalazione ed evento iniziale in modo atomico, un errore impedisce salvataggi parziali. La foto allegata entro 5 MB è pubblicata solo dopo la verifica di formato e sicurezza. |
| FR-08 | La creazione riuscita restituisce un codice di tracking non enumerabile, la pagina di consultazione mostra esclusivamente lo stato pubblico consentito. |
| FR-09 | Solo le transizioni di stato ammesse (`In verifica` → `Confermata` / `Rifiutata` / `Duplicata`, `Confermata` → `Risolta`) aggiornano lo stato e aggiungono un evento immutabile con autore e timestamp. Un duplicato è associato alla segnalazione di riferimento. |
| FR-10 | Solo un pacchetto dati completo e validato viene pubblicato, in caso di errore restano attivi i dati precedenti. |
| FR-11 | Alla pubblicazione di un aggiornamento validato i nuovi dati risultano immediatamente consultabili sulle viste di mappa ed elenco. |
| FR-12 | Le notifiche sono inviate in modo asincrono solo agli utenti che hanno espresso consenso, un errore del canale non annulla la decisione di moderazione. |
| FR-13 | La consultazione dei luoghi e il tracking tramite codice non richiedono autenticazione. L’invio e le altre operazioni riservate sono bloccati lato server (HTTP 401/403) per utenti non autenticati o non autorizzati. |
| FR-14 | Un utente può creare un account, accedere, disconnettersi, aggiungere o rimuovere luoghi preferiti e attivare o revocare il consenso alle notifiche. |
| FR-15 | L’utente registrato vede solo le proprie segnalazioni, con codice di tracking e stato corrente. |

*Tabella 3.2: Criteri di accettazione dei requisiti funzionali*

### 3.3 REQUISITI NON FUNZIONALI

La baseline di accessibilità del prodotto è **[WCAG 2.1 livello AA](https://www.w3.org/TR/WCAG21/)**. La verifica copre pagine, componenti, stati dinamici e flussi completi, inclusi gli errori. Per ogni criterio applicabile di livello A e AA registra esito, metodo, ambiente ed evidenza. Strumenti automatici, revisione manuale, tastiera, zoom, contrasto, tecnologie assistive e confronto tra mappa e vista testuale hanno risultati distinti. Le sessioni con utenti sono registrate separatamente dai risultati di conformità e usabilità. Gli strumenti automatici non dimostrano da soli la conformità. Eventuali dichiarazioni di conformità richiedono la verifica di tutti i criteri applicabili nel perimetro dichiarato. ISO 9241-110 resta il riferimento complementare per l’usabilità.

| ID | Qualità | Azione richiesta | Criterio di verifica | Responsabile |
|---|---|---|---|---|
| NFR-01 | Accessibilità | Consentire di completare i flussi essenziali da tastiera e con screen reader secondo **WCAG 2.1 livello AA**. | Per ogni criterio WCAG 2.1 A e AA applicabile, registrare esito ed evidenza. Il requisito è superato se tutti i criteri applicabili risultano conformi. | Team di sviluppo / QA |
| NFR-02 | Accessibilità | Fornire una vista testuale equivalente alle informazioni presentate sulla mappa. | Verifica di parità informativa tra vista mappa e vista elenco testuale per le medesime ricerche. | Team di sviluppo / QA |
| NFR-03 | Accessibilità | Garantire contrasto cromatico adeguato e leggibilità con zoom fino al 200%. | Verificare un rapporto di contrasto ≥ 4.5:1 per il testo standard e l’assenza di perdita di contenuto allo zoom secondo WCAG 2.1 livello AA. | Team di sviluppo / QA |
| NFR-04 | Privacy | Minimizzare i dati personali e separare l’identità dell’autore dai contenuti pubblici delle segnalazioni. | Verifica che endpoint pubblici e pagine di tracking non espongano email o identificativi dell’utente (conformità GDPR). | Responsabile tecnico |
| NFR-05 | Sicurezza | Proteggere le comunicazioni con HTTPS/TLS e applicare il controllo degli accessi basato sui ruoli (RBAC). | Verifica del protocollo HTTPS su tutti gli endpoint e rifiuto lato server delle operazioni riservate per ruoli non autorizzati. | Responsabile tecnico |
| NFR-06 | Sicurezza | Applicare validazione server-side degli input, rate limiting e protezione da injection. | Test con input malformati e tentativi di injection verificando il rifiuto senza effetti sui dati. | Responsabile tecnico |
| NFR-07 | Sicurezza | Accettare solo file immagine non superiori a 5 MB e verificarne tipo, integrità e sicurezza prima della pubblicazione. | Accettare file immagine validi fino a 5 MB. Rifiutare file più grandi o con estensione, tipo MIME e firma del contenuto discordanti. Rifiutare o mantenere in quarantena i file se la scansione fallisce o non è disponibile. | Responsabile tecnico |
| NFR-08 | Qualità dei dati | Esporre provenienza e freschezza di ogni dato di accessibilità. | Ispezione di un campione di luoghi, in cui ogni caratteristica riporta fonte e data di osservazione. La regola di scadenza applicata è quella definita in FR-03. | Team di sviluppo / QA |
| NFR-09 | Affidabilità | Mantenere consultabili i dati validi precedenti quando l’importazione di una fonte fallisce. | Indurre un errore di importazione e verificare che la versione pubblicata non cambi e che il dataset precedente resti consultabile senza pubblicazioni parziali. | Team di sviluppo / QA |
| NFR-10 | Performance | Rispondere rapidamente alle richieste di caricamento della mappa, dei luoghi e dei percorsi. | In un ambiente di test documentato, misurare il p95 per ciascuno dei tre flussi. Il p95 deve essere inferiore a 2 secondi. La soglia è una proposta di progetto da confermare con il responsabile tecnico prima del pilota. | Team di sviluppo / QA |
| NFR-11 | Usabilità | Consentire agli utenti di completare autonomamente i flussi principali e registrare tempi ed errori. | In una prova con 5 partecipanti del target, ognuno svolge i 4 compiti della Tabella 4.2. Almeno 17 dei 20 tentativi devono essere completati senza aiuto (≥85%). Il campione e la soglia sono una proposta di verifica da confermare prima della prova. | Team di sviluppo / QA |
| NFR-12 | Manutenibilità | Mantenere separati dominio, casi d’uso e adattatori, documentando le API. | Revisione dell’architettura a livelli e documentazione completa degli endpoint REST tramite specifica OpenAPI. | Responsabile tecnico |
| NFR-13 | Portabilità | Garantire la fruibilità dell’interfaccia web su browser desktop e dispositivi mobili (*responsive design*). | Test di compatibilità su Chrome, Firefox, Safari ed Edge sia su desktop che su smartphone. | Team di sviluppo / QA |

*Tabella 3.3: Requisiti non funzionali*

### 3.4 VINCOLI

| Vincolo | Specifica |
|---|---|
| Piattaforma applicativa | Backend sviluppato in Java LTS / Jakarta EE per API REST e worker asincroni, frontend web responsive. |
| Persistenza e file | Database relazionale PostgreSQL con estensione spaziale PostGIS e storage dedicato per le immagini allegate. |
| Struttura software | Architettura disaccoppiata tra presentazione, casi d’uso, dominio e adattatori esterni, documentata con OpenAPI. |
| Accesso e ruoli | Consultazione dei luoghi e tracking tramite codice pubblici. Invio di segnalazioni riservato all’Utente registrato autenticato. Autenticazione e controllo dei ruoli per preferiti, notifiche, storico personale e moderazione. |
| Qualità dei dati | Ogni proprietà di accessibilità registra fonte e timestamp. Dati assenti o con data di aggiornamento più vecchia di 30 giorni sono classificati come *Sconosciuti*. |
| Normativa | I trattamenti di dati personali seguono il GDPR. Esigenze di percorso, descrizioni e immagini delle segnalazioni possono rivelare, se associate a una persona, informazioni sulla disabilità o sulla salute (art. 9 GDPR). Minimizzazione dei dati e valutazione d’impatto sono a cura del responsabile del trattamento. WCAG 2.1 livello AA è il target tecnico del prodotto e non costituisce una dichiarazione di conformità legale. Gli obblighi applicabili dipendono dal soggetto erogatore. |

*Tabella 3.4: Vincoli di progetto*

### 3.5 MODELLI DI SISTEMA

#### 3.5.1 UTENTI DEL SISTEMA

| Utente | Obiettivi | Contesto d’uso |
|---|---|---|
| **Ospite** | Consultare luoghi, pianificare percorsi, seguire segnalazioni tramite codice e registrarsi. Non può inviare segnalazioni. | Utilizza le funzioni pubbliche senza account, prevalentemente da smartphone in mobilità (in stazione o alla fermata). |
| **Utente registrato** | Può usare le funzioni pubbliche, inviare segnalazioni, salvare luoghi preferiti, ricevere notifiche personalizzate e consultare lo storico delle proprie segnalazioni. | Pendolare abituale o cittadino con disabilità che utilizza quotidianamente il trasporto pubblico da smartphone o desktop. |
| **Amministratore** | Moderare le segnalazioni ricevute (conferma, rifiuto, risoluzione, unione duplicati) e gestire la sincronizzazione delle fonti dati autorizzate. | Operatore dell’ente di trasporto o amministratore della piattaforma che opera da postazione desktop tramite pannello riservato. |

*Tabella 3.5: Utenti del sistema e ruoli*

| Attore esterno | Ruolo |
|---|---|
| **Provider di routing** | Sistema esterno che calcola gli itinerari a partire da origine, destinazione e vincoli (UC-01). |
| **Fonte dati autorizzata** | Sistema esterno che pubblica i dataset ufficiali di fermate, linee e accessibilità, in formato GTFS (UC-06). |

*Tabella 3.6: Attori esterni*

#### 3.5.2 SCENARI

Gli scenari seguenti, inventati a scopo illustrativo, rappresentano una validazione preliminare dei requisiti e coprono utenti con diverse esigenze di accessibilità.

| Titolo | Attori | Obiettivo | Descrizione dello scenario |
|---|---|---|---|
| **Pianificazione di un percorso** | Ospite, Provider di routing | Confrontare percorsi senza presentare dati incerti come verificati. | Giorgio si muove in carrozzina e deve raggiungere l’ospedale. Dallo smartphone apre AEGIS senza registrarsi, indica origine, destinazione e l’esigenza di percorso senza barriere. Il sistema propone due alternative: una è compatibile, l’altra contiene un tratto contrassegnato come “non verificato” perché l’ultimo dato sull’ascensore è scaduto. Giorgio sceglie la prima. |
| **Consultazione di un luogo** | Ospite | Conoscere caratteristiche, stato e provenienza dei dati di una stazione. | Elena è non vedente e usa uno screen reader. Cerca la stazione dalla vista elenco testuale, naviga da tastiera fino alla scheda di dettaglio e ascolta per ogni caratteristica (ascensore, percorso tattile, annunci sonori) lo stato, la fonte e la data di aggiornamento. Il percorso tattile risulta “sconosciuto”: Elena sa che non è confermato. |
| **Invio di una segnalazione** | Utente registrato | Comunicare un guasto e ottenere un riferimento per seguirlo. | Sara accede al proprio account e apre il modulo di segnalazione. Seleziona la fermata, indica la categoria “ascensore guasto”, descrive il problema e allega una foto. Il sistema valida i dati e le mostra un codice di tracking, che lei salva. |
| **Tracking di una segnalazione** | Ospite | Verificare lo stato senza esporre dati riservati. | Dopo due giorni Sara inserisce il codice nella pagina di tracking e vede che la sua segnalazione è passata da “In verifica” a “Confermata”, con una nota pubblica dell’operatore. Non vede alcun dato personale, né altre segnalazioni. |
| **Registrazione e preferiti** | Ospite, Utente registrato | Creare un account e seguire i luoghi usati abitualmente. | Giorgio si registra con UC-09. Dopo l’accesso con UC-07, aggiunge ai preferiti la fermata sotto casa e attiva il consenso alle notifiche con UC-08. Quando una segnalazione su quella fermata viene confermata, riceve un avviso. Dall’area personale consulta lo storico delle sue segnalazioni. |
| **Moderazione di una segnalazione** | Amministratore | Valutare le segnalazioni in modo autorizzato e tracciabile. | Davide, operatore dell’ente di trasporto, accede al pannello di moderazione, apre la segnalazione di Sara con la foto e la cronologia e la conferma indicando la motivazione. Un’altra segnalazione sullo stesso guasto viene unita come duplicato. Ogni decisione resta nello storico. |
| **Aggiornamento di una fonte** | Amministratore, Fonte dati autorizzata | Mantenere aggiornati e validati i dataset ufficiali. | Davide avvia la sincronizzazione del nuovo pacchetto GTFS. Il sistema controlla formato, chiavi e coerenza geografica. Un primo tentativo fallisce perché il file è corrotto: i dati precedenti restano visibili e l’errore è registrato. Il secondo tentativo ha successo e le nuove fermate compaiono su mappa ed elenco. |

*Tabella 3.7: Scenari di utilizzo di AEGIS*

#### 3.5.3 MODELLI DEI CASI D’USO

##### Diagramma globale

Il confine di sistema separa i casi d’uso di AEGIS dagli attori umani e dai sistemi esterni. Ospite e Utente registrato sono specializzazioni dell’attore Utente della piattaforma. L’Ospite partecipa a UC-01, UC-02 e UC-04 e può registrarsi con UC-09. L’Utente registrato partecipa a UC-01, UC-02, UC-03 e UC-04. UC-03, UC-05, UC-06 e UC-08 includono UC-07 perché richiedono autenticazione. UC-06 descrive l’aggiornamento manuale avviato dall’Amministratore. La sincronizzazione schedulata è un processo interno automatico. I flussi alternativi e di errore sono descritti nelle specifiche dei casi d’uso.

```mermaid
usecase-beta
direction TB
accTitle: Diagramma UML dei casi d’uso di AEGIS
accDescr {
  Attori umani e sistemi esterni interagiscono con i nove casi d’uso di AEGIS.
  Ospite e Utente registrato specializzano Utente della piattaforma.
  Solo Ospite è associato a UC-09 Registrarsi.
  UC-03, UC-05, UC-06 e UC-08 includono l’autenticazione UC-07.
  La sincronizzazione schedulata è un processo interno automatico.
}
actor G("Ospite")
actor R("Utente registrato")
actor U("Utente della piattaforma")
actor A("Amministratore")
actor RP("Provider di routing") <<sistema esterno>>
actor DS("Fonte dati autorizzata") <<sistema esterno>>
systemBoundary aegis["AEGIS"]
  UC01("UC-01 Pianificare un viaggio")
  UC02("UC-02 Consultare un luogo")
  UC03("UC-03 Inviare una segnalazione")
  UC04("UC-04 Seguire una segnalazione")
  UC05("UC-05 Moderare una segnalazione")
  UC06("UC-06 Aggiornare una fonte")
  UC07("UC-07 Autenticarsi")
  UC08("UC-08 Gestire preferiti e storico")
  UC09("UC-09 Registrarsi")
end
G --|> U
R --|> U
U --> UC01
U --> UC02
R --> UC03
U --> UC04
G --> UC09
R --> UC07
R --> UC08
A --> UC05
A --> UC06
A --> UC07
RP --> UC01
DS --> UC06
UC05 ..> : include UC07
UC06 ..> : include UC07
UC03 ..> : include UC07
UC08 ..> : include UC07
```

*Figura 3.1: Diagramma UML dei casi d’uso di AEGIS*

##### Specifiche dei casi d’uso

###### UC-01: Pianificare un viaggio

| Campo | Specifica |
|---|---|
| Attori partecipanti | Ospite, Utente registrato, Provider di routing |
| Condizioni d’ingresso | Origine, destinazione ed esigenze di accessibilità specificate e valide. La consultazione non richiede autenticazione. |
| Condizioni d’uscita | Restituiti zero o più percorsi con tempi, cambi e stato di verifica. Fonte e freschezza sono esposte quando disponibili, dati assenti o scaduti risultano sconosciuti. |
| Requisiti qualitativi | Il sistema distingue barriere note, tratti compatibili e tratti non verificati (FR-05). Il p95 delle richieste dei flussi di mappa, luogo e percorso resta sotto 2 secondi nel profilo di test NFR-10. |
| Flusso degli eventi | 1. L’utente inserisce origine, destinazione ed esigenze.<br>2. Il sistema valida la richiesta e la inoltra al Provider di routing.<br>3. Il provider restituisce le alternative disponibili.<br>4. Il sistema arricchisce i percorsi con stato di verifica e provenienza dei dati e mostra risultati ed eventuali incertezze. |
| Flussi alternativi e di errore | • Input non valido: il sistema segnala i campi da correggere senza invocare il provider.<br>• Provider non disponibile o nessun percorso trovato: il sistema comunica l’indisponibilità senza presentare risultati non verificati come accessibili. |

###### UC-02: Consultare un luogo

| Campo | Specifica |
|---|---|
| Attori partecipanti | Ospite, Utente registrato |
| Condizioni d’ingresso | Luogo identificabile tramite ricerca testuale o selezione sulla mappa. |
| Condizioni d’uscita | Dettaglio e caratteristiche note sono mostrati con fonte e validità. Dati assenti o scaduti sono indicati come sconosciuti. |
| Requisiti qualitativi | Le informazioni sulla mappa sono disponibili anche in forma testuale per tastiera e screen reader (NFR-01, NFR-02). |
| Flusso degli eventi | 1. L’utente seleziona o cerca un luogo.<br>2. Il sistema recupera dettaglio e caratteristiche di accessibilità.<br>3. Il sistema valuta la freschezza rispetto alla scadenza e classifica come sconosciuti i dati scaduti o assenti.<br>4. Il sistema mostra caratteristiche, fonte, data di aggiornamento ed eventuali segnalazioni attive. |
| Flussi alternativi e di errore | • Luogo non trovato: il sistema mostra un avviso informativo.<br>• Errore di caricamento: il sistema comunica un errore temporaneo senza esporre dati obsoleti come validi. |

###### UC-03: Inviare una segnalazione

| Campo | Specifica |
|---|---|
| Attori partecipanti | Utente registrato |
| Condizioni d’ingresso | Utente autenticato tramite UC-07. Campi obbligatori compilati correttamente e informativa sul trattamento dei dati accettata. |
| Condizioni d’uscita | Segnalazione ed evento iniziale salvati, codice di tracking univoco restituito all’utente. |
| Requisiti qualitativi | Nessun dato identificativo dell’utente è esposto pubblicamente (NFR-04). L’eventuale foto è JPEG, PNG o WebP e non supera 5 MB (NFR-07). L’operazione richiede autenticazione e autorizzazione lato server (NFR-05). |
| Flusso degli eventi | 1. L’utente inserisce luogo, categoria, descrizione, data di osservazione e allega facoltativamente una foto.<br>2. Il sistema valida i campi e controlla formato e sicurezza dell’eventuale immagine.<br>3. Il sistema salva in un’unica transazione la segnalazione e l’evento iniziale, e genera un codice di tracking non enumerabile.<br>4. Il sistema restituisce il codice di tracking. |
| Flussi alternativi e di errore | • Campi mancanti o file non valido: il sistema rifiuta l’invio evidenziando l’errore, senza salvare dati parziali.<br>• Errore di persistenza: il sistema annulla la transazione e invita a riprovare.<br>• Limite di invii superato: il sistema rifiuta la richiesta e invita a riprovare più tardi. |

###### UC-04: Seguire una segnalazione

| Campo | Specifica |
|---|---|
| Attori partecipanti | Ospite, Utente registrato |
| Condizioni d’ingresso | Codice di tracking fornito dall’utente. |
| Condizioni d’uscita | Mostrato esclusivamente lo stato pubblico consentito della segnalazione, senza dati riservati. |
| Requisiti qualitativi | Il codice non consente di enumerare altre segnalazioni (FR-08), le richieste ripetute con codici errati sono limitate (NFR-06), pagina fruibile tramite tecnologie assistive (NFR-01). |
| Flusso degli eventi | 1. L’utente inserisce il proprio codice di tracking.<br>2. Il sistema verifica l’esistenza e la validità del codice.<br>3. Il sistema recupera lo stato corrente (In verifica, Confermata, Risolta, Rifiutata, Duplicata) e le note pubbliche.<br>4. Il sistema visualizza le informazioni a schermo. |
| Flussi alternativi e di errore | • Codice non riconosciuto o errato: il sistema mostra un messaggio generico di codice non valido, senza rivelare dettagli sull’archivio. |

###### UC-05: Moderare una segnalazione

| Campo | Specifica |
|---|---|
| Attori partecipanti | Amministratore |
| Condizioni d’ingresso | Amministratore autenticato (UC-07) e autorizzato, segnalazione esistente. |
| Condizioni d’uscita | Stato ed evento storico aggiornati nella stessa transazione con autore e timestamp. |
| Requisiti qualitativi | Tracciabilità completa di ogni decisione, gli eventi precedenti restano immutabili, controllo del ruolo lato server (NFR-05). |
| Flusso degli eventi | 1. L’amministratore seleziona una segnalazione dalla coda di moderazione.<br>2. Il sistema mostra dettagli, foto allegata e cronologia degli eventi.<br>3. L’amministratore seleziona la decisione (Conferma, Rifiuta, Risolvi o Unisci duplicato indicando la segnalazione di riferimento) e inserisce la motivazione.<br>4. Il sistema verifica il ruolo e la validità della transizione, aggiorna la segnalazione, aggiunge l’evento allo storico e pianifica le notifiche previste (FR-12). |
| Flussi alternativi e di errore | • Utente non autenticato o non autorizzato: il sistema risponde 401/403.<br>• Transizione non ammessa o segnalazione inesistente: il sistema rifiuta il comando lasciando invariato lo stato precedente. |

###### UC-06: Aggiornare una fonte

| Campo | Specifica |
|---|---|
| Attori partecipanti | Amministratore, Fonte dati autorizzata |
| Condizioni d’ingresso | Fonte configurata e abilitata. Amministratore autenticato e autorizzato a gestire le fonti. |
| Condizioni d’uscita | Versione, timestamp, controlli ed esito registrati, solo un pacchetto completo e validato diventa visibile. |
| Requisiti qualitativi | In caso di errore mantengono validità i dati precedenti, senza pubblicazioni parziali (NFR-09). |
| Flusso degli eventi | 1. L’Amministratore avvia la sincronizzazione dalla dashboard.<br>2. Il sistema acquisisce i dati dalla Fonte dati autorizzata.<br>3. Il sistema verifica formato, chiavi e coerenza geografica.<br>4. Se i controlli sono superati, il sistema pubblica i nuovi dati in un’unica operazione atomica e registra l’esito positivo. |
| Flussi alternativi e di errore | • Fonte non raggiungibile, file corrotto o controlli non superati: il sistema annulla la pubblicazione, registra l’errore nel log e mantiene attivi i dati precedenti. |

###### UC-07: Autenticarsi

| Campo | Specifica |
|---|---|
| Attori partecipanti | Utente registrato, Amministratore |
| Condizioni d’ingresso | Account esistente e credenziali disponibili. |
| Condizioni d’uscita | Sessione autenticata con i ruoli dell’utente. |
| Requisiti qualitativi | Comunicazioni su HTTPS e controllo degli accessi per ruolo (NFR-05), messaggi di errore che non rivelano se l’email esiste, moduli fruibili con tecnologie assistive (NFR-01). |
| Flusso degli eventi | 1. L’utente inserisce le credenziali.<br>2. Il sistema verifica le credenziali e avvia la sessione.<br>3. Il sistema restituisce i ruoli dell’utente. |
| Flussi alternativi e di errore | • Credenziali errate: il sistema mostra un messaggio generico, senza avviare la sessione. |

###### UC-08: Gestire preferiti e storico

| Campo | Specifica |
|---|---|
| Attori partecipanti | Utente registrato |
| Condizioni d’ingresso | Utente autenticato (UC-07). |
| Condizioni d’uscita | Preferiti e consensi aggiornati, oppure storico delle proprie segnalazioni mostrato con i relativi stati. |
| Requisiti qualitativi | L’utente accede solo ai propri dati (NFR-04, NFR-05), il consenso alle notifiche è revocabile in ogni momento. |
| Flusso degli eventi | 1. L’utente apre la propria area personale.<br>2. Il sistema mostra luoghi preferiti, consensi per le notifiche e storico delle segnalazioni inviate con il codice e lo stato.<br>3. L’utente aggiunge o rimuove un preferito oppure modifica il consenso alle notifiche.<br>4. Il sistema salva la modifica e aggiorna la vista. |
| Flussi alternativi e di errore | • Sessione non valida o scaduta: il sistema richiede nuovamente l’autenticazione.<br>• Errore di salvataggio: il sistema mantiene lo stato precedente e invita a riprovare. |

###### UC-09: Registrarsi

| Campo | Specifica |
|---|---|
| Attori partecipanti | Ospite |
| Condizioni d’ingresso | Email non ancora in uso e informativa sul trattamento dei dati accettata. |
| Condizioni d’uscita | Account creato. L’accesso autenticato avviene tramite UC-07. |
| Requisiti qualitativi | Comunicazioni su HTTPS, messaggi di errore che non rivelano se l’email esiste e modulo fruibile con tecnologie assistive (NFR-01, NFR-05). |
| Flusso degli eventi | 1. L’Ospite inserisce i dati richiesti.<br>2. Il sistema valida i dati e crea l’account.<br>3. Il sistema conferma la registrazione. |
| Flussi alternativi e di errore | • Email già in uso o dati non validi: il sistema evidenzia i campi da correggere e non crea l’account. |

*Tabella 3.8: Specifiche dei casi d’uso*

#### 3.5.4 MODELLO DEGLI OGGETTI

##### Oggetti Entity

| Oggetto | Responsabilità | Casi d’uso |
|---|---|---|
| Place | Rappresenta un luogo, una fermata o una stazione consultabile con posizione geografica. | UC-01, UC-02, UC-06 |
| AccessibilityFeature | Rappresenta una caratteristica di accessibilità associata a un luogo, con stato (Disponibile, Non disponibile, Sconosciuto), fonte, data di osservazione e validità. | UC-01, UC-02, UC-06 |
| Report | Rappresenta una segnalazione georeferenziata con categoria, descrizione, data di osservazione, stato, codice di tracking e autore registrato obbligatorio. Per i duplicati conserva il riferimento alla segnalazione originale. | UC-03, UC-04, UC-05, UC-08 |
| ReportAttachment | Rappresenta la foto allegata a una segnalazione, con chiave di archiviazione ed esito della scansione. | UC-03, UC-05 |
| ReportEvent | Registra in modo append-only tipo di evento, autore, motivazione e data relativi a una segnalazione. | UC-03, UC-05 |
| UserAccount | Rappresenta l’identità e i ruoli applicativi di un utente autenticato. | UC-05, UC-06, UC-07, UC-08, UC-09 |
| FavoritePlace | Collega un utente registrato a un luogo preferito e memorizza il consenso alle notifiche. | UC-05, UC-08 |
| DataSource | Rappresenta una fonte autorizzata e la relativa configurazione di validità. | UC-06 |
| SourceSync | Registra versione, esito, timestamp e controlli di qualità di una sincronizzazione. | UC-06 |
| TransitLine | Rappresenta una linea di trasporto importata da una fonte autorizzata. | UC-01, UC-06 |
| TransitStop | Rappresenta una fermata con coordinate geografiche e riferimenti alle linee servite. | UC-01, UC-02, UC-06 |

*Tabella 3.9: Oggetti Entity*

##### Oggetti Boundary

| Oggetto | Responsabilità |
|---|---|
| WebInterface | Presenta mappa interattiva, viste testuali equivalenti, percorsi, moduli di segnalazione, area personale e pannello di amministrazione agli attori umani. |
| PlacesAccessibilityAPI | Riceve richieste di ricerca e dettaglio sui luoghi e restituisce dati con fonte e freschezza. |
| RoutesAPI | Riceve origine, destinazione e vincoli di accessibilità e restituisce alternative con stato di verifica. |
| ReportsTrackingAPI | Riceve nuove segnalazioni e richieste di tracking, restituendo il codice o lo stato pubblico consentito. |
| ModerationAPI | Espone la coda e le operazioni di moderazione, protette da autenticazione e ruolo. |
| SourceAdminAPI | Espone l’avvio e lo stato delle sincronizzazioni delle fonti dati, riservato agli amministratori. |
| AccountAPI | Espone registrazione, accesso, preferiti, consensi e storico delle proprie segnalazioni. |
| StorageScanAdapter | Interagisce con il servizio di archiviazione e scansione per verificare le immagini allegate prima della pubblicazione. |
| RoutingProviderAdapter | Inoltra i parametri di viaggio al provider esterno di routing e acquisisce gli itinerari calcolati. |
| NotificationChannelAdapter | Inoltra le notifiche autorizzate al canale esterno (email o push) registrando l’esito di invio. |
| DataSourceAdapter | Acquisisce i flussi dati dalle fonti ufficiali autorizzate per aggiornare linee, fermate e caratteristiche. |

*Tabella 3.10: Oggetti Boundary*

##### Oggetti Control

| Oggetto | Responsabilità |
|---|---|
| PlanRouteControl | Coordina validazione della richiesta, chiamata al provider di routing e classificazione dell’accessibilità delle tratte. |
| GetPlaceAccessibilityControl | Recupera le caratteristiche di un luogo e applica le regole di scadenza e freschezza. |
| CreateReportControl | Coordina validazione dei dati, verifica dell’eventuale allegato e salvataggio atomico di segnalazione ed evento iniziale. |
| TrackReportControl | Verifica il codice di tracking e filtra le informazioni pubbliche restituite. |
| ModerateReportControl | Verifica il ruolo, valida le transizioni di stato e registra aggiornamento ed evento append-only nella stessa transazione. |
| SynchronizeSourceControl | Coordina importazione, controlli di qualità e pubblicazione atomica dei dataset ufficiali. |
| AuthenticateUserControl | Gestisce registrazione, verifica delle credenziali e rilascio della sessione con i ruoli. |
| ManageProfileControl | Gestisce preferiti, consensi per le notifiche e storico delle segnalazioni dell’utente autenticato. |

*Tabella 3.11: Oggetti Control*

#### 3.5.5 DIAGRAMMA DELLE CLASSI

Il diagramma rappresenta la struttura statica concettuale del dominio: classi, attributi, enumerazioni e associazioni. Metodi, operazioni e navigabilità sono rimandati alla progettazione. Nelle associazioni `1..1` indica un legame obbligatorio con un solo oggetto, `0..1` un legame facoltativo e `*` un numero non limitato di oggetti. Le coordinate geografiche sono modellate come *value object* (`GeoPoint`) e compaiono come attributo `position` nelle classi che le usano.

```mermaid
classDiagram
  accTitle: Diagramma UML delle classi del dominio di AEGIS
  accDescr {
    Classi Entity del dominio con attributi principali, enumerazioni e associazioni.
    Mostra Place, AccessibilityFeature, Report, ReportEvent, UserAccount, DataSource, TransitLine e TransitStop con le relative molteplicità.
  }
  class Place {
    <<entity>>
    PlaceId id
    String name
    PlaceType type
    GeoPoint position
  }
  class AccessibilityFeature {
    <<entity>>
    FeatureType type
    FeatureState state
    Instant observedAt
    Instant validUntil
  }
  class FeatureState {
    <<enumeration>>
    AVAILABLE
    NOT_AVAILABLE
    UNKNOWN
  }
  class Report {
    <<entity>>
    ReportId id
    ReportCategory category
    String description
    Instant observedAt
    Instant createdAt
    ReportStatus status
    String trackingCode
    GeoPoint position
  }
  class ReportStatus {
    <<enumeration>>
    IN_REVIEW
    CONFIRMED
    REJECTED
    DUPLICATE
    RESOLVED
  }
  class ReportAttachment {
    <<entity>>
    String storageKey
    String mimeType
    ScanResult scanResult
  }
  class ScanResult {
    <<enumeration>>
    PENDING
    CLEAN
    REJECTED
    UNAVAILABLE
  }
  class ReportEvent {
    <<entity>>
    ReportEventId id
    ReportEventType type
    String motivation
    Instant occurredAt
  }
  class ReportEventType {
    <<enumeration>>
    CREATED
    CONFIRMED
    REJECTED
    MARKED_DUPLICATE
    RESOLVED
  }
  class GeoPoint {
    <<value object>>
    double latitude
    double longitude
  }
  class UserAccount {
    <<entity>>
    UserId id
    String email
    Set~Role~ roles
  }
  class Role {
    <<enumeration>>
    REGISTERED_USER
    ADMINISTRATOR
  }
  class FavoritePlace {
    <<entity>>
    Boolean notificationsConsent
    Instant addedAt
  }
  class DataSource {
    <<entity>>
    SourceId id
    String name
    String version
    Duration validity
  }
  class SourceSync {
    <<entity>>
    SyncResult result
    Instant completedAt
  }
  class TransitLine {
    <<entity>>
    TransitLineId id
    String name
  }
  class TransitStop {
    <<entity>>
    TransitStopId id
    GeoPoint position
  }

  Place "1" *-- "0..*" AccessibilityFeature : possiede
  Place "1" -- "0..*" TransitStop : ospita
  TransitLine "1..*" -- "2..*" TransitStop : serve
  Report "1" *-- "1..*" ReportEvent : registra
  Report "0..*" -- "0..1" Place : riguarda
  Report "1" *-- "0..1" ReportAttachment : allega
  Report "0..*" -- "0..1" Report : duplicato di
  Report "0..*" -- "1" UserAccount : inviata da
  ReportEvent "0..*" -- "0..1" UserAccount : eseguito da
  UserAccount "1" *-- "0..*" FavoritePlace : segue
  FavoritePlace "0..*" -- "1" Place : riferito a
  AccessibilityFeature "0..*" -- "1" DataSource : proviene da
  TransitLine "0..*" -- "1" DataSource : importata da
  TransitStop "0..*" -- "1" DataSource : importata da
  SourceSync "0..*" -- "1" DataSource : aggiorna
```

*Figura 3.2: Diagramma UML delle classi del sistema*

Ogni `Report` ha un autore `UserAccount` obbligatorio, perché UC-03 è riservato all’Utente registrato autenticato. L’identità dell’autore è visibile solo all’autore e agli amministratori, mai nelle risposte pubbliche (NFR-04). Il tipo `ReportEventType` comprende l’evento iniziale (CREATED) e le decisioni di moderazione (CONFIRMED, REJECTED, MARKED_DUPLICATE, RESOLVED).

#### 3.5.6 DIAGRAMMI DI SEQUENZA

I diagrammi descrivono le interazioni dinamiche tra attori, oggetti Boundary, Control ed Entity per i casi d’uso. In questa fase di analisi le Entity sono interrogate direttamente dai Control: la scelta di eventuali repository è rimandata al System Design.

##### UC-01: Pianificazione di un viaggio

```mermaid
sequenceDiagram
  accTitle: Diagramma di sequenza per UC-01 Pianificare un viaggio
  accDescr {
    L’utente richiede un percorso. Il controllo valida l’input, invoca il provider di routing, arricchisce i risultati con lo stato di accessibilità e gestisce input non valido, provider non disponibile o nessun percorso.
  }
  actor U as Ospite / Utente registrato
  participant UI as WebInterface
  participant API as RoutesAPI
  participant UC as PlanRouteControl
  participant RP as RoutingProviderAdapter
  participant AF as AccessibilityFeature
  U->>UI: Inserisce origine, destinazione ed esigenze
  UI->>API: Richiesta calcolo percorso
  API->>UC: validateAndPlan(params)
  alt Input non valido
    UC-->>API: Errore di validazione
    API-->>UI: 400 Bad Request
    UI-->>U: Evidenzia i campi da correggere
  else Input valido
    UC->>RP: computeRoutes(params)
    alt Provider disponibile e percorsi trovati
      RP-->>UC: Alternative e tratte
      UC->>AF: getFeaturesAndCheckFreshness(tratte)
      AF-->>UC: Caratteristiche verificate o Sconosciute
      UC-->>API: Percorsi con stato di verifica
      API-->>UI: Risultati percorsi
      UI-->>U: Mostra alternative e tratti non verificati
    else Provider non disponibile o nessun percorso
      RP-->>UC: Errore o lista vuota
      UC-->>API: Esito non disponibile
      API-->>UI: Messaggio informativo
      UI-->>U: Comunica indisponibilità senza dati non verificati
    end
  end
```

*Figura 3.3: Diagramma di sequenza per UC-01*

##### UC-02: Consultazione di un luogo

```mermaid
sequenceDiagram
  accTitle: Diagramma di sequenza per UC-02 Consultare un luogo
  accDescr {
    L’utente seleziona un luogo. Il controllo recupera dettaglio e caratteristiche di accessibilità, applica la valutazione di freschezza e gestisce luogo non trovato.
  }
  actor U as Ospite / Utente registrato
  participant UI as WebInterface
  participant API as PlacesAccessibilityAPI
  participant UC as GetPlaceAccessibilityControl
  participant P as Place
  participant AF as AccessibilityFeature
  U->>UI: Seleziona un luogo su mappa o elenco
  UI->>API: Richiede dettaglio luogo (placeId)
  API->>UC: getPlaceDetails(placeId)
  UC->>P: findById(placeId)
  alt Luogo trovato
    P-->>UC: Dati luogo
    UC->>AF: getFeaturesAndCheckFreshness(placeId)
    AF-->>UC: Caratteristiche (verificate o Sconosciute)
    UC-->>API: Scheda dettaglio completa
    API-->>UI: Dati luogo e accessibilità
    UI-->>U: Mostra caratteristiche, fonte e aggiornamento
  else Luogo non trovato
    P-->>UC: NotFound
    UC-->>API: Errore luogo inesistente
    API-->>UI: 404 Not Found
    UI-->>U: Mostra avviso luogo non trovato
  end
```

*Figura 3.4: Diagramma di sequenza per UC-02*

##### UC-03: Invio di una segnalazione

```mermaid
sequenceDiagram
  accTitle: Diagramma di sequenza per UC-03 Inviare una segnalazione
  accDescr {
    L’utente autenticato invia una segnalazione. Il sistema verifica sessione, valida dati, controlla l’eventuale allegato, salva segnalazione ed evento iniziale in transazione e restituisce il codice di tracking.
  }
  actor U as Utente registrato
  participant UI as WebInterface
  participant API as ReportsTrackingAPI
  participant UC as CreateReportControl
  participant ST as StorageScanAdapter
  participant R as Report
  participant E as ReportEvent
  U->>UI: Accede e compila il modulo con eventuale foto
  UI->>API: Invia segnalazione
  API->>API: Verifica sessione
  alt Sessione assente o non valida
    API-->>UI: 401 Unauthorized
    UI-->>U: Richiede l’accesso
  else Sessione valida
    API->>UC: createReport(reportData, attachment, userId)
    alt Campi obbligatori mancanti
      UC-->>API: Errore di validazione
      API-->>UI: 400 Bad Request
      UI-->>U: Evidenzia i campi da correggere
    else Campi validi
      opt Foto allegata
        UC->>ST: uploadAndScan(attachment)
        ST-->>UC: Esito scansione (storageKey oppure errore)
      end
      alt Allegato non valido
        UC-->>API: Rifiuta segnalazione
        API-->>UI: Errore allegato non valido
        UI-->>U: Mostra messaggio di errore
      else Allegato assente o valido
        Note over UC,E: Transazione database per segnalazione ed evento iniziale
        UC->>R: <<create>> save(reportData, storageKey, userId)
        R-->>UC: reportId, trackingCode
        UC->>E: <<create>> appendInitialEvent(reportId)
        E-->>UC: Evento salvato
        UC-->>API: trackingCode
        API-->>UI: Conferma invio e trackingCode
        UI-->>U: Mostra conferma e codice di tracking
      end
    end
  end
```

*Figura 3.5: Diagramma di sequenza per UC-03*

##### UC-04: Tracking di una segnalazione

```mermaid
sequenceDiagram
  accTitle: Diagramma di sequenza per UC-04 Seguire una segnalazione
  accDescr {
    L’utente inserisce un codice di tracking. Il controllo verifica il codice e restituisce solo lo stato pubblico consentito o un messaggio generico di codice non valido.
  }
  actor U as Ospite / Utente registrato
  participant UI as WebInterface
  participant API as ReportsTrackingAPI
  participant UC as TrackReportControl
  participant R as Report
  U->>UI: Inserisce codice di tracking
  UI->>API: Richiede stato (trackingCode)
  API->>UC: trackByCode(trackingCode)
  UC->>R: findByTrackingCode(trackingCode)
  alt Codice valido
    R-->>UC: Report trovato
    UC->>R: getPublicSummary()
    R-->>UC: Stato e note pubbliche (senza dati personali)
    UC-->>API: Riepilogo stato
    API-->>UI: 200 OK
    UI-->>U: Mostra stato aggiornato della segnalazione
  else Codice non valido o inesistente
    R-->>UC: NotFound
    UC-->>API: Codice non riconosciuto
    API-->>UI: Risposta generica
    UI-->>U: Mostra messaggio codice non valido
  end
```

*Figura 3.6: Diagramma di sequenza per UC-04*

##### UC-05: Moderazione di una segnalazione

```mermaid
sequenceDiagram
  accTitle: Diagramma di sequenza per UC-05 Moderare una segnalazione
  accDescr {
    L’amministratore autorizzato seleziona una decisione. Il controllo verifica ruolo e transizione, aggiorna stato ed evento in transazione e pianifica la notifica autorizzata.
  }
  actor A as Amministratore
  participant UI as WebInterface
  participant API as ModerationAPI
  participant UC as ModerateReportControl
  participant R as Report
  participant E as ReportEvent
  participant N as NotificationChannelAdapter
  A->>UI: Seleziona decisione e inserisce motivazione
  UI->>API: Invia comando moderazione
  API->>API: Verifica sessione e ruolo ADMINISTRATOR
  alt Utente non autenticato o non autorizzato
    API-->>UI: 401 Unauthorized / 403 Forbidden
    UI-->>A: Mostra errore di accesso
  else Utente autorizzato
    API->>UC: moderateReport(reportId, decision, motivation, targetReportId, adminId)
    UC->>R: findById(reportId)
    alt Report esistente
      R-->>UC: Stato corrente
      UC->>UC: Verifica transizione ammessa
      alt Transizione valida
        Note over UC,E: Transazione atomica
        UC->>R: updateStatus(decision)
        R-->>UC: Stato aggiornato
        UC->>E: <<create>> appendEvent(reportId, decision, motivation, adminId)
        E-->>UC: Evento registrato
        opt Utenti con consenso alle notifiche
          UC->>N: enqueueStatusUpdate(reportId, decision)
          N-->>UC: Intenzione registrata
        end
        Note over N: Invio asincrono dopo il commit
        UC-->>API: Conferma moderazione
        API-->>UI: Esito positivo
        UI-->>A: Aggiorna coda e mostra conferma
      else Transizione non ammessa
        UC-->>API: Operazione rifiutata
        API-->>UI: Errore di validazione
        UI-->>A: Mostra messaggio di errore
      end
    else Report inesistente
      R-->>UC: NotFound
      UC-->>API: Errore report inesistente
      API-->>UI: 404 Not Found
      UI-->>A: Mostra messaggio di errore
    end
  end
```

*Figura 3.7: Diagramma di sequenza per UC-05*

##### UC-06: Aggiornamento di una fonte

La sequenza rappresenta l’aggiornamento manuale di una fonte da parte dell’Amministratore autenticato. L’esecuzione schedulata è un processo interno automatico e non fa parte di questa interazione con un attore.

```mermaid
sequenceDiagram
  accTitle: Diagramma di sequenza per UC-06 Aggiornare una fonte
  accDescr {
    L’amministratore avvia la sincronizzazione. Il controllo acquisisce il dataset, esegue i controlli di qualità, pubblica atomicamente i dati validi o registra l’errore mantenendo i dati precedenti.
  }
  actor A as Amministratore
  participant UI as WebInterface
  participant API as SourceAdminAPI
  participant UC as SynchronizeSourceControl
  participant DS as DataSourceAdapter
  participant SS as SourceSync
  A->>UI: Avvia sincronizzazione fonte
  UI->>API: Richiede sync (sourceId)
  API->>API: Verifica sessione e ruolo ADMINISTRATOR
  alt Utente non autorizzato
    break Accesso negato
      API-->>UI: 401 Unauthorized / 403 Forbidden
      UI-->>A: Mostra errore di accesso
    end
  else Utente autorizzato
    API->>UC: synchronize(sourceId)
  end
  UC->>DS: fetchLatestDataset(sourceId)
  alt Fonte non raggiungibile
    DS-->>UC: Errore acquisizione
    UC->>SS: <<create>> recordSyncFailure(sourceId, error)
    SS-->>UC: Errore registrato
    UC-->>API: Errore sincronizzazione
    API-->>UI: Messaggio di errore
    UI-->>A: Segnala fallimento, dati attivi invariati
  else Dataset acquisito
    DS-->>UC: Pacchetto dati e versione
    UC->>UC: Verifica formato, chiavi e coerenza geografica
    alt Controlli superati
      Note over UC,SS: Pubblicazione atomica
      UC->>SS: <<create>> recordSyncSuccess(sourceId, version)
      SS-->>UC: Esito registrato
      UC-->>API: Sincronizzazione completata
      API-->>UI: Conferma aggiornamento
      UI-->>A: Mostra stato aggiornato della fonte
    else Dati corrotti o incoerenti
      UC->>SS: <<create>> recordSyncFailure(sourceId, error)
      SS-->>UC: Errore registrato
      UC-->>API: Errore validazione
      API-->>UI: Messaggio di errore
      UI-->>A: Segnala fallimento, dati attivi invariati
    end
  end
```

*Figura 3.8: Diagramma di sequenza per UC-06*

##### UC-07: Autenticazione

```mermaid
sequenceDiagram
  accTitle: Diagramma di sequenza per UC-07 Autenticarsi
  accDescr {
    L’utente inserisce le credenziali. Il controllo verifica l’identità e avvia la sessione con i ruoli oppure restituisce un errore generico.
  }
  actor U as Utente registrato / Amministratore
  participant UI as WebInterface
  participant API as AccountAPI
  participant UC as AuthenticateUserControl
  participant UA as UserAccount
  U->>UI: Inserisce le credenziali
  UI->>API: Richiede l'accesso
  API->>UC: authenticate(credentials)
  UC->>UA: verifyCredentials(credentials)
  alt Credenziali valide
    UA-->>UC: Utente e ruoli
    UC-->>API: Sessione e ruoli
    API-->>UI: 200 OK
    UI-->>U: Accesso effettuato
  else Credenziali errate
    UA-->>UC: Verifica fallita
    UC-->>API: Accesso negato
    API-->>UI: 401 Unauthorized
    UI-->>U: Messaggio generico di credenziali non valide
  end
```

*Figura 3.9: Diagramma di sequenza per UC-07*

##### UC-08: Gestione di preferiti e storico

```mermaid
sequenceDiagram
  accTitle: Diagramma di sequenza per UC-08 Gestire preferiti e storico
  accDescr {
    L’utente autenticato consulta preferiti e storico delle proprie segnalazioni. Può aggiornare preferiti e consensi, con verifica della sessione e gestione degli errori di salvataggio.
  }
  actor U as Utente registrato
  participant UI as WebInterface
  participant API as AccountAPI
  participant UC as ManageProfileControl
  participant FP as FavoritePlace
  participant R as Report
  U->>UI: Apre l'area personale
  UI->>API: Richiede preferiti e storico
  API->>API: Verifica sessione
  alt Sessione non valida
    API-->>UI: 401 Unauthorized
    UI-->>U: Richiede nuova autenticazione
  else Sessione valida
    API->>UC: getProfile(userId)
    UC->>FP: findByUser(userId)
    FP-->>UC: Luoghi preferiti e consensi
    UC->>R: findByAuthor(userId)
    R-->>UC: Segnalazioni proprie con stato
    UC-->>API: Dati area personale
    API-->>UI: 200 OK
    UI-->>U: Mostra preferiti, consensi e storico
    opt L'utente modifica preferiti o consensi
      U->>UI: Aggiunge o rimuove un preferito, aggiorna il consenso
      UI->>API: Richiede modifica
      API->>UC: updateFavorites(userId, changes)
      UC->>FP: save(changes)
      FP-->>UC: Modifica salvata
      UC-->>API: Conferma
      API-->>UI: Esito positivo
      UI-->>U: Mostra la vista aggiornata
    end
  end
```

*Figura 3.10: Diagramma di sequenza per UC-08*

##### UC-09: Registrazione

```mermaid
sequenceDiagram
  accTitle: Diagramma di sequenza per UC-09 Registrarsi
  accDescr {
    L’ospite richiede la registrazione. Il controllo valida i dati, crea l’account se l’email è disponibile e restituisce conferma o errore di validazione.
  }
  actor G as Ospite
  participant UI as WebInterface
  participant API as AccountAPI
  participant UC as AuthenticateUserControl
  participant UA as UserAccount
  G->>UI: Inserisce i dati di registrazione
  UI->>API: Richiede la registrazione
  API->>UC: register(data)
  UC->>UA: createAccount(data)
  alt Dati validi ed email libera
    UA-->>UC: Account creato
    UC-->>API: Esito positivo
    API-->>UI: 201 Created
    UI-->>G: Conferma la registrazione
  else Dati non validi o email già in uso
    UA-->>UC: Errore di validazione
    UC-->>API: Registrazione rifiutata
    API-->>UI: 400 Bad Request
    UI-->>G: Evidenzia i campi da correggere
  end
```

*Figura 3.11: Diagramma di sequenza per UC-09*

##### Diagramma degli stati di Report

La macchina a stati applica le transizioni di FR-09. Solo l’Amministratore autenticato può confermare, rifiutare, risolvere una segnalazione confermata o associarla come duplicata. Ogni transizione registra autore e timestamp. Gli stati Rifiutata, Duplicata e Risolta sono terminali. La transizione a Duplicata richiede il riferimento alla segnalazione principale.

```mermaid
stateDiagram-v2
  accTitle: Diagramma degli stati della segnalazione Report
  accDescr {
    Stati e transizioni ammesse per una segnalazione. Da In verifica può passare a Confermata, Rifiutata o Duplicata. Da Confermata può passare a Risolta. Rifiutata, Duplicata e Risolta sono terminali.
  }
  state "In verifica" as InVerifica
  state "Confermata" as Confermata
  state "Rifiutata" as Rifiutata
  state "Duplicata" as Duplicata
  state "Risolta" as Risolta

  [*] --> InVerifica
  InVerifica --> Confermata: conferma
  InVerifica --> Rifiutata: rifiuto
  InVerifica --> Duplicata: unione come duplicato
  Confermata --> Risolta: risoluzione
  Rifiutata --> [*]
  Duplicata --> [*]
  Risolta --> [*]
```

*Figura 3.12: Diagramma degli stati della segnalazione `Report`*

## INTERFACCIA UTENTE

### 4.1 DESCRIZIONE DEL PROTOTIPO REALIZZATO

| Aspetto | Descrizione |
|---|---|
| Scopo (ruolo, interfaccia, implementazione) | Non valutabile: nel repository non è presente un prototipo navigabile o implementato, quindi non sono documentabili ruolo, viste e componenti realizzati. |
| Modo d’uso (statico, dinamico, interattivo) | Non disponibile in assenza del prototipo. |
| Fedeltà (bassa o alta) | Non valutabile in assenza del prototipo. |
| Completezza funzionale (orizzontale o verticale) | Nessuna funzionalità di prototipo è presente nel repository. |
| Durata (usa e getta, evolutivo, incrementale, interattivo) | Non determinabile senza il prototipo e il relativo piano di evoluzione. |

*Tabella 4.1: Caratteristiche del prototipo*

### 4.2 TEST DI USABILITÀ

#### 4.2.1 Obiettivi del test

La prova è pianificata ma non ancora svolta. Verificherà i criteri del RAD per tastiera, screen reader e vista testuale (NFR-01, NFR-02) e misurerà completamento autonomo, tempi, errori e richieste d’aiuto. Il target è almeno 17 compiti completati senza aiuto su 20 tentativi (85%, NFR-11).

#### 4.2.2 Metodologia

Il protocollo prevede 5 partecipanti appartenenti al target del sistema. Ogni partecipante svolge i quattro compiti della Tabella 4.2, per un totale di 20 tentativi. I partecipanti sono identificati con codici anonimi. Per ciascun compito si registrano tempo, completamento senza aiuto, errori e osservazioni. I risultati non sono ancora raccolti perché nel repository non è presente il prototipo necessario alla prova.

Compiti proposti, derivati dagli scenari della Tabella 3.7:

| Compito | Scenario di riferimento | Caso d’uso |
|---|---|---|
| Trovare un percorso accessibile tra due punti e riconoscere i tratti non verificati. | Pianificazione di un percorso | UC-01 |
| Consultare stato, fonte e aggiornamento di una caratteristica di una stazione. | Consultazione di un luogo | UC-02 |
| Inviare una segnalazione con foto e annotare il codice di tracking. | Invio di una segnalazione | UC-03 |
| Consultare lo stato di una segnalazione tramite codice. | Tracking di una segnalazione | UC-04 |

*Tabella 4.2: Compiti del test*

#### 4.2.3 Sintesi delle misure

| Misura | Metodo | Obiettivo | Risultato |
|---|---|---|---|
| Completamento autonomo | Compiti conclusi senza aiuto / 20 | Almeno 17 su 20 (85%) | Non raccolto |
| Tempo per compito | Secondi dall’avvio al completamento, registrati per ciascun tentativo | Rilevare per tutti i 20 tentativi | Non raccolto |
| Errori e richieste d’aiuto | Conteggio e descrizione per compito | Registrare tutti gli eventi osservati | Non raccolto |

*Tabella 4.3: Misure previste e stato della raccolta*

#### 4.2.4 Analisi dei risultati

L’analisi non è ancora disponibile perché non sono state svolte sessioni. Dopo la raccolta, ogni problema sarà identificato, classificato per gravità e collegato alle osservazioni e ai compiti interessati.

#### 4.2.5 Sintesi delle interviste

Non sono disponibili interviste post-test. Le risposte saranno sintetizzate dopo le sessioni previste.

#### 4.2.6 Raccomandazioni finali

Le raccomandazioni saranno definite in base ai problemi osservati e ordinate per priorità dopo l’esecuzione dei test.

### 4.3 VALUTAZIONE DELL’USABILITÀ SECONDO ISO 9241-110

Ogni principio è valutato da ciascun valutatore con un voto da 0 (minimo) a 4 (massimo). Il voto riportato è la media degli n valutatori. I voti sono visualizzati in un diagramma a stella che rappresenta il profilo di qualità del sistema e, se possibile, di un sistema simile per confronto.

| Principio | Voto medio AEGIS | Voto medio sistema di confronto | Commento |
|---|---|---|---|
| Adeguatezza al compito | Non valutato | Non selezionato | Valutazione non svolta |
| Autodescrizione | Non valutato | Non selezionato | Valutazione non svolta |
| Conformità alle aspettative | Non valutato | Non selezionato | Valutazione non svolta |
| Apprendibilità | Non valutato | Non selezionato | Valutazione non svolta |
| Controllabilità | Non valutato | Non selezionato | Valutazione non svolta |
| Robustezza agli errori d’uso | Non valutato | Non selezionato | Valutazione non svolta |
| Coinvolgimento dell’utente | Non valutato | Non selezionato | Valutazione non svolta |

*Tabella 4.4: Valutazione per principi ISO 9241-110*

Il diagramma a stella sarà generato quando saranno disponibili i voti dei valutatori.

### 4.4 ALLEGATI

Nel repository non sono presenti allegati di test. Questionari, griglie di osservazione e altri materiali raccolti saranno aggiunti dopo le sessioni.

## GLOSSARIO

| Termine | Definizione |
|---|---|
| **RAD** | *Requirements Analysis Document*, Documento di Analisi dei Requisiti. |
| **TPL** | Trasporto Pubblico Locale (metropolitane, autobus, tram, ferrovie urbane). |
| **WCAG 2.1 AA** | *Web Content Accessibility Guidelines*, criteri W3C di accessibilità dei contenuti web di livello A e AA. È il target tecnico del prodotto. |
| **ARIA** | *Accessible Rich Internet Applications*, insieme di attributi che rendono comprensibili agli screen reader i componenti dinamici delle pagine web. |
| **Legge Stanca** | Legge 4/2004 sull’accessibilità degli strumenti informatici per le persone con disabilità. |
| **GDPR** | Regolamento Europeo sulla protezione dei dati personali. |
| **RBAC** | *Role-Based Access Control*, controllo degli accessi basato sui ruoli. |
| **MoSCoW** | Tecnica di prioritizzazione dei requisiti (Must, Should, Could, Won’t). |
| **Crowdsourcing** | Modello collaborativo in cui gli utenti contribuiscono ad aggiornare lo stato delle infrastrutture. |
| **Ospite** | Persona senza account che può consultare luoghi, pianificare percorsi, seguire segnalazioni tramite codice e registrarsi. Per inviare segnalazioni deve autenticarsi come Utente registrato. |
| **Utente registrato** | Utente autenticato che può inviare segnalazioni, salvare luoghi preferiti, ricevere notifiche e consultare lo storico delle proprie segnalazioni. |
| **Amministratore** | Operatore autorizzato a moderare le segnalazioni e gestire la sincronizzazione delle fonti dati ufficiali. |
| **Luogo** | Stazione, fermata o altro punto di interesse del trasporto pubblico per cui sono consultabili le caratteristiche di accessibilità. |
| **Segnalazione (Report)** | Comunicazione con cui un utente informa il sistema di una barriera architettonica o di un guasto, soggetta a moderazione. |
| **Dato valido** | Informazione verificata e aggiornata da non più di 30 giorni. |
| **Sconosciuto** | Stato esplicito assegnato a una caratteristica priva di dati o con dati scaduti, per evitare di dichiarare accessibile un luogo non verificato. |
| **Tratto non verificato** | Segmento di un itinerario per il quale non sono disponibili dati aggiornati sull’accessibilità. |
| **Codice di tracking** | Codice alfanumerico univoco e non enumerabile generato all’invio di una segnalazione per consultarne lo stato pubblico. |
| **Storico append-only** | Registro cronologico in cui ogni evento di moderazione viene aggiunto in coda, senza possibilità di modificare o cancellare gli eventi precedenti. |
| **GTFS** | *General Transit Feed Specification*, formato standard aperto per i dati su orari, linee e fermate del trasporto pubblico. |
| **PostGIS** | Estensione spaziale del database PostgreSQL per la gestione di dati geografici. |
| **OpenAPI** | Specifica standard per la descrizione e la documentazione delle API REST. |
| **p95** | 95° percentile: soglia temporale entro cui viene completato almeno il 95% delle richieste al sistema. |
| **Entity, Boundary, Control** | Classificazione degli oggetti del modello: dati persistenti di dominio (Entity), punti di interazione con attori e sistemi esterni (Boundary), logica applicativa che coordina i casi d’uso (Control). |

*Tabella 5.1: Glossario dei termini utilizzati nel progetto AEGIS*
