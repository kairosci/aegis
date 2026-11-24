# System Design Document

| Dato | Valore |
|---|---|
| Progetto | Da compilare |
| Corso o committente | Da compilare |
| Docenti o referenti | Da compilare |
| Autori | Da compilare |
| Anno o versione | Da compilare |

## SOMMARIO

Inserire l’indice mantenendo la numerazione e la gerarchia di questo modello.

## INTRODUZIONE

### 1.1 SCOPO DEL SISTEMA

Descrivere le decisioni architetturali e progettuali e il loro rapporto con gli obiettivi e i requisiti del sistema.

### 1.2 OBIETTIVI DI PROGETTAZIONE

| Obiettivo | Criterio o decisione architetturale |
|---|---|
| Separazione delle responsabilità | Da definire |

### 1.3 DEFINIZIONI, ACRONIMI E ABBREVIAZIONI

| Termine | Definizione |
|---|---|
| Da compilare | Da definire |

### 1.4 RIFERIMENTI

Elencare solo fonti consultate e decisioni già approvate. Separare riferimenti esterni, requisiti e documenti di progetto.

| Riferimento | Versione o stato | Uso |
|---|---|---|
| Da compilare | Da verificare | Da descrivere |

### 1.5 PANORAMICA

Riassumere l’organizzazione del documento e le decisioni descritte nelle sezioni successive.

## SISTEMA CORRENTE

Descrivere architettura, processi e limiti esistenti. Se il sistema è greenfield, dichiararlo e indicare quali assunzioni devono essere convalidate.

## SISTEMA PROPOSTO

### 3.1 PANORAMICA

Descrivere architettura, componenti, tecnologie approvate, flussi principali e distribuzione. Distinguere ciò che è proposto da ciò che è implementato e verificato, collegando lo stato alle evidenze disponibili. Marcare come da decidere provider e versioni non ancora selezionati.

### 3.2 DECOMPOSIZIONE DEL SISTEMA

Descrivere moduli, responsabilità, dipendenze e confini. Separare dominio, casi d’uso e adattatori quando applicabile.

| Modulo | Responsabilità | Dipendenze e interfacce |
|---|---|---|
| Da definire | Da descrivere | Da specificare |

#### 3.2.1 DIAGRAMMA DEI MODULI

Inserire il diagramma e descriverne componenti, dipendenze e direzione dei flussi.

Descrizione: da compilare.

### 3.3 HARDWARE/SOFTWARE MAPPING

Associare componenti software ai nodi e servizi di esecuzione. Distinguere sviluppo, staging e produzione e documentare le configurazioni senza esporre segreti.

| Nodo o servizio | Componenti | Responsabilità e connessioni |
|---|---|---|
| Da definire | Da elencare | Da descrivere |

### 3.4 GESTIONE DEI DATI PERSISTENTI

Descrivere modello, proprietari, consistenza, indici, migrazioni, conservazione, backup e sincronizzazione.

#### 3.4.1 STRUTTURA DEI DATI PERSISTENTI

Descrivere entità o collezioni, attributi, vincoli, relazioni, indici e migrazioni del database selezionato.

| Entità o raccolta | Dati principali | Vincoli e relazioni |
|---|---|---|
| Da definire | Da descrivere | Da documentare |

Inserire una matrice per categorie di dati, uso, accesso, conservazione e cancellazione. Includere identità, contenuti utente, allegati, tracking, audit, log e copie. Distinguere dati identificativi e contenuti pubblici. Esplicitare i conflitti tra cronologia append-only e cancellazione senza fissare tempi o policy non approvati.

#### 3.4.2 ARCHIVIAZIONE DEI FILE

Descrivere archiviazione, accesso, scansione, pubblicazione, scadenza e cancellazione dei file. Specificare il comportamento in caso di errore.

#### 3.4.3 MECCANISMI DI SINCRONIZZAZIONE

Descrivere fonti, frequenze, versioni, controlli di qualità, idempotenza, retry, registrazione degli esiti e invalidazione delle cache. Se un effetto esterno avviene dopo il commit, registrarne il lavoro durevole nella stessa transazione. Le chiavi cache devono rendere non selezionabili i dati di una generazione superata anche se la pulizia fisica fallisce. Documentare recupero, retry e prova dell’interruzione tra commit ed effetto.

| Fonte o processo | Frequenza o trigger | Esito e controlli | Responsabile |
|---|---|---|---|
| Da definire | Da concordare | Da documentare | Da indicare |

### 3.5 CONTROLLO ACCESSI E SICUREZZA

Descrivere minacce rilevanti, identità, ruoli, autorizzazioni, protezione dei dati, audit e limiti di abuso. Le regole devono essere applicate lato server.

#### 3.5.1 AUTENTICAZIONE UTENTE

Descrivere metodi supportati, ciclo della sessione, recupero dell’account e dipendenze esterne. Non includere credenziali o dati personali reali.

#### 3.5.2 CONTROLLO ACCESSI AI DATI

Descrivere autorizzazioni per ruolo e risorsa, proprietà dei dati, validazione e protezione da accessi non autorizzati.

#### 3.5.3 GESTIONE DELLE IMMAGINI

Descrivere limiti, controlli del tipo e del contenuto, scansione, quarantena, autorizzazione, pubblicazione e cancellazione.

#### 3.5.4 PROTEZIONE DA ABUSI

Descrivere validazione, rate limiting, idempotenza, rilevamento di duplicati, moderazione e audit. Quantità e soglie restano da concordare finché non sono approvate.

**Analisi delle minacce.**

Analizzare gli scenari di minaccia rilevanti indicando bene esposto, impatto, controllo progettato, metodo di verifica e stato. Distinguere i controlli previsti da quelli implementati e verificati. Non dichiarare mitigato un rischio senza evidenza.

| Scenario di minaccia | Bene e impatto | Controllo progettato | Verifica richiesta |
|---|---|---|---|
| Da descrivere | Da valutare | Da approvare | Da pianificare |

**Sicurezza d'uso e pericoli operativi.**

Distinguere i pericoli derivanti dalle informazioni o dal comportamento del sistema dalle minacce informatiche. Per ciascun pericolo indicare possibile danno, controllo progettato ed evidenza richiesta. Non dichiarare controlli validati senza prove e registrare il rischio residuo prima del rilascio.

| Pericolo d'uso | Possibile danno | Controllo progettato | Evidenza richiesta |
|---|---|---|---|
| Da descrivere | Da valutare | Da approvare | Da pianificare |

### 3.6 DECISIONI SUL FLUSSO DI CONTROLLO GLOBALE

Descrivere come richieste, eventi e dipendenze attraversano i componenti, inclusi errori, transazioni e operazioni asincrone.

#### 3.6.1 GESTIONE DELLA SESSIONE UTENTE

Descrivere creazione, verifica, scadenza e chiusura della sessione e il comportamento per consultazione pubblica o funzioni protette.

#### 3.6.2 NAVIGAZIONE TRA SCHERMATE

Descrivere le viste, i percorsi di navigazione, i parametri e i metodi alternativi quando una funzione del dispositivo non è disponibile.

#### 3.6.3 GESTIONE ASINCRONA ED EVENTI

Descrivere code, worker, eventi, retry, idempotenza, ordinamento e osservabilità dei processi asincroni.

#### 3.6.4 GESTIONE DEGLI ERRORI

Descrivere classificazione degli errori, transazioni, risposta HTTP, messaggi accessibili, log e dati esclusi dai log.

### 3.7 CONDIZIONI LIMITE

Descrivere comportamento, risposta e stato persistente in ciascuna condizione limite rilevante. Non lasciare implicito il trattamento dei dati incerti.

#### 3.7.1 UTENTE NON AUTENTICATO

Descrivere le funzioni pubbliche, le operazioni protette e la risposta per una richiesta non autorizzata.

#### 3.7.2 ELEMENTO NON TROVATO O IDENTIFICATIVO NON VALIDO

Descrivere risposta, informazioni non divulgate e distinzione tra risorsa assente e dati incompleti.

#### 3.7.3 CONTRIBUTI DUPLICATI

Descrivere rilevamento, deduplicazione, unione o rifiuto e conservazione della cronologia.

#### 3.7.4 OPERAZIONI RIPETUTE O DUPLICAZIONE DEI DATI IMPORTATI

Descrivere idempotenza e trattamento di retry o importazioni ripetute.

#### 3.7.5 CARICAMENTO LENTO O ASSENZA DI RETE

Descrivere feedback, timeout, eventuale fallback e dati che restano non verificati.

#### 3.7.6 INPUT INCOMPLETI O INVALIDI

Descrivere validazione, errori restituiti e garanzia di assenza di effetti parziali. Includere file non validi, se previsti.

#### 3.7.7 PERMESSI DI GEOLOCALIZZAZIONE NEGATI

Descrivere funzionalità alternative, inserimento manuale e trattamento della posizione.

## SERVIZI DEL SOTTOSISTEMA

Descrivere i principali servizi applicativi, i loro confini e i flussi dati. Adattare le responsabilità al dominio senza introdurre funzioni non richieste.

### 4.1 AUTENTICAZIONE UTENTE

Descrivere accesso, registrazione se prevista, gestione sessione, uscita e integrazione con il servizio di identità selezionato.

### 4.2 GESTIONE DEGLI ELEMENTI

Descrivere creazione o importazione, ricerca, filtraggio, consultazione e dettaglio delle risorse principali del dominio.

### 4.3 VALUTAZIONI O INFORMAZIONI DI DOMINIO

Descrivere contributi o informazioni collegate alle risorse, validazione, provenienza e presentazione. Se la funzione non è prevista, motivare l’adattamento al dominio.

### 4.4 ELEMENTI SALVATI O PREFERENZE

Descrivere preferenze o elementi salvati, se previsti. Se non applicabile, usare la sezione per la funzione di dominio corrispondente e dichiarare l’adattamento.

### 4.5 SEGNALAZIONI

Descrivere invio, persistenza, tracciamento, moderazione e cronologia delle decisioni.

### 4.6 GEOLOCALIZZAZIONE E RICERCA

Descrivere ricerca per coordinate e testo, geocodifica, distanze e comportamento quando i permessi o i servizi esterni non sono disponibili.

## GLOSSARIO

Definire i termini di dominio e architettura che determinano comportamento, contratti dei dati o gestione degli errori. Usare una voce per termine e mantenere le definizioni coerenti con requisiti, diagrammi e flussi.

| Termine | Definizione |
|---|---|
| Da compilare | Significato nel contesto del sistema |
