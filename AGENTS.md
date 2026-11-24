# Regole predefinite

- La priorità è preservare il lavoro altrui. Prima di modificare un file, controlla il contenuto, le modifiche già presenti e, quando possibile, la loro provenienza. Se una modifica non è stata fatta dall'utente o non puoi stabilirne la provenienza, chiedi conferma prima di cambiarla. Procedi con umiltà e massimo rispetto per il lavoro esistente. Limita ogni intervento a quanto richiesto e non sostituire o sovrascrivere contenuti altrui.
- Non usare mai `--force` o operazioni equivalenti per Git o per sovrascrivere file e contenuti. Se un'operazione richiede la modalità forzata, fermati e preserva il lavoro esistente.
- Per aggiornare un documento esistente, modifica sempre lo stesso file e mantieni il suo ID o percorso. Non crearne una copia a ogni iterazione. Crea un nuovo documento solo quando viene richiesto esplicitamente.
- Non introdurre simboli decorativi, caratteri insoliti, artefatti di codifica o testo spazzatura. Usa testo pulito e formattazione coerente con il file. Mantieni i caratteri necessari al contenuto e alla lingua.

## Skill aegis

Le skill specifiche del progetto si trovano in `.agents/skills/`. Usa solo quelle pertinenti alla richiesta. Non caricare le skill di altri ambiti per abitudine.

Per ogni modifica a RAD, SDD, template o documentazione di progetto, applica `aegis-docs` e `aegis-antislop` insieme. Verifica evidenze, tracciabilità, linguaggio, diagrammi ed export prima di concludere.

## Contribuire ad aegis

### Stato e ambito

Il repository contiene la documentazione RAD/SDD e gli strumenti per esportarla in DOCX. L'applicazione non è ancora implementata e non dispone di build o test applicativi. Gli script di validazione, i test documentali e i workflow CI sono stati rimossi. Il repository è pubblico ma non dichiara una licenza. Finché OPEN-10 non è risolta, non inviare contributi esterni che richiedono diritti di riuso o ridistribuzione.

### Flusso di lavoro

1. Aprire o aggiornare un'attività descrivendo problema, attore, risultato atteso, criteri di accettazione e requisiti collegati.
2. I maintainer lavorano su un branch dedicato e aprono una pull request verso `main`. Chi segnala un problema può usare le issue abilitate. Non riscrivere la cronologia condivisa e non inserire segreti o dati personali nel repository o nelle issue.
3. Aggiornare RAD per modifiche di requisito o comportamento. Aggiornare SDD per modifiche architetturali o operative. Registrare le decisioni direttamente nel documento pertinente, indicando se sono proposte, approvate o ancora aperte e collegandole ai criteri di verifica.
4. Collegare ogni requisito funzionale interessato a casi d'uso e moduli. Specificare le verifiche previste per accessibilità, privacy e sicurezza quando la modifica le influenza.
5. Riportare nella pull request la revisione svolta e i risultati effettivamente osservati. Non dichiarare prove con utenti, verifiche di ripristino o analisi non eseguite.

Per modifiche documentali applicare la skill [aegis-antislop](.agents/skills/aegis-antislop/SKILL.md) insieme alla skill [aegis-docs](.agents/skills/aegis-docs/SKILL.md). Controllare che ogni affermazione sia sostenuta da una fonte o marcata come proposta o decisione aperta, che i termini restino coerenti e che il diagramma globale riporti tutti i casi d'uso e le relative associazioni.

### Esportazione DOCX

Le versioni richieste per l'esportazione sono descritte nei [prerequisiti documentali del README](README.md#prerequisiti-documentali).

Per generare localmente gli export installare gli strumenti Mermaid e Chromium headless:

```sh
make install-doc-tools
```

Generare gli export con:

```sh
make docs
```

Gli export vengono salvati in `build/docs/` e non vanno aggiunti al repository. `make docs` genera i file senza eseguire controlli automatici. Esaminare manualmente gli export prima di condividerli. I controlli della build e i test applicativi non esistono finché ENG-03 non introduce una build.

### Criteri di revisione

La revisione controlla che:

- requisiti, casi d'uso, codice, test e documentazione restino tracciabili,
- input esterni siano validati al confine e regole di dominio siano rappresentate con tipi e invarianti espliciti,
- dati assenti o scaduti non siano mostrati come prova di accessibilità,
- autorizzazioni e transizioni siano verificate lato server e gli effetti atomici siano coperti da test,
- log, esempi e fixture non contengano segreti, codici di tracking reali o dati personali,
- interfacce, viste testuali e flussi essenziali siano verificati con tastiera e tecnologie assistive,
- le verifiche riportate siano riproducibili e collegate all'ambiente e alla versione usati.

### Stato della governance GitHub

Verifica di sola lettura del repository `kairosci/aegis` il 16 novembre 2026:

| Controllo | Stato osservato |
|---|---|
| Visibilità e branch predefinito | Pubblico. Branch predefinito: `main` |
| Licenza dichiarata su GitHub | Non configurata |
| Issues | Abilitate |
| Protezione di `main` e ruleset | Non configurati |
| Avvisi di vulnerabilità Dependabot | Abilitati per le dipendenze rilevate |
| Dependabot security updates | Abilitati |
| Aggiornamenti Dependabot | Dipendenze Node documentali, controllo settimanale |
| Secret scanning e push protection | Abilitati |
| Rilevamento di pattern non provider | Disabilitato. Eleggibilità dell'account da verificare secondo OPEN-11. |
| Verifica di validità dei segreti rilevati | Disabilitata. L'invio dei segreti agli issuer e l'eleggibilità sono da approvare secondo OPEN-11. |
| Segnalazione privata delle vulnerabilità GitHub | Abilitata |
| Collegamento al report privato nel flusso issue | Configurato in `.github/ISSUE_TEMPLATE/config.yml` |
| Release | Nessuna release pubblicata |

Dependabot controlla settimanalmente gli aggiornamenti delle dipendenze Node documentali. Non sono configurati workflow CI o controlli automatici sul repository. Gli avvisi e la segnalazione privata delle vulnerabilità sono attivi. Il proprietario deve ancora decidere la licenza e i termini di contribuzione secondo OPEN-10, la protezione di `main` secondo OPEN-09 e le impostazioni aggiuntive di scansione secondo OPEN-11.

La verifica delle impostazioni GitHub è stata eseguita in sola lettura il 16 novembre 2026. Gli endpoint sono relativi a `https://api.github.com`.

| Evidenza | Endpoint o artefatto | Risultato osservato |
|---|---|---|
| Visibilità, branch predefinito e licenza | `GET /repos/kairosci/aegis` tramite `gh repo view` | Pubblico, `main`, licenza assente. |
| Protezione del branch | `GET /repos/kairosci/aegis/branches/main/protection` | `404`, branch non protetto. |
| Ruleset | `GET /repos/kairosci/aegis/rulesets` | Elenco vuoto. |
| Avvisi di vulnerabilità | `GET /repos/kairosci/aegis/vulnerability-alerts` | Attivi, risposta positiva senza corpo. |
| Aggiornamenti di sicurezza Dependabot | `GET /repos/kairosci/aegis/automated-security-fixes` | `enabled: true`. |
| Scansione e protezione dei segreti | Proprietà `security_and_analysis` di `GET /repos/kairosci/aegis` | Scansione e push protection attive. Pattern non provider e controlli di validità disattivati. |
| Segnalazione privata delle vulnerabilità | `GET /repos/kairosci/aegis/private-vulnerability-reporting` | `enabled: true`. |
| Configurazione versionata | [.github/dependabot.yml](.github/dependabot.yml) e [.github/ISSUE_TEMPLATE/config.yml](.github/ISSUE_TEMPLATE/config.yml) | Aggiornamenti settimanali documentati e collegamento al report privato configurato. |

Questi risultati descrivono lo stato osservato in quella data e non sostituiscono la decisione del proprietario su OPEN-09, OPEN-10 e OPEN-11. Prima di modificare le impostazioni aggiuntive di scansione, verificare l'eleggibilità dell'account, gli effetti dell'invio dei segreti agli issuer e chi riceve gli avvisi.
