package it.unicam.cs.mpgc.rpg125949.domain.character;

import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Element;
import it.unicam.cs.mpgc.rpg125949.domain.combat.HealAbility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FighterTest {

    private static final Stats BASE_STATS = new Stats(100, 20, 15, 30);

    /** Repertorio minimo: un combattente deve conoscere almeno un'abilita'. */
    private static final List<Ability> ABILITIES =
            List.of(new HealAbility("Riposo", Element.NEUTRAL, 1));

    private Fighter fighter;

    @BeforeEach
    void setUp() {
        fighter = new Fighter("Kael", Element.FIRE, BASE_STATS, ABILITIES);
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
    void knowsWhichAbilitiesItCanUse() {
        Ability rest = new HealAbility("Riposo", Element.NEUTRAL, 5);
        Fighter equipped = new Fighter("Kael", Element.FIRE, BASE_STATS, List.of(rest));

        assertEquals(List.of(rest), equipped.getAbilities());
    }

    /**
     * Un combattente privo di mosse non potrebbe agire durante il proprio
     * turno: e' uno stato che non deve poter esistere.
     */
    @Test
    void refusesToExistWithoutAnyAbility() {
        assertThrows(IllegalArgumentException.class,
                () -> new Fighter("Kael", Element.FIRE, BASE_STATS, List.of()));
    }

    @Test
    void doesNotLetItsRepertoireBeAlteredFromOutside() {
        Ability rest = new HealAbility("Riposo", Element.NEUTRAL, 5);
        List<Ability> source = new ArrayList<>(List.of(rest));
        Fighter equipped = new Fighter("Kael", Element.FIRE, BASE_STATS, source);

        source.clear();

        assertAll(
                () -> assertEquals(1, equipped.getAbilities().size()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> equipped.getAbilities().add(rest))
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
                () -> assertThrows(NullPointerException.class, () -> new Fighter(null, Element.FIRE, BASE_STATS, ABILITIES)),
                () -> assertThrows(NullPointerException.class, () -> new Fighter("Kael", null, BASE_STATS, ABILITIES)),
                () -> assertThrows(NullPointerException.class, () -> new Fighter("Kael", Element.FIRE, null, ABILITIES)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Fighter("   ", Element.FIRE, BASE_STATS, ABILITIES))
        );
    }
}
