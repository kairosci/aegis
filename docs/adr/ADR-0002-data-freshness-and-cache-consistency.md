# ADR-0002: validità dei dati e coerenza della cache

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0002 |
| Titolo | Validità dei dati e coerenza della cache |
| Stato | Proposto |
| Data | 16 novembre 2026 |
| Responsabile | Responsabile dati, da designare |
| Revisori | Responsabile tecnico e titolari delle fonti, da designare |
| Requisiti correlati | FR-02, FR-03, FR-10, FR-11, NFR-08, UC-01, UC-02, UC-06, OPEN-02, ENG-02 ed ENG-04 |

## Contesto e problema

Le caratteristiche di accessibilità dipendono dalla fonte e dal tipo di dato. RAD richiede provenienza, aggiornamento e validità e stabilisce che dati assenti o scaduti siano sconosciuti. SDD propone generazioni di dati isolate, pubblicazione atomica e chiavi cache legate al vettore delle generazioni attive.

OPEN-02 lascia da approvare frequenza, validità, aggiornamento urgente e obiettivi di freschezza per fonte e tipo. La documentazione specifica la struttura di consistenza, ma non deve inventare tali valori. Resta inoltre da rendere esplicito che una risposta memorizzata in cache non può estendere la validità temporale di una caratteristica.

## Driver decisionali

| Driver | Criterio |
|---|---|
| Sicurezza d’uso | Un dato scaduto o privo di una regola di validità approvata non deve essere presentato come prova di accessibilità. |
| Provenienza verificabile | La risposta deve mantenere fonte, timestamp e validità associati ai dati restituiti. |
| Coerenza concorrente | Una risposta deve usare dati e provenienza dello stesso insieme di generazioni attive. |
| Recuperabilità | Un’interruzione dopo la pubblicazione non deve perdere il lavoro pendente di pulizia cache. |
| Parametri approvati | Frequenze e periodi di validità restano definiti dai responsabili delle fonti secondo OPEN-02. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| Finestra globale di freschezza | Configurazione e verifica iniziali semplici. | Una soglia unica non rappresenta le diverse frequenze e regole delle fonti. Può dichiarare valido un dato già scaduto o rendere inutilizzabile un dato ancora valido. | Non soddisfa NFR-08 se le fonti e i tipi di dato richiedono regole diverse. |
| Validità per fonte e tipo con cache versionata | Mantiene distinte le regole di validità e consente risposte coerenti durante pubblicazioni concorrenti. | Richiede metadati e configurazione approvata per fonte e tipo. La pulizia fisica della cache richiede recupero e osservabilità. | Soddisfa il comportamento progettato per FR-02, FR-03, FR-10, FR-11 e NFR-08. Frequenze e soglie restano aperte in OPEN-02. |
| Nessuna cache dei risultati | Evita chiavi cache obsolete e semplifica la lettura. | Non garantisce da sola una vista coerente durante pubblicazioni concorrenti e aumenta le letture del repository. La freschezza richiede comunque una regola per fonte e tipo. | Soddisfa parte dei requisiti di validità, ma non risolve da sola la consistenza degli snapshot né NFR-14. |

## Proposta per approvazione

Adottare validità per fonte e tipo di dato e mantenere la cache come ottimizzazione che non modifica tale validità.

- Ogni caratteristica restituita conserva fonte, timestamp di acquisizione e validità. La validità deriva da metadati attendibili della fonte o da una regola per fonte e tipo approvata.
- Se una caratteristica è assente, scaduta o priva di una regola di validità approvata, il sistema la presenta come sconosciuta e non la usa per dichiarare compatibilità.
- Ogni lettura rivaluta la scadenza anche in caso di cache hit. La durata della cache non può estendere la validità del dato.
- Le chiavi cache includono il vettore delle generazioni attive, l’area e i parametri normalizzati. Dati e provenienza sono letti dallo stesso snapshot.
- La pubblicazione della generazione e la registrazione del lavoro pendente di pulizia avvengono nella stessa transazione. La pulizia è idempotente e recuperabile. Le chiavi di generazioni superate non sono selezionabili anche se la pulizia fisica è in ritardo o fallisce.
- Quando una fonte non fornisce una versione confrontabile, le pubblicazioni concorrenti per la stessa fonte sono serializzate secondo una regola da approvare prima dell’attivazione.

La proposta non assegna frequenze, periodi di validità, soglie di freschezza o definizioni di aggiornamento urgente. Questi valori restano in OPEN-02 e devono essere approvati con i titolari delle fonti.

## Decisione

Non approvata. Il responsabile dati e il responsabile tecnico devono rivedere la proposta con i titolari delle fonti. Le regole temporali e l’ordinamento delle pubblicazioni senza versione confrontabile richiedono approvazione prima dell’attivazione delle fonti.

## Conseguenze attese se approvata

- Il modello dei dati conserva i metadati necessari a determinare validità e provenienza.
- Una cache hit non evita il controllo della validità temporale.
- Le query leggono i dati secondo un vettore coerente delle generazioni attive.
- La pubblicazione rende immediatamente non selezionabili le chiavi delle generazioni precedenti, indipendentemente dal completamento della pulizia fisica.
- Le metriche operative distinguono ritardo di aggiornamento, dati scaduti, lavoro pendente di pulizia ed errori di sincronizzazione.
- La configurazione per fonte e tipo resta versionata e non contiene soglie scelte per supposizione.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Approvazione delle regole OPEN-02 | Frequenze, validità e aggiornamento urgente definiti per fonte e tipo con titolari delle fonti | Responsabile dati e titolari delle fonti, da designare | In attesa |
| Prova dei dati scaduti e senza regola approvata | Risposta e interfaccia mostrano stato sconosciuto e non dichiarano compatibilità | Responsabile tecnico, da designare | Non eseguito |
| Prova di cache hit dopo la scadenza | La cache non rende valido un dato scaduto | Responsabile tecnico, da designare | Non eseguito |
| Prova di pubblicazione concorrente | Dati e provenienza restituiti corrispondono allo stesso vettore delle generazioni attive | Responsabile tecnico, da designare | Non eseguito |
| Prova di interruzione dopo il commit | Il lavoro di pulizia cache resta registrato e viene recuperato in modo idempotente | Responsabile tecnico e operativo, da designare | Non eseguito |

Riesaminare la proposta quando sono approvate le fonti dell’area pilota, quando cambiano i loro contratti o quando l’implementazione modifica le garanzie di consistenza.
