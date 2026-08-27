# Arena

RPG a squadre con combattimento a turni. Il giocatore compone una squadra di
combattenti, ognuno con statistiche, elemento di appartenenza e abilita', e
affronta avversari in un torneo a difficolta' crescente. Tra una battaglia e
l'altra la squadra cresce di livello e la progressione viene salvata su disco.

Progetto per il corso di **Metodologie di Programmazione, Modellazione e
Gestione della Conoscenza** - A.A. 2025/26, Universita' di Camerino.

---

## Come eseguire il progetto

### Prerequisiti

- **Java 21** o successivo
  Se sulla macchina non e' presente un JDK 21, Gradle lo scarica
  automaticamente tramite il meccanismo delle *toolchain*.
- Nessun'altra installazione richiesta: Gradle viene fornito dal *wrapper*
  incluso nel repository.

### Istruzioni

```bash
git clone <url-del-repository>
cd arena-rpg
```

### Build del progetto

```bash
./gradlew build
```

### Esecuzione

```bash
./gradlew run
```

Su Windows, se si usa `cmd` o PowerShell, i comandi sono `gradlew.bat build` e
`gradlew.bat run`.

---

## Struttura del progetto

```
src/main/java/it/unicam/cs/mpgc/rpg125949/
├── domain/         Modello di dominio, indipendente da interfaccia e persistenza
├── application/    Casi d'uso e orchestrazione; definisce le porte di persistenza
├── persistence/    Implementazioni delle porte di persistenza
└── ui/             Interfaccia grafica JavaFX
```

Le dipendenze fra i package puntano sempre verso l'interno: `domain` non
conosce nessuno degli altri, e nessuna classe di `domain` o `application`
importa JavaFX. Questo permette di affiancare in futuro interfacce diverse
(web, mobile) riutilizzando integralmente la logica di gioco.

La documentazione completa - funzionalita', responsabilita' delle classi,
organizzazione dei dati e meccanismi di estensione - si trova nella
**Wiki del repository**.

---

## Dichiarazione di utilizzo di strumenti di AI

Nella realizzazione di questo progetto e' stato utilizzato **Claude
(Anthropic)**, attraverso l'interfaccia Claude Code, per le seguenti attivita':

- discussione e definizione dell'architettura del progetto e della suddivisione
  in package;
- supporto nella diagnosi di problemi di configurazione dell'ambiente di
  sviluppo (build Gradle, dipendenze, esecuzione);
- stesura della documentazione.

Le decisioni progettuali sono state discusse e approvate dallo studente, che ha
verificato il funzionamento del codice tramite build e test automatici.

Una descrizione **dettagliata e suddivisa per fase di sviluppo** dell'uso di
strumenti di AI, con l'indicazione dello scopo di ogni utilizzo, e' riportata
nella **Wiki del repository**, come richiesto dalla specifica del progetto.
