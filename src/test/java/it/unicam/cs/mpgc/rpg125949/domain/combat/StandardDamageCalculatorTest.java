package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;

class StandardDamageCalculatorTest {

    /** Repertorio minimo: un combattente deve conoscere almeno un'abilita'. */
    private static final List<Ability> ABILITIES =
            List.of(new HealAbility("Riposo", Element.NEUTRAL, 1));

    private DamageCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StandardDamageCalculator(new StandardEffectivenessChart());
    }

    private static Fighter fighter(Element element, int attack, int defense) {
        return new Fighter("Prova", element, new Stats(100, attack, defense, 10), ABILITIES);
    }

    @Test
    void strongerAttackersDealMoreDamage() {
        Fighter defender = fighter(Element.NEUTRAL, 10, 20);
        Fighter weak = fighter(Element.NEUTRAL, 20, 10);
        Fighter strong = fighter(Element.NEUTRAL, 60, 10);

        int weakDamage = calculator.computeDamage(weak, defender, 40, Element.NEUTRAL);
        int strongDamage = calculator.computeDamage(strong, defender, 40, Element.NEUTRAL);

        assertTrue(strongDamage > weakDamage,
                "atk 60 ha inflitto " + strongDamage + ", atk 20 ha inflitto " + weakDamage);
    }

    @Test
    void betterDefendersTakeLessDamage() {
        Fighter attacker = fighter(Element.NEUTRAL, 40, 10);
        Fighter fragile = fighter(Element.NEUTRAL, 10, 10);
        Fighter armoured = fighter(Element.NEUTRAL, 10, 60);

        int onFragile = calculator.computeDamage(attacker, fragile, 40, Element.NEUTRAL);
        int onArmoured = calculator.computeDamage(attacker, armoured, 40, Element.NEUTRAL);

        assertTrue(onFragile > onArmoured,
                "difesa 10 ha subito " + onFragile + ", difesa 60 ha subito " + onArmoured);
    }

    @Test
    void strongerAbilitiesDealMoreDamage() {
        Fighter attacker = fighter(Element.NEUTRAL, 40, 10);
        Fighter defender = fighter(Element.NEUTRAL, 10, 20);

        int light = calculator.computeDamage(attacker, defender, 20, Element.NEUTRAL);
        int heavy = calculator.computeDamage(attacker, defender, 80, Element.NEUTRAL);

        assertTrue(heavy > light, "potenza 80 ha inflitto " + heavy + ", potenza 20 ha inflitto " + light);
    }

    @Test
    void superEffectiveAbilitiesDealDoubleTheNeutralDamage() {
        Fighter attacker = fighter(Element.FIRE, 40, 10);
        Fighter natureDefender = fighter(Element.NATURE, 10, 20);
        Fighter neutralDefender = fighter(Element.NEUTRAL, 10, 20);

        int superEffective = calculator.computeDamage(attacker, natureDefender, 40, Element.FIRE);
        int neutral = calculator.computeDamage(attacker, neutralDefender, 40, Element.FIRE);

        assertAll(
                () -> assertTrue(neutral > 0, "il danno neutro deve essere positivo, era " + neutral),
                () -> assertEquals(neutral * 2, superEffective)
        );
    }

    @Test
    void resistedAbilitiesDealHalfTheNeutralDamage() {
        Fighter attacker = fighter(Element.NATURE, 40, 10);
        Fighter fireDefender = fighter(Element.FIRE, 10, 20);
        Fighter neutralDefender = fighter(Element.NEUTRAL, 10, 20);

        int resisted = calculator.computeDamage(attacker, fireDefender, 40, Element.NATURE);
        int neutral = calculator.computeDamage(attacker, neutralDefender, 40, Element.NATURE);

        assertAll(
                () -> assertTrue(neutral > 0, "il danno neutro deve essere positivo, era " + neutral),
                () -> assertEquals(neutral / 2, resisted)
        );
    }

    /**
     * Un attacco deve sempre lasciare il segno: senza questa regola un
     * difensore con difesa altissima diventerebbe invulnerabile e la
     * battaglia non potrebbe finire.
     */
    @Test
    void anAttackAlwaysDealsAtLeastOnePointOfDamage() {
        Fighter weakling = fighter(Element.NEUTRAL, 1, 10);
        Fighter fortress = fighter(Element.NEUTRAL, 10, 9999);

        assertEquals(1, calculator.computeDamage(weakling, fortress, 1, Element.NEUTRAL));
    }

    @Test
    void rejectsInvalidArguments() {
        Fighter a = fighter(Element.NEUTRAL, 20, 20);
        Fighter b = fighter(Element.NEUTRAL, 20, 20);

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> calculator.computeDamage(null, b, 40, Element.FIRE)),
                () -> assertThrows(NullPointerException.class,
                        () -> calculator.computeDamage(a, null, 40, Element.FIRE)),
                () -> assertThrows(NullPointerException.class,
                        () -> calculator.computeDamage(a, b, 40, null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calculator.computeDamage(a, b, 0, Element.FIRE)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calculator.computeDamage(a, b, -10, Element.FIRE))
        );
    }

    /**
     * Le statistiche ammettono difesa pari a zero: il calcolo non deve
     * dividere per zero.
     */
    @Test
    void handlesADefenderWithoutDefense() {
        Fighter attacker = fighter(Element.NEUTRAL, 40, 10);
        Fighter defenceless = fighter(Element.NEUTRAL, 10, 0);

        int damage = calculator.computeDamage(attacker, defenceless, 40, Element.NEUTRAL);

        assertTrue(damage > 0, "danno atteso positivo, era " + damage);
    }

    @Test
    void requiresAnEffectivenessChart() {
        assertThrows(NullPointerException.class, () -> new StandardDamageCalculator(null));
    }
}
