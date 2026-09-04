package it.unicam.cs.mpgc.rpg125949.application;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameProgressTest {

    @Test
    void keepsTheStageAndTheHealthOfEachFighter() {
        GameProgress progress = new GameProgress(2, Map.of("Kael", 80, "Mira", 45));

        assertAll(
                () -> assertEquals(2, progress.stageIndex()),
                () -> assertEquals(80, progress.healthByFighter().get("Kael")),
                () -> assertEquals(45, progress.healthByFighter().get("Mira"))
        );
    }

    @Test
    void cannotBeAlteredAfterItHasBeenCreated() {
        Map<String, Integer> source = new java.util.HashMap<>(Map.of("Kael", 80));
        GameProgress progress = new GameProgress(0, source);

        source.clear();

        assertAll(
                () -> assertEquals(1, progress.healthByFighter().size()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> progress.healthByFighter().put("Intruso", 1))
        );
    }

    @Test
    void rejectsAnInvalidProgress() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new GameProgress(0, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new GameProgress(-1, Map.of())),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new GameProgress(0, Map.of("Kael", -5)))
        );
    }

    /**
     * Il progresso descrive lo stato salvato senza dipendere dagli oggetti di
     * dominio: e' cio' che permette di serializzarlo in qualunque formato.
     */
    @Test
    void describesTheSavedStateWithPlainValuesOnly() {
        List<Class<?>> types = new ArrayList<>();
        for (var component : GameProgress.class.getRecordComponents()) {
            types.add(component.getType());
        }

        assertEquals(List.of(int.class, Map.class), types);
    }
}
