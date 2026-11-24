# aegis

Monorepo per la piattaforma di mobilità urbana accessibile.

## Stato del repository

Il repository contiene la documentazione e gli strumenti per esportare RAD e SDD in DOCX. Il prodotto applicativo non è ancora implementato: `src/` non contiene moduli e non è presente una build applicativa. Requisiti e architettura descrivono il sistema proposto e restano da convalidare nei punti elencati in [RAD e SDD](docs/MILESTONES.md).

## Struttura

| Percorso | Contenuto |
|---|---|
| `docs/` | [RAD](docs/RAD.md), [SDD](docs/SDD.md), [modelli e riferimenti](docs/template.md), [roadmap](docs/MILESTONES.md) e [guida ai contributi](docs/CONTRIBUTING.md). |
| `config/` | Configurazione condivisa della toolchain documentale e dei dati del progetto. |
| `config/data/` | Directory per i dati del progetto. Attualmente vuota. |
| `src/` | Codice applicativo. |
| `.agents/skills/` | Skill Codex specifiche del progetto. |
| `.github/` | Dependabot e collegamento issue alla segnalazione privata delle vulnerabilità. |

La documentazione e i relativi template risiedono in `docs/`. Il DOCX sorgente è il riferimento di stile e struttura. I DOCX esportati non sono versionati. Non sono configurati script di test o validazione documentale e non sono presenti workflow CI. La generazione locale degli export è disponibile con `make docs`.

## Segnalazioni

Per bug e richieste, consultare le [issue aperte](https://github.com/kairosci/aegis/issues) e usare il [modello issue](docs/template/ISSUE.md) per descrivere contesto, riproduzione e risultato atteso. Per le vulnerabilità seguire la [politica di sicurezza](docs/SECURITY.md) e usare il report privato GitHub. Prima di condividere log o esempi, rimuovere credenziali, token, dati personali e codici di tracking.

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
