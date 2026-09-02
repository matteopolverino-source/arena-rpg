package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DamageAbilityTest {

    private DamageCalculator calculator;
    private Fighter user;
    private Fighter target;

    @BeforeEach
    void setUp() {
        calculator = new StandardDamageCalculator(new StandardEffectivenessChart());
        user = new Fighter("Kael", Element.FIRE, new Stats(100, 40, 10, 30));
        target = new Fighter("Sela", Element.NATURE, new Stats(100, 20, 20, 20));
    }

    @Test
    void exposesItsNameAndElement() {
        Ability ability = new DamageAbility("Fendente igneo", Element.FIRE, 40, calculator);

        assertAll(
                () -> assertEquals("Fendente igneo", ability.getName()),
                () -> assertEquals(Element.FIRE, ability.getElement())
        );
    }

    @Test
    void declaresThatItTargetsTheOpponent() {
        Ability ability = new DamageAbility("Fendente igneo", Element.FIRE, 40, calculator);

        assertEquals(TargetType.OPPONENT, ability.getTargetType());
    }

    @Test
    void reducesTheHealthOfTheTarget() {
        Ability ability = new DamageAbility("Fendente igneo", Element.FIRE, 40, calculator);

        ability.applyTo(user, target);

        assertTrue(target.getCurrentHp() < 100,
                "il bersaglio doveva subire danno, ha ancora " + target.getCurrentHp() + " HP");
    }

    @Test
    void dealsExactlyTheDamageComputedByTheCalculator() {
        Ability ability = new DamageAbility("Fendente igneo", Element.FIRE, 40, calculator);
        int expected = calculator.computeDamage(user, target, 40, Element.FIRE);

        int dealt = ability.applyTo(user, target);

        assertAll(
                () -> assertEquals(expected, dealt),
                () -> assertEquals(100 - expected, target.getCurrentHp())
        );
    }

    /**
     * Il danno inflitto non puo' superare i punti vita rimasti: l'abilita'
     * deve riportare quanto ha davvero tolto, non quanto avrebbe voluto.
     */
    @Test
    void reportsOnlyTheDamageActuallyInflicted() {
        Fighter almostDead = new Fighter("Toren", Element.NEUTRAL, new Stats(100, 10, 1, 10));
        almostDead.takeDamage(97);
        Ability ability = new DamageAbility("Colpo devastante", Element.NEUTRAL, 200, calculator);

        int dealt = ability.applyTo(user, almostDead);

        assertAll(
                () -> assertEquals(3, dealt),
                () -> assertEquals(0, almostDead.getCurrentHp()),
                () -> assertTrue(almostDead.isDefeated())
        );
    }

    /**
     * La stima serve alla IA per confrontare le opzioni prima di scegliere:
     * non deve produrre alcun effetto sul bersaglio.
     */
    @Test
    void estimatesTheDamageWithoutInflictingIt() {
        Ability ability = new DamageAbility("Fendente igneo", Element.FIRE, 40, calculator);
        int expected = calculator.computeDamage(user, target, 40, Element.FIRE);

        int estimate = ability.estimateEffect(user, target);

        assertAll(
                () -> assertEquals(expected, estimate),
                () -> assertEquals(100, target.getCurrentHp(), "la stima non deve infliggere danno")
        );
    }

    @Test
    void rejectsAnInvalidConstruction() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new DamageAbility(null, Element.FIRE, 40, calculator)),
                () -> assertThrows(NullPointerException.class,
                        () -> new DamageAbility("Fendente", null, 40, calculator)),
                () -> assertThrows(NullPointerException.class,
                        () -> new DamageAbility("Fendente", Element.FIRE, 40, null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new DamageAbility("   ", Element.FIRE, 40, calculator)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new DamageAbility("Fendente", Element.FIRE, 0, calculator))
        );
    }

    @Test
    void rejectsNullParticipants() {
        Ability ability = new DamageAbility("Fendente igneo", Element.FIRE, 40, calculator);

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> ability.applyTo(null, target)),
                () -> assertThrows(NullPointerException.class, () -> ability.applyTo(user, null))
        );
    }
}
