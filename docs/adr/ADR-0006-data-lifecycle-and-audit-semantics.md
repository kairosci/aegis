# ADR-0006: ciclo di vita dei dati e semantica dell’audit

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0006 |
| Titolo | Ciclo di vita dei dati e semantica dell’audit |
| Stato | Proposto |
| Data | 16 novembre 2026 |
| Responsabile | Responsabile privacy, da designare |
| Revisori | Responsabile tecnico, responsabile dati e responsabile operativo, da designare |
| Requisiti correlati | FR-07, FR-09, NFR-04, NFR-09, NFR-10, UC-03, UC-04, UC-05, OPEN-04, OPEN-05, OPEN-08, OPEN-12, ENG-02, ENG-06 ed ENG-08 |

## Contesto e problema

RAD richiede minimizzazione, separazione delle identità, tracking limitato e storico delle decisioni di moderazione. SDD descrive eventi append-only e rileva che la cancellazione può coinvolgere database, object storage, indici, cache, log e backup. L’inventario comprende anche richieste di percorso, coordinate e preferenze di accessibilità, che possono essere trasferite al provider selezionato. Se associate a una persona, alcune preferenze possono rivelare informazioni sulla disabilità o sulla salute. Il considerando 35 del GDPR include la disabilità tra gli esempi di informazioni relative alla salute e l’articolo 9 disciplina il trattamento di categorie particolari. L’articolo 35 richiede una valutazione d’impatto quando il trattamento è suscettibile di presentare un rischio elevato, inclusi i casi previsti dal paragrafo 3. La qualificazione dei campi e l’applicabilità delle condizioni devono essere determinate dal responsabile privacy nel contesto concreto. OPEN-05 lascia da approvare finalità, basi giuridiche, accessi, correzione, conservazione, cancellazione e semantica dell’audit.

Riferimenti normativi per la revisione, consultati il 17 novembre 2026: [Regolamento (UE) 2016/679, considerando 35 e articoli 9 e 35](https://eur-lex.europa.eu/legal-content/IT/TXT/?uri=CELEX%3A32016R0679).

Un evento di moderazione utile alla ricostruzione delle transizioni non deve diventare un contenitore permanente di descrizioni, identificativi o altri dettagli personali. Allo stesso tempo, la cancellazione di una segnalazione non può essere descritta come completa se restano copie accessibili, allegati o log. Il contratto applicativo deve distinguere i fatti minimi della transizione dai contenuti e riferimenti soggetti a regole di ciclo di vita.

Questa proposta definisce opzioni tecniche da sottoporre ai responsabili. Non formula conclusioni giuridiche né stabilisce basi, obblighi, eccezioni o periodi di conservazione.

## Driver decisionali

| Driver | Criterio |
|---|---|
| Minimizzazione | Gli eventi conservano solo i dati necessari a descrivere transizione, esito e attribuzione approvata. |
| Correzione tracciabile | Una correzione non cancella silenziosamente la storia operativa e non duplica dati personali senza necessità. |
| Cancellazione verificabile | La procedura considera ogni copia controllata dal sistema, non soltanto la riga principale nel database. |
| Audit non identificativo per costruzione | Il contenuto del journal non include testo libero, credenziali o dati di contatto. |
| Coerenza con OPEN-05 | Accesso, conservazione, revoca e cancellazione derivano da una matrice approvata per categoria. |
| Recuperabilità | Backup e ripristino non devono rendere nuovamente disponibili dati la cui cancellazione è stata completata secondo la policy approvata. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| Eventi con dati completi immutabili | Ricostruzione semplice e attribuzione diretta. | Mantiene nei log di dominio contenuti e riferimenti personali anche dopo la rimozione dai record principali. | Supporta la cronologia, ma non dimostra il criterio di cancellazione richiesto da NFR-04 e OPEN-05. |
| Cancellazione fisica di report ed eventi collegati | Rimuove direttamente i record selezionati. | Distrugge la sequenza utile all’audit e può lasciare allegati, cache, indici, log e backup non coordinati. | Può soddisfare parte del ciclo di vita solo con una policy approvata e una procedura completa. Non preserva da sola la semantica append-only. |
| Eventi minimizzati e riferimenti personali separati | Conserva la sequenza delle transizioni riducendo il contenuto identificativo del journal e consente di applicare regole distinte ai riferimenti. | Richiede modello dati, relazioni e processi di cancellazione coordinati. La pseudonimizzazione non equivale ad anonimato e i dati presenti nei contenuti richiedono comunque una gestione approvata. | Può conciliare audit e ciclo di vita se la matrice OPEN-05 approva campi, accessi, eccezioni e procedura end-to-end. |

## Proposta per approvazione

Separare il journal delle transizioni dai contenuti della segnalazione e dai riferimenti che collegano una persona ai propri dati. Adottare minimizzazione dei campi e una procedura di ciclo di vita con esiti verificabili.

- Ogni evento conserva solo identificativo del caso, tipo di transizione, esito, timestamp e riferimento all’attore nella forma approvata. Non include descrizione libera, credenziali, codice di tracking o dati di contatto.
- La correzione dei dati del report aggiorna il contenuto corrente secondo la policy approvata e registra una transizione di correzione con il minimo di informazioni necessario. Il valore precedente non viene copiato nel journal salvo approvazione esplicita di finalità, accesso e conservazione.
- I dati di identità e le associazioni tra identità e report sono conservati separatamente dai contenuti pubblici e dagli eventi. Rimuovere un’associazione non deve essere descritto come anonimizzazione senza una valutazione specifica del rischio di reidentificazione.
- La richiesta di cancellazione è una procedura tracciata e autorizzata che usa la matrice OPEN-05 per determinare dati coinvolti, eccezioni approvate, responsabili e criteri di completamento. Il sistema non dichiara conclusa la procedura finché le componenti previste non hanno registrato l’esito.
- La matrice di ciclo di vita elenca per categoria finalità, campi, titolare, accessi, destinatari, basi giuridiche, conservazione, correzione, cancellazione, copie e prova richiesta. Comprende almeno identità, richieste di percorso, coordinate, preferenze di accessibilità, report, eventi, credenziale di tracking, immagini, cache, indici, log, dati trasferiti ai provider e backup.
- Il responsabile privacy valuta nel contesto se richieste, coordinate e preferenze associate a una persona rivelino dati relativi alla salute. Documenta le condizioni applicabili ai sensi dell’articolo 9 e se è necessaria una valutazione d’impatto secondo l’articolo 35. La documentazione non presume che ogni preferenza sia una categoria particolare né sceglie una base giuridica.
- La cancellazione coordinata considera record relazionali, oggetti, indici derivati, cache e log. Le copie di backup seguono una regola approvata di conservazione e scadenza, con un processo per evitare che un ripristino ripubblichi dati cancellati secondo la policy.
- Audit di moderazione e audit della procedura di cancellazione hanno contenuti minimizzati e accesso ristretto. L’identità degli operatori è conservata o rimossa secondo le decisioni OPEN-04 e OPEN-05.
- Le prove usano dati sintetici e verificano l’assenza dei valori trattati nelle componenti elencate, l’accesso residuo e l’esito dopo un ripristino di prova. I risultati non dimostrano la conformità normativa, che richiede revisione dei responsabili competenti.

Campi obbligatori, tempi, eccezioni, basi giuridiche, diritto di accesso, classificazione dei dati di percorso, condizioni applicabili ai sensi dell’articolo 9, necessità di una valutazione d’impatto ai sensi dell’articolo 35, modalità di correzione e regole per i backup restano da decidere in OPEN-05. La proposta non impone la cancellazione di una transizione d’audit né autorizza la conservazione indefinita dei dati.

## Decisione

Non approvata. Il responsabile privacy deve approvare la matrice e la semantica delle eccezioni con i responsabili di prodotto, dati e operazioni. La decisione deve identificare le categorie e i campi che possono restare nell’audit, le modalità di correzione, il ciclo di vita dei backup e le prove necessarie prima di attivare invio, tracking e moderazione.

## Conseguenze attese se approvata

- Eventi e dati del report hanno schemi e permessi distinti.
- La cronologia descrive transizioni senza duplicare per impostazione predefinita il testo del report o le credenziali.
- La rimozione dei riferimenti personali e quella dei contenuti sono procedure esplicite, verificabili e coordinate con le copie controllate dal sistema.
- Ripristino e cache devono rispettare lo stato della procedura di cancellazione secondo la policy approvata.
- Le dichiarazioni di completamento della cancellazione si basano su esiti registrati per ciascun archivio previsto.
- La conformità, i tempi e le eccezioni restano da approvare e non sono dedotti da questa proposta tecnica.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Matrice OPEN-05 approvata | Ogni categoria ha finalità, base giuridica, destinatari, accessi, responsabile, regole di conservazione, correzione e cancellazione approvate | Responsabile privacy, da designare | In attesa |
| Classificazione delle richieste di percorso | Campi, coordinate, preferenze, trasferimenti al provider e telemetria sono classificati nel contesto d’uso. Applicabilità degli articoli 9 e 35 del GDPR motivata senza inferire una base giuridica | Responsabile privacy e consulente competente, da designare | In attesa |
| Schema e campioni del journal | Gli eventi rispettano i campi minimizzati approvati e non includono testo libero o credenziali | Responsabile tecnico e privacy, da designare | Non eseguito |
| Prova di correzione e cancellazione | L’esito è verificabile per database, storage oggetti, indici, cache e log previsti dalla matrice | Responsabile tecnico e responsabile dati, da designare | Non eseguito |
| Prova su backup e ripristino | Dopo il ripristino di prova, dati e associazioni rispettano lo stato atteso secondo la policy approvata | Responsabile operativo e privacy, da designare | Non eseguito |
| Revisione del rischio di reidentificazione | L’uso di riferimenti separati o pseudonimi è valutato e non viene chiamato anonimizzazione senza evidenza | Responsabile privacy, da designare | In attesa |
| Revisione della procedura | Autorizzazioni, errori parziali, retry, registrazione degli esiti e criterio di completamento sono documentati e provati | Responsabile operativo e tecnico, da designare | Non eseguito |

Riesaminare la proposta quando sono approvate OPEN-04 e OPEN-05, quando cambiano le categorie di dati, il modello di audit o i processi di backup e ripristino.
