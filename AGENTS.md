# Regole predefinite

- La priorità è preservare il lavoro altrui. Prima di modificare un file, controlla il contenuto, le modifiche già presenti e, quando possibile, la loro provenienza. Se una modifica non è stata fatta dall'utente o non puoi stabilirne la provenienza, chiedi conferma prima di cambiarla. Procedi con umiltà e massimo rispetto per il lavoro esistente. Limita ogni intervento a quanto richiesto e non sostituire o sovrascrivere contenuti altrui.
- Non usare mai `--force` o operazioni equivalenti per Git o per sovrascrivere file e contenuti. Se un'operazione richiede la modalità forzata, fermati e preserva il lavoro esistente.
- Per aggiornare un documento esistente, modifica sempre lo stesso file e mantieni il suo ID o percorso. Non crearne una copia a ogni iterazione. Crea un nuovo documento solo quando viene richiesto esplicitamente.
- Prima di creare un Google Doc, controlla il registro dei link in `docs/GOOGLE_DOCS_SYNC.md`. Se il link esiste, aggiorna quel documento e conserva il suo ID. Se non esiste o non è più valido, crea il documento e registra subito il nuovo link nello stesso file.
- Non introdurre simboli decorativi, caratteri insoliti, artefatti di codifica o testo spazzatura. Usa testo pulito e formattazione coerente con il file. Mantieni i caratteri necessari al contenuto e alla lingua.

## Skill AEGIS

Le skill specifiche del progetto si trovano in `.agents/skills/`. Usa solo quelle pertinenti alla richiesta. Non caricare le skill di altri ambiti per abitudine.

Per ogni modifica a RAD, SDD, template o documentazione di progetto, applica `aegis-docs` e `aegis-antislop` insieme. Verifica evidenze, tracciabilità, linguaggio, diagrammi ed export prima di concludere.

## Pull request

Prima di creare o aggiornare una pull request, leggi `.github/pull_request_template.md` e segui le sue istruzioni, inclusa l’indicazione di lasciare vuoto il corpo quando richiesto.
