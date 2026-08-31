package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HealAbilityTest {

    private Fighter user;
    private Fighter target;

    @BeforeEach
    void setUp() {
        user = new Fighter("Mira", Element.WATER, new Stats(100, 20, 20, 20));
        target = new Fighter("Toren", Element.NEUTRAL, new Stats(100, 20, 20, 20));
    }

    @Test
    void restoresHealthToTheTarget() {
        target.takeDamage(50);
        Ability ability = new HealAbility("Rugiada", Element.WATER, 30);

        ability.applyTo(user, target);

        assertEquals(80, target.getCurrentHp());
    }

    /**
     * Se il bersaglio e' quasi sano l'abilita' deve riportare i punti
     * effettivamente ripristinati, non quelli teorici: e' il numero che
     * l'interfaccia mostrera' al giocatore.
     */
    @Test
    void reportsOnlyTheHealthActuallyRestored() {
        target.takeDamage(5);
        Ability ability = new HealAbility("Rugiada", Element.WATER, 30);

        int restored = ability.applyTo(user, target);

        assertAll(
                () -> assertEquals(5, restored),
                () -> assertEquals(100, target.getCurrentHp())
        );
    }

    /**
     * Una cura e' un'abilita' come le altre: il motore di battaglia deve
     * poterla trattare senza sapere che tipo di abilita' sia.
     */
    @Test
    void isInterchangeableWithAnyOtherAbility() {
        Ability ability = new HealAbility("Rugiada", Element.WATER, 30);

        assertAll(
                () -> assertEquals("Rugiada", ability.getName()),
                () -> assertEquals(Element.WATER, ability.getElement())
        );
    }

    @Test
    void rejectsAnInvalidConstruction() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new HealAbility(null, Element.WATER, 30)),
                () -> assertThrows(NullPointerException.class,
                        () -> new HealAbility("Rugiada", null, 30)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new HealAbility("   ", Element.WATER, 30)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new HealAbility("Rugiada", Element.WATER, 0))
        );
    }

    @Test
    void rejectsNullParticipants() {
        Ability ability = new HealAbility("Rugiada", Element.WATER, 30);

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> ability.applyTo(null, target)),
                () -> assertThrows(NullPointerException.class, () -> ability.applyTo(user, null))
        );
    }
}
