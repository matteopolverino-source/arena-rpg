package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;

class HealAbilityTest {

    /** Repertorio minimo: un combattente deve conoscere almeno un'abilita'. */
    private static final List<Ability> ABILITIES =
            List.of(new HealAbility("Riposo", Element.NEUTRAL, 1));

    private Fighter user;
    private Fighter target;

    @BeforeEach
    void setUp() {
        user = new Fighter("Mira", Element.WATER, new Stats(100, 20, 20, 20), ABILITIES);
        target = new Fighter("Toren", Element.NEUTRAL, new Stats(100, 20, 20, 20), ABILITIES);
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
    void declaresThatItTargetsWhoeverUsesIt() {
        Ability ability = new HealAbility("Rugiada", Element.WATER, 30);

        assertEquals(TargetType.SELF, ability.getTargetType());
    }

    @Test
    void estimatesTheHealingWithoutApplyingIt() {
        target.takeDamage(50);
        Ability ability = new HealAbility("Rugiada", Element.WATER, 30);

        int estimate = ability.estimateEffect(user, target);

        assertAll(
                () -> assertEquals(30, estimate),
                () -> assertEquals(50, target.getCurrentHp(), "la stima non deve curare")
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
