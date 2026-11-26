# AEGIS

Monorepo per la piattaforma di mobilità urbana accessibile.

## Stato del repository

Il repository contiene RAD, SDD, strumenti documentali e un prototipo applicativo Java con frontend web. Le viste Jakarta Faces in italiano coprono il tracking pubblico UC-04 e una ricerca per coordinate con risultati in elenco testuale per UC-02. Bootstrap è incluso localmente. Il deployment locale su Tomcat 11.0.29 è stato verificato il 26 novembre 2026: le viste e gli asset CSS rispondono `200`, la validazione delle coordinate funziona e una ricerca valida restituisce lo stato vuoto previsto. Il prodotto non è completo né pronto per l'esercizio: i repository sono in memoria e senza dati iniziali, non esiste una suite di test applicativa, la mappa non è implementata e il recupero positivo di un luogo o di una segnalazione non è stato verificato.

## Struttura

| Percorso | Contenuto |
|---|---|
| `docs/` | [RAD](docs/RAD.md), [SDD](docs/SDD.md) e [template](docs/template/). |
| `backend/src/` | Moduli Maven Java per configurazione, dominio, casi d'uso, adattatori e WAR. |
| `frontend/` | Sorgenti Facelets/CSS, Bootstrap bloccato nel lockfile e script di build degli asset. |
| `config/` | Toolchain documentale e configurazione applicativa esterna, inclusi i messaggi italiani. |
| `.agents/skills/` | Skill Codex specifiche del progetto. |
| `.github/` | Dependabot e collegamento issue alla segnalazione privata delle vulnerabilità. |

`make frontend-build` installa le dipendenze JavaScript bloccate e produce gli asset in `frontend/dist/`. `make package` costruisce tali asset e il WAR `backend/src/bootstrap/target/aegis.war`. `make tomcat` avvia il container locale, ma non installa né distribuisce automaticamente il WAR. Il runtime usa repository in memoria e non contiene dati di esempio.

La documentazione e i relativi template risiedono in `docs/`. Il DOCX sorgente è il riferimento di stile e struttura. I DOCX esportati non sono versionati. La build Maven non trova test applicativi e non sono presenti workflow CI. La generazione locale degli export è disponibile con `make docs`.

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
