# ADR-0011: contratto della moderazione delle segnalazioni

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0011 |
| Titolo | Contratto della moderazione delle segnalazioni |
| Stato | Proposto |
| Data | 16 novembre 2026 |
| Responsabile | Responsabile prodotto, da designare |
| Revisori | Referente moderazione e responsabile privacy, da designare |
| Requisiti correlati | FR-09, FR-12, NFR-04, NFR-09, UC-03, UC-04, UC-05, OPEN-04, OPEN-05, OPEN-08, OPEN-12 ed ENG-06 |

## Contesto e problema

RAD richiede che un amministratore con permesso di moderazione possa confermare, rifiutare, risolvere o unire una segnalazione duplicata. FR-09 richiede che una transizione ammessa aggiorni stato ed evento nella stessa transazione. UC-05 richiede una motivazione quando prevista dalla policy. SDD descrive eventi append-only, autorizzazione lato server e notifiche registrate atomicamente quando consentite.

Il repository non definisce ancora gli stati iniziali e finali, le transizioni ammesse, il significato di ciascuna azione, i ruoli, le motivazioni obbligatorie o gli effetti di un'unione. Queste scelte sono assegnate a OPEN-12. Le azioni nominate in RAD non bastano a dedurre gli stati: per esempio, confermare una segnalazione può indicarne la validità senza implicarne la risoluzione.

## Driver decisionali

| Driver | Criterio |
|---|---|
| Integrità del flusso | Ogni comando è ammesso solo per gli stati iniziali approvati e produce un risultato definito. |
| Autorizzazione | Il server verifica attore, ruolo e permesso per ogni comando secondo OPEN-04. |
| Tracciabilità | Stato corrente ed evento append-only sono aggiornati atomicamente. |
| Privacy | Motivazioni, identità e contenuti dell'evento rispettano OPEN-05. |
| Effetti esterni | Una notifica è pianificata solo se consentita e non annulla la transizione persistita. |
| Verificabilità | Casi di accettazione e test coprono ogni transizione ammessa e rifiutata. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| Campo di stato modificabile senza eventi strutturati | Modello iniziale semplice. | Non offre una cronologia affidabile e non dimostra attore, motivazione o transizioni atomiche. | Non soddisfa i criteri di FR-09, NFR-04 e OPEN-12. |
| Macchina a stati esplicita con eventi append-only | Rende verificabili stati, guardie, ruoli, motivazioni ed effetti. | Richiede una matrice approvata, migrazioni compatibili e test per ogni transizione. | Può soddisfare FR-09, UC-05 e OPEN-12 dopo l'approvazione delle policy. |
| Workflow configurabile per coda e categoria | Può adattare il processo a più gruppi e tipi di segnalazione. | Aumenta configurazione, combinazioni, audit e rischio di regole divergenti. Non è giustificato da requisiti approvati. | La necessità e i criteri restano da dimostrare. |

## Proposta per approvazione

Adottare una macchina a stati esplicita con comandi di moderazione e un registro append-only. La proposta non assegna nomi o valori agli stati e non autorizza alcuna transizione prima dell'approvazione della matrice OPEN-12.

La matrice da approvare deve contenere, per ciascun comando e stato iniziale:

| Comando nominato in RAD | Stato iniziale ammesso | Stato finale | Ruolo e permesso | Motivazione | Relazione con altri report | Notifica consentita |
|---|---|---|---|---|---|---|---|
| Confermare | Da decidere | Da decidere | Da decidere | Da decidere | Non applicabile o da decidere | Da decidere secondo OPEN-04, OPEN-05 e OPEN-08 |
| Rifiutare | Da decidere | Da decidere | Da decidere | Da decidere | Non applicabile o da decidere | Da decidere secondo OPEN-04, OPEN-05 e OPEN-08 |
| Risolvere | Da decidere | Da decidere | Da decidere | Da decidere | Non applicabile o da decidere | Da decidere secondo OPEN-04, OPEN-05 e OPEN-08 |
| Unire un duplicato | Da decidere | Da decidere | Da decidere | Da decidere | Identificare il report di riferimento e l'effetto sul duplicato | Da decidere secondo OPEN-04, OPEN-05 e OPEN-08 |

La decisione deve inoltre stabilire se siano ammessi riassegnazione, riapertura, annullamento o correzione, come gestire comandi concorrenti e ripetuti e quali campi della decisione sono visibili tramite UC-04. Un comando rifiutato non modifica stato, evento o outbox. Una modifica ammessa registra stato, evento e intenzione di notifica autorizzata nella stessa transazione. L'eventuale consegna segue la proposta ADR-0005 e non è garantita esattamente una volta.

Per l'unione, la matrice deve definire il report di riferimento, la relazione persistente tra i report, il trattamento dello stato del duplicato e la presentazione nel tracking. La segnalazione originale e la sua cronologia non sono cancellate dalla sola operazione di unione. Accesso, conservazione e cancellazione della relazione seguono OPEN-05.

La motivazione deve essere validata lato server secondo le regole approvate. Testo libero, lunghezza, visibilità e conservazione non sono definiti da questa proposta. Gli eventi non devono duplicare contenuti personali non necessari. L'attore e i dati di attribuzione seguono OPEN-04 e OPEN-05.

## Decisione

Non approvata. Il responsabile prodotto e il referente moderazione devono completare e approvare la matrice con revisione privacy. I ruoli devono corrispondere alla policy OPEN-04. La decisione deve specificare gli effetti sulle notifiche secondo OPEN-08 e il trattamento dei dati secondo OPEN-05. OPEN-12 resta aperta fino alla registrazione dell'approvazione e delle evidenze.

## Conseguenze attese se approvata

- RAD specifica gli esiti osservabili delle azioni di UC-05 e aggiorna i criteri FR-09 e FR-12.
- SDD definisce vincoli, persistenza e concorrenza senza lasciare transizioni implicite.
- Test unitari e di integrazione verificano tutte le transizioni approvate, quelle rifiutate, autorizzazione, motivazioni, duplicati, atomicità e outbox.
- Il tracking espone soltanto i campi approvati e non rivela dettagli riservati.
- Le migrazioni conservano gli eventi storici e definiscono il trattamento dei record esistenti prima dell'attivazione del flusso.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Matrice OPEN-12 approvata | Stati, transizioni, attori, motivazioni, unioni, visibilità ed effetti sono definiti e coerenti con RAD e SDD | Responsabile prodotto, referente moderazione e responsabile privacy, da designare | In attesa |
| Prove dei comandi | Ogni transizione ammessa produce un solo aggiornamento atomico. Ogni transizione rifiutata lascia invariati stato, eventi e outbox | Responsabile tecnico, da designare | Non eseguito |
| Prove di autorizzazione e concorrenza | Ruoli non autorizzati e comandi concorrenti o ripetuti rispettano la matrice approvata | Responsabile tecnico e sicurezza, da designare | Non eseguito |
| Prova dell'unione | Report di riferimento, stato, tracking e cronologia corrispondono alla regola approvata senza cancellare gli eventi originari | Responsabile tecnico e responsabile privacy, da designare | Non eseguito |

Riesaminare la proposta se cambiano ruoli, categorie di segnalazione, accesso al tracking, policy di conservazione o processo di moderazione.
