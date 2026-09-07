package it.unicam.cs.mpgc.rpg125949.persistence;

import it.unicam.cs.mpgc.rpg125949.application.GameProgress;
import it.unicam.cs.mpgc.rpg125949.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg125949.application.port.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonGameRepositoryTest {

    @TempDir
    Path directory;

    private Path saveFile;
    private GameRepository repository;

    @BeforeEach
    void setUp() {
        saveFile = directory.resolve("partita.json");
        repository = new JsonGameRepository(saveFile);
    }

    @Test
    void readsBackExactlyWhatItSaved() {
        GameProgress original = new GameProgress(3, Map.of("Kael", 80, "Mira", 45, "Toren", 160));

        repository.save(original);

        assertEquals(original, repository.load().orElseThrow());
    }

    @Test
    void reportsNoProgressWhenNothingWasEverSaved() {
        assertTrue(repository.load().isEmpty());
    }

    @Test
    void replacesThePreviousSave() {
        repository.save(new GameProgress(1, Map.of("Kael", 10)));

        repository.save(new GameProgress(4, Map.of("Kael", 120)));

        GameProgress loaded = repository.load().orElseThrow();
        assertAll(
                () -> assertEquals(4, loaded.stageIndex()),
                () -> assertEquals(120, loaded.healthByFighter().get("Kael"))
        );
    }

    @Test
    void removesTheSavedProgress() {
        repository.save(new GameProgress(2, Map.of("Kael", 50)));

        repository.clear();

        assertAll(
                () -> assertTrue(repository.load().isEmpty()),
                () -> assertFalse(Files.exists(saveFile))
        );
    }

    @Test
    void clearingWhenNothingIsSavedIsNotAnError() {
        repository.clear();

        assertTrue(repository.load().isEmpty());
    }

    /**
     * Un salvataggio danneggiato deve essere segnalato come guasto di
     * persistenza, non far trapelare l'eccezione della libreria usata: chi
     * chiama non deve sapere che sotto c'e' del JSON.
     */
    @Test
    void reportsADamagedSaveAsAPersistenceFailure() throws Exception {
        Files.writeString(saveFile, "questo non e' affatto JSON {{{");

        assertThrows(PersistenceException.class, () -> repository.load());
    }

    @Test
    void createsTheContainingDirectoryIfItIsMissing() {
        Path nested = directory.resolve("salvataggi").resolve("partita.json");
        GameRepository deep = new JsonGameRepository(nested);

        deep.save(new GameProgress(0, Map.of("Kael", 100)));

        assertAll(
                () -> assertTrue(Files.exists(nested)),
                () -> assertEquals(0, deep.load().orElseThrow().stageIndex())
        );
    }

    /**
     * Il file deve restare leggibile da una persona: e' il modo piu' semplice
     * per verificare un salvataggio senza avviare il gioco.
     */
    @Test
    void writesAFileAPersonCanRead() throws Exception {
        repository.save(new GameProgress(2, Map.of("Kael", 80)));

        String content = Files.readString(saveFile);

        assertAll(
                () -> assertTrue(content.contains("Kael"), "il file non nomina i combattenti"),
                () -> assertTrue(content.contains("80"), "il file non riporta i punti vita"),
                () -> assertTrue(content.lines().count() > 1, "il file non e' impaginato su piu' righe")
        );
    }

    @Test
    void rejectsAnInvalidConstruction() {
        assertThrows(NullPointerException.class, () -> new JsonGameRepository(null));
    }

    @Test
    void rejectsAMissingProgress() {
        assertThrows(NullPointerException.class, () -> repository.save(null));
    }
}
