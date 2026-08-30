package it.unicam.cs.mpgc.rpg125949.domain.character;

import it.unicam.cs.mpgc.rpg125949.domain.combat.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FighterTest {

    private static final Stats BASE_STATS = new Stats(100, 20, 15, 30);

    private Fighter fighter;

    @BeforeEach
    void setUp() {
        fighter = new Fighter("Kael", Element.FIRE, BASE_STATS);
    }

    @Test
    void startsAtFullHealth() {
        assertAll(
                () -> assertEquals(100, fighter.getCurrentHp()),
                () -> assertFalse(fighter.isDefeated())
        );
    }

    @Test
    void keepsTheIdentityItWasCreatedWith() {
        assertAll(
                () -> assertEquals("Kael", fighter.getName()),
                () -> assertEquals(Element.FIRE, fighter.getElement()),
                () -> assertEquals(BASE_STATS, fighter.getStats())
        );
    }

    @Test
    void losesHealthWhenDamaged() {
        fighter.takeDamage(30);

        assertEquals(70, fighter.getCurrentHp());
    }

    @Test
    void healthNeverDropsBelowZero() {
        fighter.takeDamage(250);

        assertEquals(0, fighter.getCurrentHp());
    }

    @Test
    void isDefeatedWhenHealthReachesZero() {
        fighter.takeDamage(100);

        assertTrue(fighter.isDefeated());
    }

    @Test
    void recoversHealthWhenHealed() {
        fighter.takeDamage(50);

        fighter.heal(20);

        assertEquals(70, fighter.getCurrentHp());
    }

    @Test
    void healthNeverExceedsTheMaximum() {
        fighter.takeDamage(10);

        fighter.heal(500);

        assertEquals(100, fighter.getCurrentHp());
    }

    @Test
    void rejectsNegativeAmounts() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> fighter.takeDamage(-1)),
                () -> assertThrows(IllegalArgumentException.class, () -> fighter.heal(-1))
        );
    }

    @Test
    void rejectsAnInvalidIdentity() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new Fighter(null, Element.FIRE, BASE_STATS)),
                () -> assertThrows(NullPointerException.class, () -> new Fighter("Kael", null, BASE_STATS)),
                () -> assertThrows(NullPointerException.class, () -> new Fighter("Kael", Element.FIRE, null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Fighter("   ", Element.FIRE, BASE_STATS))
        );
    }
}
