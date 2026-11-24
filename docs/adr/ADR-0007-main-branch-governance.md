# ADR-0007: governance del branch main

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0007 |
| Titolo | Governance del branch main |
| Stato | Respinto |
| Data | 16 novembre 2026 |
| Responsabile | Proprietario del repository, da designare |
| Revisori | Maintainer e responsabile tecnico, da designare |
| Requisiti correlati | NFR-15, OPEN-09, ENG-02, ENG-07 ed ENG-09 |

## Contesto e problema

La verifica GitHub del 17 novembre 2026 ha rilevato `main` senza protezione e nessun ruleset. Su richiesta del proprietario, gli script di validazione documentale e i workflow GitHub Actions sono stati rimossi. La generazione DOCX è disponibile solo in locale.

Il repository non ha ancora una build applicativa. Un gate sempre attivo può verificare i documenti, i requisiti e le regole di tracciabilità già disponibili. Non equivale a compilazione o test applicativi. Il quality gate dovrà essere esteso quando ENG-03 introduce la build.

## Driver decisionali

| Driver | Criterio |
|---|---|
| Integrità di `main` | Le modifiche arrivano tramite un flusso di revisione configurato dal proprietario. |
| Check affidabile | Il check obbligatorio viene pubblicato anche quando una modifica non interessa i percorsi dell’export DOCX. |
| Verifica riproducibile | Non è previsto un check automatizzato nel repository. |
| Accesso amministrativo | La proposta non presume un numero di maintainer o una possibilità di auto approvazione. |
| Evoluzione del quality gate | Build, test e analisi applicative si aggiungono quando sono implementati e approvati in ENG-03 ed ENG-07. |
| Recupero | Il proprietario mantiene un percorso amministrativo documentato per correggere un ruleset che impedisca un rilascio necessario. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| Nessuna protezione | Consente push diretti e non introduce nuovi blocchi. | Errori o modifiche non verificate possono raggiungere `main`. | Non fornisce il gate richiesto da OPEN-09. È lo stato osservato. |
| Protezione di `main` senza check obbligatorio | Richiede il flusso di pull request scelto dal proprietario. | La revisione da sola non prova che i controlli automatizzati siano passati. | Soddisfa parte di OPEN-09 ma non rende obbligatoria la verifica documentale. |
| Ruleset con pull request e check sempre attivo | Associa la protezione a un controllo stabile che copre ogni pull request. | Richiede configurazione e approvazione del proprietario. Numero di approvazioni e gestione delle eccezioni dipendono dai maintainer effettivi. | Può soddisfare OPEN-09 con il check `Verifica documentazione`. Non sostituisce i controlli applicativi futuri di ENG-07. |

## Proposta respinta

La proposta di configurare un ruleset con pull request obbligatorie e check CI non è stata adottata. Questo ADR non configura le impostazioni remote di GitHub e non stabilisce una policy di revisione.

## Decisione

Il proprietario ha respinto la proposta il 17 novembre 2026 e ha richiesto la rimozione degli script di validazione e dei workflow CI. Il branch `main` resta senza protezione. Ogni futura automazione o regola di merge richiede una nuova decisione del proprietario.

## Conseguenze

- Il repository non esegue controlli automatici su push o pull request.
- Gli export DOCX devono essere generati e revisionati localmente.
- `main` resta senza ruleset o protezione.
- Ogni futura automazione richiede una nuova decisione del proprietario.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Rimozione degli script e workflow | Directory `scripts/`, test documentali e workflow CI assenti | Proprietario del repository | Completato nel commit `5d302ea` |
| Stato remoto del branch | `main` senza protezione o ruleset | Proprietario del repository | Verificato in [CONTRIBUTING](../CONTRIBUTING.md#stato-della-governance-github) il 17 novembre 2026 |

Riesaminare la decisione se il proprietario richiede nuove automazioni o modifiche alla governance del branch.
