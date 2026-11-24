# ADR-0003: contratto del provider di routing accessibile

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0003 |
| Titolo | Contratto del provider di routing accessibile |
| Stato | Proposto |
| Data | 16 novembre 2026 |
| Responsabile | Responsabile tecnico, da designare |
| Revisori | Referente mobilità e responsabile di prodotto, da designare |
| Requisiti correlati | FR-04, FR-05, NFR-04, NFR-08, NFR-11, NFR-14, UC-01, OPEN-01, OPEN-03, OPEN-05, OPEN-07, OPEN-08 ed ENG-02 |

## Contesto e problema

Il caso UC-01 inoltra origine, destinazione ed esigenze a `RoutingProvider`. Il risultato deve essere combinato con dati di accessibilità con provenienza e validità. OPEN-03 lascia da selezionare provider, rete e profili supportati. OPEN-01 deve definire area pilota e fonti autorizzate. Origine, destinazione e preferenze possono essere dati personali o rivelare informazioni sulla salute quando associati a una persona. OPEN-05 deve definire finalità, basi giuridiche, destinatari, accesso e ciclo di vita, compresi log e telemetria del provider.

Senza un contratto esplicito, il sistema rischia di interpretare la presenza di un percorso come prova di accessibilità. Un provider può non supportare un profilo, restituire risultati parziali, non rispondere o fornire dati che non corrispondono all’area pilota. Nessuno di questi casi deve produrre una dichiarazione positiva non verificata.

## Driver decisionali

| Driver | Criterio |
|---|---|
| Correttezza | Un percorso calcolato non dimostra da solo che i suoi tratti siano accessibili. |
| Esplicitazione dell’incertezza | Dati mancanti, scaduti, contraddittori o parziali restano sconosciuti o non verificati. |
| Sostituibilità | La logica applicativa dipende dal contratto `RoutingProvider`, non dalle API di un prodotto specifico. |
| Compatibilità verificabile | La selezione considera area pilota, profili, provenienza, freschezza e granularità dei risultati. |
| Degrado controllato | Errori e profili non supportati sono visibili e non vengono convertiti in compatibilità implicita. |
| Privacy e sostenibilità | La valutazione considera dati trasmessi, licenza, limiti d’uso e costi operativi prima della selezione. |
| Minimizzazione dei dati | Inviare al provider solo i campi e i vincoli necessari al calcolo richiesto. Escludere account ID, tracking ID e altri identificativi non necessari. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| Provider ospitato dietro `RoutingProvider` | Riduce la gestione dell’infrastruttura di routing e consente di sostituire l’integrazione. | Dipendenza da copertura, contratto, disponibilità, licenza, limiti e trattamento dei dati del fornitore. | Può supportare UC-01 se la compatibilità è dimostrata per area e profili selezionati. La scelta resta aperta in OPEN-03. |
| Motore di routing gestito dal progetto dietro `RoutingProvider` | Maggiore controllo su configurazione, dati e comportamento operativo. | Richiede gestione di dati, aggiornamenti, capacità, monitoraggio e reperibilità. | Può supportare UC-01 se copertura, qualità e profili sono verificati. Richiede una valutazione operativa in OPEN-07. |
| Implementazione di routing specifica di aegis | Consente controllo diretto del modello e del contratto applicativo. | Aumenta responsabilità di correttezza, copertura, manutenzione e validazione del routing. | Non dimostra da sola copertura o accessibilità. Non è giustificata senza requisiti e capacità approvati. |

## Proposta per approvazione

Mantenere `RoutingProvider` come porta sostituibile e definire il contratto applicativo indipendentemente dal provider concreto.

- L’adattatore riceve una richiesta validata con origine, destinazione e solo le esigenze necessarie al profilo scelto. Esclude identificativi dell’account, codici di tracking e altri dati non necessari. Parametri e profili effettivamente supportati restano da definire in OPEN-03.
- Ogni alternativa restituisce identificativo della fonte e, quando disponibile, gli attributi di percorso e la geometria dei tratti necessari a correlare i dati di accessibilità. La granularità minima da richiedere al provider va verificata durante la selezione.
- Ogni tratto conserva uno stato esplicito: compatibile secondo evidenza valida, incompatibile secondo evidenza valida, oppure sconosciuto o non verificato. Un risultato del provider senza evidenza compatibile non autorizza a dichiarare il tratto accessibile.
- Evidenze assenti, scadute o contraddittorie non sono usate per escludere un tratto né per dichiararlo compatibile. Il risultato espone fonte e freschezza disponibili secondo le regole di OPEN-02.
- Timeout, indisponibilità, risposta incompleta e profilo non supportato producono uno stato esplicito. Un’eventuale alternativa di fallback è marcata non verificata e non viene presentata come equivalente a un percorso compatibile.
- La valutazione dei candidati documenta per l’area pilota copertura, profili, dettaglio per tratto, provenienza, freschezza, comportamento in errore, campi trasmessi, identificativi esclusi, log, telemetria, destinatari, conservazione, cancellazione, licenza, limiti d’uso, disponibilità e costo operativo. Il responsabile privacy valuta il trattamento e i trasferimenti secondo OPEN-05. I criteri e i valori sono approvati dai responsabili, non presunti in questo ADR.

La proposta non seleziona un provider, una rete o profili di esigenze. La decisione dipende dall’area, dalle fonti e dai profili approvati in OPEN-01 e OPEN-03. Campi inviati, log, telemetria, conservazione e destinatari sono riesaminati secondo OPEN-05 e OPEN-08 prima della selezione.

## Decisione

Non approvata. Il responsabile tecnico e il referente mobilità devono valutare i candidati rispetto all’area pilota e ai profili approvati. Prima dell’adozione, i revisori devono approvare contratto, granularità dei risultati, comportamento di fallback e criteri di compatibilità.

## Conseguenze attese se approvata

- Gli adattatori isolano il sistema dalle API e dai modelli specifici del provider.
- `PlanRoute` rappresenta esplicitamente compatibilità, incompatibilità e incertezza per ogni tratto valutabile.
- Un errore del provider non genera una dichiarazione implicita di accessibilità.
- La scelta del provider richiede evidenze riferite all’area pilota e ai profili effettivamente supportati.
- Costi, vincoli di licenza, limiti d’uso e dati trasmessi diventano parte della decisione architetturale.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Matrice comparativa dei candidati | Copertura, profili, dettaglio per tratto, provenienza, campi trasmessi, identificativi esclusi, log, telemetria, destinatari, conservazione, licenza e vincoli operativi sono documentati per l’area pilota approvata e revisionati secondo OPEN-05 | Responsabile tecnico, referente mobilità e responsabile privacy, da designare | In attesa |
| Prova di minimizzazione della richiesta | La richiesta inviata contiene solo campi del contratto approvato e non include identificativi dell’account, codici di tracking o attributi non necessari | Responsabile tecnico, da designare | Non eseguito |
| Prova di dato mancante, scaduto o contraddittorio | Il tratto è sconosciuto o non verificato e non è dichiarato compatibile | Responsabile tecnico, da designare | Non eseguito |
| Prova di timeout e indisponibilità | L’errore è visibile e l’eventuale fallback è marcato non verificato | Responsabile tecnico, da designare | Non eseguito |
| Prova di risposta parziale e profilo non supportato | Il sistema non completa silenziosamente la risposta né attribuisce compatibilità senza evidenza | Responsabile tecnico, da designare | Non eseguito |
| Approvazione della decisione | Provider, rete, profili e criteri di compatibilità sono approvati e OPEN-03 può essere chiuso | Responsabile tecnico e referente mobilità, da designare | In attesa |

Riesaminare la decisione quando cambia l’area pilota, un contratto del provider, la rete di fonti o un profilo supportato.
