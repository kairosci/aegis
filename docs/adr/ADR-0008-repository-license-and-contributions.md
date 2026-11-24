# ADR-0008: licenza del repository e contributi

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0008 |
| Titolo | Licenza del repository e contributi |
| Stato | Proposto |
| Data | 16 novembre 2026 |
| Responsabile | Proprietario del repository, da designare |
| Revisori | Maintainer e consulente legale, se richiesto dal proprietario |
| Requisiti correlati | NFR-15, OPEN-10, ENG-02, ENG-07 ed ENG-09 |

## Contesto e problema

Il repository `kairosci/aegis` è pubblico, ma la verifica GitHub del 17 novembre 2026 ha restituito `licenseInfo: null`. Nel repository non risultano file `LICENSE`, `COPYING` o `NOTICE`. La pubblicazione del codice in un repository pubblico non equivale a una concessione generale dei diritti di riuso. I termini GitHub consentono agli utenti di visualizzare e fare fork del repository ospitato. Senza licenza si applicano le regole di copyright predefinite e non si concede in via generale il diritto di riprodurre, distribuire o creare opere derivate. GitHub suggerisce di aggiungere una licenza se il progetto intende essere open source ([documentazione GitHub](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/licensing-a-repository)).

La selezione deve esprimere l’intento del proprietario per uso, modifica, ridistribuzione, attribuzione, brevetti e reciprocità. Deve inoltre chiarire come sono trattati contributi esterni, documentazione, modelli, immagini, dataset e materiali di esempio. L’OSI mantiene un elenco di licenze approvate attraverso il proprio processo di revisione ([elenco OSI](https://opensource.org/licenses)).

Questo ADR confronta modelli comuni, non formula un parere legale e non assegna diritti sul materiale di terze parti. I PDF di esempio presenti solo come file non tracciati nella working directory non sono inclusi né licenziati da questa proposta.

## Driver decisionali

| Driver | Criterio |
|---|---|
| Intento del proprietario | La licenza scelta corrisponde alla politica desiderata su riuso e redistribuzione. |
| Chiarezza per chi contribuisce | Le regole inbound per codice e documentazione sono visibili prima di accettare contributi. |
| Compatibilità dei contenuti | I file distribuiti hanno titolarità o autorizzazioni compatibili con la licenza dichiarata. |
| Semplicità di manutenzione | Il testo legale è standard e identificabile tramite nome e identificatore SPDX. |
| Tracciabilità | La licenza è presente nel repository e il metadato GitHub corrisponde al file. |
| Revisione informata | Proprietà, contributi preesistenti e materiali di terze parti sono controllati prima della pubblicazione. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| Nessuna licenza | Non introduce una scelta di riuso non approvata dal proprietario. | Gli utenti non ricevono una concessione generale per riprodurre, distribuire o creare opere derivate. Il repository resta pubblico ma senza una licenza open source. | Mantiene l’attuale stato. Non soddisfa l’obiettivo di OPEN-10 se il progetto intende accettare riuso o contributi pubblici. |
| Licenza permissiva MIT | Testo breve. Richiede di mantenere avviso di copyright e testo della licenza nelle copie o porzioni sostanziali distribuite. La guida di GitHub la descrive come permissiva ([MIT](https://choosealicense.com/licenses/mit/)). | Non esprime una politica di reciprocità sulle opere derivate. Il proprietario deve verificare che tale libertà corrisponda all’intento del progetto. | Adatta solo se il proprietario approva il modello permissivo e la revisione dei contenuti. |
| Licenza permissiva Apache-2.0 | Include una concessione esplicita di diritti brevettuali da parte dei contributori e condizioni di ridistribuzione con avvisi e indicazione delle modifiche ([testo OSI](https://opensource.org/license/apache-2.0)). | Testo e obblighi di avviso sono più articolati rispetto a MIT. Il proprietario deve verificare titolarità, contributi e materiali distribuiti. | Adatta solo se il proprietario approva le condizioni e il trattamento dei brevetti previsti dal testo. |
| Licenza copyleft GPL-3.0 | La ridistribuzione di versioni modificate deve rispettare gli obblighi di disponibilità del codice sorgente corrispondente descritti dalla licenza ([testo OSI](https://opensource.org/license/gpl-3.0)). | Impone condizioni di ridistribuzione diverse da quelle permissive. Compatibilità con dipendenze e distribuzione deve essere valutata prima dell’adozione. | Adatta solo se il proprietario intende mantenere tali condizioni per le opere coperte. |

## Proposta per approvazione

Non accettare contributi esterni che richiedano diritti di riuso o ridistribuzione finché OPEN-10 non è risolto. Prima della scelta, il proprietario deve:

- dichiarare l’intento di distribuzione e il livello di reciprocità desiderato.
- confermare chi detiene i diritti sul codice, sulla documentazione e sugli altri file già versionati.
- identificare i materiali di terze parti, le relative licenze e le autorizzazioni necessarie.
- decidere se i contributi sono accettati sotto la licenza del progetto oppure richiedono un accordo separato come Developer Certificate of Origin (DCO) o Contributor License Agreement (CLA).
- selezionare il testo standard esatto e il relativo identificatore SPDX.
- inserire il file di licenza nella radice e verificare che GitHub riconosca la stessa licenza.
- definire eccezioni esplicite per file, dataset, immagini o documenti con condizioni differenti.

La comparazione non sceglie una licenza predefinita. MIT, Apache-2.0 e GPL-3.0 comportano condizioni diverse e la scelta richiede l’approvazione del proprietario. Il testo integrale non va modificato per adattarlo senza revisione competente.

## Decisione

Non approvata. Il proprietario deve scegliere la licenza, confermare la titolarità dei materiali, definire i termini inbound dei contributi e stabilire l’ambito coperto. La pubblicazione di un file `LICENSE` è il criterio necessario per aggiornare OPEN-10, ma non sostituisce l’audit dei contenuti di terze parti.

## Conseguenze attese se approvata

- Il repository dichiara in modo rilevabile le condizioni d’uso approvate.
- README e CONTRIBUTING rimandano alla stessa licenza e ai termini per i contributi.
- Materiali non coperti o soggetti a licenze diverse sono identificati in modo esplicito.
- I contributi esterni sono trattati secondo una regola inbound pubblicata prima dell’accettazione.
- GitHub mostra un metadato di licenza coerente con il testo versionato.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Approvazione dell’intento di licenza | Il proprietario approva identificatore SPDX, diritti concessi, reciprocità e ambito | Proprietario del repository | In attesa |
| Inventario dei diritti | Ogni file distribuito è di titolarità del progetto o ha una licenza o autorizzazione registrata | Proprietario e maintainer, da designare | Non eseguito |
| Termini per i contributi | CONTRIBUTING descrive chiaramente condizioni inbound e richiede gli eventuali atti approvati | Proprietario del repository | In attesa |
| File di licenza | Il testo standard approvato è presente nella radice e corrisponde all’identificatore dichiarato | Maintainer, da designare | Non eseguito |
| Rilevamento GitHub | GitHub restituisce la licenza attesa e le eccezioni di ambito sono documentate | Maintainer, da designare | Non eseguito |

Riesaminare la decisione prima di accettare contributi esterni, quando cambia l’ambito dei file distribuiti o quando si modifica la licenza.
