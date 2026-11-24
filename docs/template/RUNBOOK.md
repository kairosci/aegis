# Runbook operativo

Creare un runbook per servizio e ambiente. Completare i campi con procedure provate, collegamenti a fonti controllate e responsabili nominati. Non inserire credenziali, token o dati personali. Lasciare esplicitamente aperti gli obiettivi non approvati.

## Metadati

| Campo | Valore |
|---|---|
| Servizio e versione | Da compilare |
| Ambiente | Sviluppo / staging / produzione / altro da indicare |
| Responsabile operativo | Da designare |
| Escalation e copertura | Da concordare |
| Ultima revisione | Da compilare |
| Prossima revisione | Da concordare |

## Registro delle revisioni

Registrare le modifiche approvate senza sovrascrivere le prove operative precedenti.

| Versione | Data | Modifica e motivazione | Autore | Revisore e approvazione |
|---|---|---|---|---|
| Da compilare | Da compilare | Da descrivere | Da designare | Da registrare |

## Scopo e impatto

Descrivere le funzioni del servizio, gli utenti interessati, gli impatti di indisponibilità o dati non aggiornati e i servizi dipendenti. Collegare i requisiti e i criteri di servizio approvati.

## Accesso e prerequisiti

| Prerequisito | Metodo approvato | Responsabile o riferimento |
|---|---|---|
| Autorizzazione operativa | Da definire | Da designare |
| Configurazione e segreti | Riferimento a secret manager. Non copiarli qui | Da designare |
| Strumenti e dashboard | Da indicare | Da designare |
| Finestra di intervento e comunicazioni | Da concordare | Da designare |

## Segnali e diagnosi

| Sintomo o allarme | Evidenza da consultare | Criterio di escalation | Procedura collegata |
|---|---|---|---|
| Indisponibilità o latenza elevata | Metriche e log approvati | Soglia e responsabile da concordare | Da definire |
| Sincronizzazione fallita o dati scaduti | Esito, fonte e timestamp della sincronizzazione | Regola per fonte da approvare | Da definire |
| Pulizia cache pendente | Generazione attiva, lavoro registrato ed esito del worker | Tempo massimo e responsabile da concordare | Da definire |
| Notifica pendente o con esito ambiguo | Stato dell'outbox, tentativi e riscontro disponibile dal provider | Regola di riconciliazione e responsabile da approvare | Da definire |
| Coda di moderazione in crescita | Dimensione ed età della coda | Soglia e responsabile da concordare | Da definire |
| Errore di sicurezza o privacy | Audit e segnalazione controllata | Procedura di incidente da approvare | Da definire |

## Procedure operative

Documentare ogni procedura come sequenza numerata. Per ciascun passo indicare prerequisiti, comando o azione approvata, risultato atteso, criterio di arresto, conseguenze e metodo di verifica. Non includere comandi non provati sull’ambiente previsto.

### Rilascio e migrazione

Indicare controlli pre-rilascio, versione e provenienza degli artefatti, compatibilità della migrazione, approvazioni, verifica post-rilascio e criteri di rollback.

### Ripristino da backup

Indicare la fonte della copia, l’ambiente isolato, i passaggi di ripristino, la validazione d’integrità, i controlli applicativi e la registrazione dell’esito. Specificare l’obiettivo di punto di ripristino (Recovery Point Objective, RPO) e l’obiettivo di tempo di ripristino (Recovery Time Objective, RTO) solo dopo approvazione e misurazione.

Durante il ripristino verificare anche riferimenti tra database e object storage, intenti di upload pendenti, esiti di scansione, stato di cancellazione secondo OPEN-05, generazioni e cache, lavori pendenti di sincronizzazione e outbox notifiche. Definire come riconciliare ogni lavoro ripristinato prima di riattivare worker o canali esterni.

### Sincronizzazione delle fonti

Descrivere avvio, retry, idempotenza, validazione, invalidazione cache e verifica dell’esito. In caso di fonte non valida o scaduta, mantenere i dati come sconosciuti e seguire la procedura di escalation approvata.

### Recupero dei lavori asincroni

Documentare una procedura per ogni tipo di lavoro durevole, inclusi sincronizzazione, pulizia cache, riconciliazione degli upload e invio notifiche nell'outbox. Indicare l'archivio autorevole dello stato, i passaggi per distinguere lavoro pendente, fallito e con esito ambiguo, le verifiche prima di un retry o replay, i controlli di idempotenza, le possibili duplicazioni, l'escalation e la registrazione dell'esito. Per la pulizia cache verificare la generazione attiva e non dichiarare incoerenti i dati solo perché la rimozione di chiavi superate è ancora pendente. Per gli upload verificare intento, esito della scansione, visibilità privata e collegamento al report. Rimuovere o riprendere gli oggetti non collegati secondo le policy OPEN-05, OPEN-08 e OPEN-15. Per una notifica ambigua, verificare il riscontro disponibile dal provider e applicare la regola approvata prima di ritentare. Non dichiarare consegna esattamente una volta e non registrare come riuscito un lavoro senza evidenza. Retry, backoff, scadenze e responsabilità restano da approvare secondo OPEN-08 e [ADR-0005](../adr/ADR-0005-operability-and-recovery-contract.md).

### Coda di moderazione

Indicare come consultare dimensione, anzianità ed errori della coda, il responsabile della presa in carico, i criteri di escalation e il canale operativo approvato. Ogni azione su una segnalazione deve rispettare autorizzazioni e transizioni della matrice OPEN-12. Non modificare direttamente lo stato o gli eventi per aggirare un errore del flusso applicativo. Registrare identificativi tecnici minimizzati e riferimenti alla procedura senza copiare descrizioni, credenziali o codici di tracking nei log del runbook.

### Gestione degli incidenti

Descrivere rilevazione, classificazione, contenimento, escalation, comunicazioni, ripristino e revisione successiva. Indicare ruoli e canali approvati senza inserire dati personali o segreti.

## Verifiche e cronologia

| Data | Procedura provata | Ambiente e versione | Esito osservato | Problemi e follow-up | Esecutore e revisore |
|---|---|---|---|---|---|
| Da raccogliere | Da provare | Da indicare | Non eseguito | Da raccogliere | Da designare |

## Riferimenti e dipendenze

Elencare dashboard, configurazioni, inventario delle dipendenze, ADR, procedure collegate e contatti operativi approvati. Usare link a sistemi controllati invece di copiare segreti o dati riservati.
