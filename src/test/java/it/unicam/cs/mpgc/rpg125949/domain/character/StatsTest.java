package it.unicam.cs.mpgc.rpg125949.domain.character;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatsTest {

    @Test
    void exposesTheValuesItWasCreatedWith() {
        Stats stats = new Stats(100, 20, 15, 30);

        assertAll(
                () -> assertEquals(100, stats.maxHp()),
                () -> assertEquals(20, stats.attack()),
                () -> assertEquals(15, stats.defense()),
                () -> assertEquals(30, stats.speed())
        );
    }

    @Test
    void rejectsNonPositiveMaxHp() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Stats(0, 20, 15, 30)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Stats(-1, 20, 15, 30))
        );
    }

    @Test
    void rejectsNegativeCombatValues() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new Stats(100, -1, 15, 30)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Stats(100, 20, -1, 30)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Stats(100, 20, 15, -1))
        );
    }

    @Test
    void comparesByValueAndNotByIdentity() {
        Stats first = new Stats(100, 20, 15, 30);
        Stats second = new Stats(100, 20, 15, 30);
        Stats different = new Stats(100, 20, 15, 31);

        assertAll(
                () -> assertEquals(first, second),
                () -> assertEquals(first.hashCode(), second.hashCode()),
                () -> assertNotEquals(first, different)
        );
    }
}
