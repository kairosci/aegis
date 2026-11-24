# aegis

Monorepo per la piattaforma di mobilità urbana accessibile.

## Stato del repository

Il repository contiene la documentazione e gli strumenti per esportare RAD e SDD in DOCX, oltre allo scheletro applicativo Java. La struttura separa dominio, casi d'uso e adattatori, ma non implementa le integrazioni esterne né le viste complete. I valori di policy risiedono in `config/application.properties`. Requisiti e architettura restano da convalidare.

## Struttura

| Percorso | Contenuto |
|---|---|
| `docs/` | [RAD](docs/RAD.md), [SDD](docs/SDD.md) e [template](docs/template/). |
| `config/` | Configurazione condivisa della toolchain documentale e dei dati del progetto. |
| `config/application.properties` | Valori applicativi letti dal caricamento tipizzato. |
| `config/data/` | Directory per i dati del progetto. Attualmente vuota. |
| `pom.xml` | Reactor Maven dei moduli Java. |
| `src/` | Moduli applicativi Java organizzati per dominio, casi d'uso e adattatori. |
| `.agents/skills/` | Skill Codex specifiche del progetto. |
| `.github/` | Dependabot e collegamento issue alla segnalazione privata delle vulnerabilità. |

La documentazione e i relativi template risiedono in `docs/`. Il DOCX sorgente è il riferimento di stile e struttura. I DOCX esportati non sono versionati. Non sono configurati script di validazione documentale, test applicativi o workflow CI. La generazione locale degli export è disponibile con `make docs`. La build Java richiede JDK 21 e Maven.

## Segnalazioni

Per bug e richieste, consultare le [issue aperte](https://github.com/kairosci/aegis/issues). Per le vulnerabilità usare il report privato GitHub. Prima di condividere log o esempi, rimuovere credenziali, token, dati personali e codici di tracking.

## Prerequisiti documentali

Per generare i DOCX servono anche Node.js `24.22.3` e Pandoc `3.14`, riportati in `config/tool-versions.env`. Installare queste versioni e renderle disponibili nel `PATH` prima di installare gli strumenti Mermaid. `make install-doc-tools` installa le dipendenze Node bloccate dal lockfile e Chromium headless. Non installa Node.js né Pandoc.

## Esportazione DOCX

Con i prerequisiti installati, predisporre Mermaid CLI e Chromium headless:

```sh
make install-doc-tools
```

Generare localmente gli export RAD e SDD con:

```sh
make docs
```

I file vengono salvati in `build/docs/`. Il comando non esegue test o validazioni automatiche.

Mermaid CLI converte i diagrammi in immagini e Pandoc incorpora immagini e testo nei DOCX. Rivedere manualmente gli export generati prima di condividerli.

`make docs` verifica che Node.js e Pandoc corrispondano alle versioni configurate e Mermaid CLI alla versione dichiarata nel lockfile. Dependabot controlla settimanalmente gli aggiornamenti delle dipendenze Node.

L'esportazione usa `docs/template/template.docx` come riferimento di stile. Pandoc converte Markdown in DOCX. MarkItDown svolge la direzione opposta, da documenti sorgente a Markdown.

## Build Java

La compilazione richiede JDK 21 e Maven. I valori applicativi sono letti da `config/application.properties`. La variabile `AEGIS_CONFIG_DIR` indica la directory che contiene il file e non ha un valore predefinito.

```sh
AEGIS_CONFIG_DIR=config mvn -f pom.xml compile
```

Il reactor separa i moduli in `src/`. Non sono presenti test applicativi e la build non esegue controlli automatici sui documenti.
