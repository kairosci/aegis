# ADR-0001: baseline Java e Jakarta EE

## Metadati

| Campo | Valore |
|---|---|
| ID | ADR-0001 |
| Titolo | Baseline Java e Jakarta EE |
| Stato | Proposto |
| Data | 16 novembre 2026 |
| Responsabile | Da designare |
| Revisori | Responsabile tecnico e team di progetto, da designare |
| Requisiti correlati | Vincolo piattaforma in RAD, NFR-12, NFR-15, NFR-16, OPEN-08, ENG-02 ed ENG-03 |

## Contesto e problema

RAD e SDD prescrivono Java LTS, Jakarta EE, Jakarta Faces e Jakarta REST, ma il repository non ha ancora una versione Java, una versione Jakarta EE o un runtime configurati. La scelta determina la toolchain, le API disponibili, la build, il packaging, le immagini di esecuzione e le verifiche di compatibilità.

La piattaforma Jakarta EE 11 supporta Java SE 17 o successivo. WildFly 42 standard dichiara compatibilità con Jakarta EE 11 Platform, Web Profile e Core Profile su Java SE 17 e Java SE 21. Il progetto WildFly raccomanda Java SE 25 per WildFly 42, ma dichiara di non rivendicare la compatibilità EE 11 su Java SE 25. Per una versione con evidenza TCK EE 11 indica Java SE 21 o 17. Lo stesso annuncio pianifica per WildFly 44, atteso nella primavera 2027, il passaggio della baseline a Java SE 21 e indica WildFly 43 come ultima versione che supporterà Java SE 17. L'opzione Java 21 evita quindi una migrazione immediata alla baseline successiva prevista. Oracle classifica Java 21 e Java 25 come LTS nella propria roadmap. La durata del supporto dipende dalla distribuzione Java scelta.

Fonti verificate il 17 novembre 2026:

- [Jakarta EE 11: release e piattaforma](https://jakarta.ee/release/11/)
- [WildFly 42: release, compatibilità EE 11 e supporto Java SE](https://www.wildfly.org/news/2026/10/22/WildFly-42-is-released/)
- [Oracle Java SE Support Roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html)

## Driver decisionali

| Driver | Criterio |
|---|---|
| Compatibilità EE verificabile | Preferire una combinazione runtime/JDK con evidenza di compatibilità Jakarta EE 11, se richiesta dal corso e dal progetto. |
| Supporto nel tempo | Usare una release Java LTS e verificare durata e licenza della distribuzione prima di fissare gli ambienti. |
| API necessarie | Il profilo selezionato deve includere Faces, REST, CDI, Persistence, Validation e Transactions richiesti dall’SDD. |
| Riproducibilità | La stessa versione di JDK e runtime deve essere selezionabile in sviluppo, CI e ambienti distribuiti. |
| Portabilità | Evitare API specifiche del vendor salvo ADR separato e motivato. |

## Opzioni considerate

| Opzione | Vantaggi | Costi e rischi | Requisiti soddisfatti o non soddisfatti |
|---|---|---|---|
| Jakarta EE 11, Java 21 LTS, WildFly 42 standard | WildFly dichiara evidenza di compatibilità EE 11 su Java 21. Oracle classifica Java 21 come LTS. | La distribuzione Java e il relativo supporto devono essere scelti. L’orizzonte di supporto varia per vendor. | Allinea Faces/REST e consente di basarsi sull’evidenza TCK pubblicata dal runtime. |
| Jakarta EE 11, Java 25 LTS, WildFly 42 standard | Oracle classifica Java 25 come LTS e il progetto WildFly lo raccomanda per il runtime 42. | WildFly non dichiara la compatibilità EE 11 su Java 25. Il progetto dovrebbe verificare la combinazione e non presentarla come certificata senza evidenza. | Potenzialmente soddisfa la piattaforma, ma la compatibilità formale EE 11 resta da dimostrare. |
| Rinviare la selezione del runtime | Nessun vincolo prematuro prima di definire deployment e supporto. | Blocca la build applicativa, le immagini runtime e le verifiche d’integrazione. | Mantiene aperti i vincoli software del RAD e non consente ENG-03. |

## Proposta per approvazione

Adottare Jakarta EE 11 con Java SE 21 LTS e WildFly 42 standard come baseline iniziale. Questa proposta privilegia l’evidenza di compatibilità EE 11 del runtime su Java 21. La distribuzione JDK e il modello di supporto devono essere selezionati separatamente. La proposta non autorizza ancora a dichiarare scelti il runtime o il provider di deployment.

## Decisione

Non approvata. Compilare questa sezione dopo la revisione del responsabile tecnico e la verifica dei vincoli del corso e di distribuzione.

## Conseguenze attese se approvata

- La build locale e la CI fissano la stessa versione Java e Jakarta EE.
- Il runtime standard e non Preview è usato negli ambienti concordati.
- Le dipendenze restano sulle API Jakarta EE standard. Le API specifiche WildFly richiedono un ADR distinto.
- L’aggiornamento a Java 25 richiede verifica di compatibilità, build e test d’integrazione sul runtime scelto.
- README, SDD, configurazione di build e pipeline vengono aggiornati nello stesso cambiamento che approva questa decisione.

## Verifica e revisione

| Evidenza richiesta | Criterio di accettazione | Responsabile | Esito e riferimento |
|---|---|---|---|
| Revisione della proposta | Approvazione esplicita della baseline e della distribuzione JDK | Da designare | In attesa |
| Build minima con Jakarta Faces e Jakarta REST | Build ripetibile in CI e avvio sul runtime selezionato | Responsabile tecnico | Non eseguito |
| Verifica di compatibilità | Riferimento alla certificazione e test d’integrazione sul JDK/runtime fissati | Responsabile tecnico | Non eseguito |
| Revisione del supporto | Versioni e durata di supporto della distribuzione JDK e del runtime documentate | Responsabile operativo | Non eseguito |

Riesaminare la scelta alla disponibilità di nuove release Jakarta EE o in caso di cambiamento del deployment, dei requisiti di supporto o della compatibilità ufficiale.
