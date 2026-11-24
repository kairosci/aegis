# ADR-0010: baseline di valutazione dell’accessibilità

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0010 |
| Titolo | Baseline di valutazione dell’accessibilità |
| Stato | Proposto |
| Data | 16 novembre 2026 |
| Responsabile | Referente accessibilità, da designare |
| Revisori | Responsabile di prodotto e partecipanti con disabilità, da designare |
| Requisiti correlati | NFR-01, NFR-02, NFR-03, NFR-13, UC-01, UC-02, UC-03, UC-04, OPEN-06, ENG-02, ENG-05 ed ENG-09 |

## Contesto e problema

RAD richiede uso da tastiera e screen reader, un’alternativa testuale alla mappa e verifiche di contrasto e zoom. Il test di usabilità è pianificato ma non eseguito. La sezione su ISO 9241-110 descrive principi di usabilità, ma non seleziona criteri di conformità per i contenuti web. OPEN-06 lascia da approvare standard, livello e protocollo con le persone coinvolte.

W3C ha pubblicato WCAG 2.2 come raccomandazione e raccomanda di adottarla come target al posto di WCAG 2.1. Il livello AA include i criteri di livello A e AA. W3C precisa inoltre che le linee guida non soddisfano ogni combinazione di disabilità e che gli strumenti automatici non possono determinare da soli l’accessibilità. Le fonti sono [WCAG 2.2](https://www.w3.org/TR/WCAG22/), [WCAG Overview](https://www.w3.org/WAI/standards-guidelines/wcag/) e [WAI: valutare l’accessibilità](https://www.w3.org/WAI/test-evaluate/).

Questa è una proposta di qualità del prodotto. Non interpreta la legislazione applicabile e non dichiara conformità legale o certificazione.

## Driver decisionali

| Driver | Criterio |
|---|---|
| Criteri verificabili | Il target identifica versione, livello e criteri che si possono valutare. |
| Copertura dei flussi | La valutazione comprende pagine, stati e processi completi, inclusi errori e contenuti dinamici. |
| Evidenza riproducibile | Ogni criterio applicabile ha esito, metodo, ambiente e riferimento all’evidenza. |
| Verifica multimodale | Analisi automatica, ispezione manuale, tastiera e tecnologie assistive sono usate con ruoli distinti. |
| Esperienza diretta | Persone con disabilità partecipano alla validazione dei compiti e dei problemi d’uso. |
| Limiti espliciti | Conformità al target, usabilità osservata e obblighi legali restano conclusioni distinte. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| WCAG 2.2 livello AA | Target web stabile del W3C con criteri di livello A e AA. Include i criteri aggiunti nella versione 2.2. | Richiede verifica manuale completa delle pagine e dei processi e valutazione di tecnologie assistive e contenuti dinamici. Non copre ogni esigenza né decide gli obblighi legali. | Può dare un criterio osservabile per NFR-01, NFR-02 e NFR-03 se approvato in OPEN-06 e affiancato da prove con utenti. |
| WCAG 2.1 livello AA | Target web ampiamente riconosciuto e compatibile con i criteri precedenti della serie WCAG. | Non include i criteri aggiunti in WCAG 2.2. L’eventuale richiesta da parte di una regola esterna deve essere identificata dal responsabile competente. | Può essere mantenuto se OPEN-06 o la verifica degli obblighi applicabili lo richiede, con una motivazione registrata. |
| Verifiche euristiche senza target WCAG | Consente controlli mirati durante la prototipazione. | Non offre una base completa e uniforme per criteri, esiti o dichiarazioni di conformità. | Utile come attività formativa, ma insufficiente da sola a chiudere OPEN-06 e NFR-03. |

## Proposta per approvazione

Adottare WCAG 2.2 livello AA come baseline di qualità del prodotto, dopo l’approvazione del referente accessibilità e dei rappresentanti degli utenti in OPEN-06. Mantenere ISO 9241-110 come riferimento complementare per la valutazione dell’usabilità.

- Definire l’inventario delle pagine, dei componenti riutilizzati, degli stati dinamici e dei processi completi. Includere pianificazione, consultazione di un luogo, invio, tracking e gli stati di errore.
- Valutare ogni criterio WCAG 2.2 di livello A e AA come applicabile o non applicabile. Ogni esito non applicabile richiede una motivazione. Per ogni criterio applicabile registrare superato, non superato o non valutato, metodo, pagina o processo, ambiente e collegamento all’evidenza.
- Usare strumenti automatici per individuare possibili problemi e regressioni. Affiancarli a revisione manuale, navigazione da tastiera, zoom, contrasto, nome e stato dei controlli, annunci con screen reader e confronto tra mappa e vista testuale. Un risultato automatico non è una dichiarazione di conformità.
- Svolgere sessioni con persone con disabilità sui flussi approvati. Concordare prima reclutamento, compiti, tecnologie assistive, consenso, misure, registrazione, analisi e gestione dei dati secondo OPEN-05 e OPEN-06.
- Conservare separati gli esiti di conformità, i problemi osservati nelle sessioni e le valutazioni di usabilità. Collegare problemi e interventi ai criteri e ai casi d’uso interessati.
- Pubblicare eventuali dichiarazioni sul livello di conformità solo dopo una valutazione completa del perimetro dichiarato e una revisione competente. Esplicitare pagine, data, versione, limiti e criteri non superati. W3C non verifica le dichiarazioni pubblicate dai progetti.
- Verificare in OPEN-06 se gli obblighi normativi o contrattuali richiedono standard, edizioni o livelli differenti. L’esito legale resta una decisione separata e non è dedotto da questo ADR.

Non assegnare soglie di successo ai test con utenti, dimensione del campione o criteri quantitativi prima della definizione condivisa del protocollo. I valori e gli esiti mancanti restano da concordare o non eseguiti.

## Decisione

Non approvata. Il referente accessibilità, il responsabile di prodotto e i partecipanti devono approvare target, perimetro dei flussi, metodi e protocollo. Una revisione competente deve verificare separatamente eventuali obblighi legali applicabili al progetto.

## Conseguenze attese se approvata

- NFR-03 ha un target e criteri osservabili per ogni pagina o processo incluso nel perimetro.
- I risultati automatici, manuali e con utenti sono documentati come evidenze distinte.
- I requisiti per tastiera, screen reader, zoom, focus, contrasto e alternativa testuale diventano verificabili rispetto a criteri identificati.
- L’eventuale dichiarazione di conformità ha perimetro, versione, data, evidenze e limiti espliciti.
- ISO 9241-110 rimane il riferimento complementare per i principi di usabilità, non un sostituto dei criteri di accessibilità web.
- Standard o livelli diversi possono essere adottati solo con motivazione e decisione registrata in OPEN-06.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Approvazione OPEN-06 | Versione, livello, perimetro delle viste e compiti sono approvati dai responsabili e dai partecipanti | Referente accessibilità e responsabile di prodotto, da designare | In attesa |
| Matrice di conformità | Ogni criterio A e AA nel perimetro ha esito, applicabilità motivata, metodo e riferimento consultabile | Referente accessibilità, da designare | Non eseguito |
| Verifica manuale e assistiva | Tastiera, focus, zoom, contrasto, screen reader, contenuti dinamici e alternativa testuale sono verificati nell’ambiente registrato | Team di verifica e partecipanti, da designare | Non eseguito |
| Sessioni con utenti | Compiti, consenso, ambiente, misure e risultati sono registrati secondo il protocollo approvato | Responsabile di prodotto e referente accessibilità, da designare | Non eseguito |
| Analisi degli obblighi applicabili | Standard e livello obbligatori sono verificati e distinti dalla baseline di prodotto | Responsabile competente, da designare | In attesa |
| Dichiarazione pubblica, se prevista | Ogni affermazione di conformità indica perimetro, versione, data, limiti ed evidenze revisionate | Proprietario del prodotto e referente accessibilità | Non eseguito |

Riesaminare la decisione se cambia la versione di WCAG adottata, il perimetro del prodotto, i flussi principali o gli obblighi esterni verificati.
