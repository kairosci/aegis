# ADR-0009: scansione dei segreti e verifiche di validità

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0009 |
| Titolo | Scansione dei segreti e verifiche di validità |
| Stato | Proposto |
| Data | 16 novembre 2026 |
| Responsabile | Proprietario del repository e responsabile sicurezza, da designare |
| Revisori | Maintainer e responsabile privacy, da designare |
| Requisiti correlati | NFR-05, NFR-06, OPEN-11, ENG-02 ed ENG-07 |

## Contesto e problema

La configurazione GitHub osservata il 16 novembre 2026 mostra secret scanning e push protection attivi. I pattern non provider e i controlli di validità sono disattivati. L’API del repository identifica `kairosci` come account personale di tipo `User`, non come organizzazione.

I pattern generici possono rilevare credenziali non associate a uno specifico provider, come chiavi private e stringhe di connessione. Le verifiche di validità dei provider possono interrogare il servizio che ha emesso il segreto. Prima di abilitarle occorre verificare se il tipo di account e il piano danno accesso alle funzioni e approvare il trasferimento del segreto al relativo issuer.

La documentazione GitHub consultata il 16 novembre 2026 indica che la scansione dei pattern generici è disponibile per repository appartenenti a organizzazioni che hanno GitHub Secret Protection attivo. La pagina delle verifiche di validità per partner indica la stessa condizione di eleggibilità per organizzazioni su GitHub Team con Secret Protection. GitHub precisa inoltre che tali verifiche periodicamente inviano il segreto al relativo issuer per provarlo contro le API del servizio. Le fonti sono [scansione dei pattern generici](https://docs.github.com/en/enterprise-cloud@latest/code-security/how-tos/secure-your-secrets/detect-secret-leaks/enabling-secret-scanning-for-generic-patterns), [verifiche di validità](https://docs.github.com/en/code-security/how-tos/secure-your-secrets/customize-leak-detection/enable-validity-checks) e [descrizione delle verifiche di validità](https://docs.github.com/en/enterprise-cloud@latest/code-security/concepts/secret-security/validity-checks).

## Driver decisionali

| Driver | Criterio |
|---|---|
| Prevenzione immediata | Mantenere attivi rilevamento dei segreti dei provider e push protection già verificati. |
| Eleggibilità reale | Verificare account, piano e disponibilità dell’impostazione prima di prescriverne l’attivazione. |
| Minimizzazione della diffusione | Non trasmettere credenziali a un issuer per controllarne la validità senza approvazione esplicita. |
| Precisione gestibile | Considerare le categorie di pattern e le precisioni dichiarate da GitHub per gestire gli alert. |
| Risposta agli incidenti | Ogni alert deve avere responsabile e procedura per revoca, sostituzione e verifica degli accessi. |
| Configurazione verificabile | Stato documentato, impostazioni API e impostazioni GitHub devono corrispondere. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| Mantenere le funzioni aggiuntive disattivate | Evita abilitazioni non supportate dal tipo di account e l’invio automatico di credenziali agli issuer. Secret scanning e push protection dei provider restano attivi. | Non rileva i pattern generici aggiuntivi e non fornisce lo stato di validità dei token partner tramite questa funzione. | È compatibile con lo stato osservato. OPEN-11 richiede comunque una decisione motivata e registrata. |
| Abilitare i pattern non provider quando supportati | Estende il rilevamento a chiavi private, stringhe di connessione e altri segreti generici. | L’eleggibilità può dipendere da owner e piano. Alcuni pattern hanno precisione inferiore e richiedono gestione degli alert. | Può aumentare la copertura se GitHub lo rende disponibile per il repository e se il proprietario assegna la risposta agli alert. |
| Abilitare le verifiche di validità partner quando supportate | Aiuta a distinguere credenziali attive e inattive e a ordinare la risposta. | GitHub invia periodicamente i segreti supportati all’issuer per controllarne la validità. La funzione non supporta pattern generici e richiede eleggibilità del repository. | Può migliorare la priorità degli alert partner solo dopo approvazione del trasferimento e conferma di compatibilità. |

## Proposta per approvazione

Mantenere attivi secret scanning e push protection già configurati. Lasciare disattivate le due funzioni aggiuntive finché il proprietario non ha verificato l’eleggibilità dell’account e deciso se accettare i rispettivi effetti.

- Per i pattern non provider, controllare nell’interfaccia e nell’API se la funzione è effettivamente disponibile per il repository personale `kairosci/aegis`. Le fonti consultate descrivono la disponibilità per repository di organizzazioni con GitHub Secret Protection. Non presumere che la sola visibilità pubblica abiliti ogni categoria aggiuntiva.
- Per le verifiche di validità partner, documentare che GitHub invia periodicamente i segreti supportati all’issuer e che i pattern generici non sono verificabili con questa funzione. Richiedere approvazione del proprietario prima di abilitarla.
- Se la funzione non è eleggibile con l’account attuale, registrare la limitazione e lasciare invariata la configurazione. Un eventuale trasferimento a un’organizzazione o cambio di piano è una decisione distinta del proprietario.
- Definire chi riceve gli alert e come revocare o sostituire una credenziale esposta. Trattare ogni segreto trovato nel repository o nella cronologia Git come esposto, anche se una verifica indica stato inattivo.
- Dopo qualsiasi modifica, confrontare l’impostazione nell’interfaccia GitHub con `security_and_analysis` restituito dall’API del repository e aggiornare la tabella di governance in `docs/CONTRIBUTING.md` con data ed esito.

## Decisione

Non approvata. Il proprietario del repository e il responsabile sicurezza devono decidere le due funzioni separatamente. Prima dell’abilitazione devono confermare eleggibilità, gestione degli alert e, per i controlli di validità, accettazione dell’invio dei segreti ai rispettivi issuer.

## Conseguenze attese se approvata

- Le funzioni già attive restano distinte dai controlli opzionali e il loro stato è verificato periodicamente.
- I pattern generici vengono abilitati solo se supportati dall’account e se esiste un processo per triage e falsi positivi.
- Le verifiche di validità partner vengono attivate solo dopo l’approvazione dell’effetto di rete e del trattamento delle credenziali.
- L’eventuale indisponibilità per il tipo di account è documentata come limite, non come errore di configurazione risolvibile localmente.
- L’audit di OPEN-11 registra decisione, stato remoto e responsabile della risposta agli alert.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Eleggibilità dei pattern non provider | Interfaccia o API conferma disponibilità per tipo di owner e piano. Se non disponibile, il limite è registrato. | Proprietario del repository | In attesa |
| Decisione sui pattern non provider | Motivazione, stato desiderato e responsabile degli alert sono documentati | Proprietario e responsabile sicurezza, da designare | In attesa |
| Approvazione delle verifiche di validità | Proprietario approva l’invio periodico dei segreti supportati ai relativi issuer | Proprietario del repository | In attesa |
| Coerenza della configurazione | Impostazioni GitHub e API corrispondono allo stato riportato in CONTRIBUTING con data di verifica | Maintainer, da designare | Non eseguito |
| Prova della risposta a un alert | La procedura di revoca o sostituzione usa una credenziale sintetica o una simulazione e registra l’esito senza esporre segreti reali | Responsabile sicurezza, da designare | Non eseguito |

Riesaminare la decisione se cambia l’owner, il piano GitHub, l’eleggibilità delle funzioni, il processo di risposta agli alert o il comportamento documentato delle verifiche di validità.
