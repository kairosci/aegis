# aegis

Monorepo per la piattaforma di mobilità urbana accessibile.

## Stato del repository

Il repository contiene la documentazione, gli strumenti per esportare RAD e SDD in DOCX e lo scheletro applicativo Java. La struttura separa dominio, casi d'uso e adattatori, ma non implementa le integrazioni esterne né le viste complete. I dati applicativi risiedono in `config/data/` e la toolchain in `mise.toml`. Requisiti e architettura restano da convalidare.

Il modulo `jpa` implementa le porte `PlaceRepository`, `ReportRepository` e `SubmissionReservationPort` sulle tabelle `place`, `accessibility_feature`, `report`, `report_event` e `report_submission`. Le classi `JpaPlaceRepository`, `JpaReportRepository` e `JpaSubmissionReservations` convertono le righe nei tipi di dominio tramite mapper espliciti. La selezione della sorgente dati a runtime e il wiring transazionale restano aperti.

## Struttura

| Percorso | Contenuto |
|---|---|
| `docs/` | [RAD](docs/RAD.md), [SDD](docs/SDD.md) e [template](docs/template/). |
| `config/` | Configurazione condivisa degli strumenti documentali. |
| `config/data/` | Unica radice versionata dei dati applicativi. |
| `config/data/application.properties` | Valori di policy e percorso del bundle messaggi letti dal caricamento tipizzato. |
| `config/data/it/aegis/adapter/web/messages.properties` | Testi localizzati della vista di tracking. |
| `mise.toml` | Versioni bloccate di JDK, Maven, Node.js, Pandoc e Tomcat. |
| `Makefile` | Comandi per toolchain, build Java ed export DOCX. |
| `pom.xml` | Reactor Maven dei moduli Java. |
| `src/config/` | Caricamento della configurazione esterna e costanti in `it.aegis.config.env`. |
| `src/domain/` | Entità, value object e regole di dominio. |
| `src/application/` | Porte e casi d'uso. |
| `src/adapters/` | Adattatori `inmemory`, `jpa`, `rest`, `web` e `worker`. |
| `src/bootstrap/` | Assemblaggio WAR dell'applicazione. |
| `.agents/skills/` | Skill specifiche del progetto. |
| `.github/` | Dependabot e collegamento issue alla segnalazione privata delle vulnerabilità. |

La documentazione e i relativi template risiedono in `docs/`. Il DOCX sorgente è il riferimento di stile e struttura. I DOCX esportati non sono versionati. Non sono configurati script di validazione documentale, test applicativi o workflow CI. La generazione locale degli export è disponibile con `make docs`.

## Segnalazioni

Per bug e richieste, consultare le [issue aperte](https://github.com/kairosci/aegis/issues). Per le vulnerabilità usare il report privato GitHub. Prima di condividere log o esempi, rimuovere credenziali, token, dati personali e codici di tracking.

## Toolchain

`mise.toml` è l'unica fonte delle versioni degli strumenti di sviluppo. Il comando seguente installa JDK, Maven, Node.js, Pandoc e Tomcat alle versioni bloccate:

```sh
make install-tools
```

Ogni target del Makefile invoca gli strumenti tramite `mise exec`. Il Makefile non ripete numeri di versione. Per usare gli strumenti fuori da Make, eseguire i comandi con `mise exec --`.

Per avviare il container servlet/JSP locale usato dalle pagine in sviluppo:

```sh
make tomcat
```

Tomcat è uno strumento locale per servlet e JSP. Non sostituisce WildFly 42, che resta il runtime Jakarta EE completo descritto nel SDD. L'eventuale WAR va distribuito manualmente nell'istanza avviata.

## Dati e codice

I dati applicativi stanno solo in `config/data/`. `ConfigLoader` richiede `AEGIS_DATA_DIR` e legge `application.properties` senza default silenziosi. Il nome della variabile è definito in `it.aegis.config.env.EnvironmentVariables` e non è ripetuto altrove nel codice. `MessageSource` carica il bundle dei messaggi dalla stessa directory. Nessun file dati o testo dell'interfaccia è presente in `src/`.

I valori provenienti dai dati esterni non sono ripetuti nel codice. Le regole di transizione, gli invarianti e i valori di protocollo, come i limiti delle coordinate geografiche, restano nel dominio come costanti esplicite o valori derivati. I byte massimi dell'allegato, la lunghezza dell'impronta, l'esito di verifica dei tratti e la dimensione del token Base62 sono calcolati dalla configurazione, dai parametri dell'algoritmo e dallo stato canonico. I messaggi diagnostici delle eccezioni e gli identificatori di chiave sono riferimenti di protocollo, non dati configurabili.

Ogni package, tipo, campo e metodo del sorgente Java è documentato con Javadoc. Il comando `make javadoc` genera la documentazione API e verifica che non manchino commenti.

## Prerequisiti documentali

Dopo `make install-tools`, predisporre le dipendenze Node bloccate dal lockfile e Chromium headless:

```sh
make install-doc-tools
```

Non è necessario installare manualmente Node.js o Pandoc.

## Esportazione DOCX

Generare localmente gli export RAD e SDD con:

```sh
make docs
```

I file vengono salvati in `build/docs/`. Il comando non esegue test o validazioni automatiche.

Mermaid CLI converte i diagrammi in immagini e Pandoc incorpora immagini e testo nei DOCX. Rivedere manualmente gli export generati prima di condividerli.

`make docs` verifica che Mermaid CLI corrisponda alla versione dichiarata nel manifest Node. Dependabot controlla settimanalmente gli aggiornamenti delle dipendenze Node.

L'esportazione usa `docs/template/template.docx` come riferimento di stile. Pandoc converte Markdown in DOCX. MarkItDown svolge la direzione opposta, da documenti sorgente a Markdown.

## Build Java

Compilare il reactor con la toolchain bloccata e la directory dati esterna:

```sh
AEGIS_DATA_DIR=config/data make compile
```

Costruire gli artefatti Java con:

```sh
AEGIS_DATA_DIR=config/data make package
```

Il reactor separa i moduli in `src/`, con gli adattatori in `src/adapters/` e l'assemblaggio in `src/bootstrap/`. Non sono presenti test applicativi e la build non ne esegue.
