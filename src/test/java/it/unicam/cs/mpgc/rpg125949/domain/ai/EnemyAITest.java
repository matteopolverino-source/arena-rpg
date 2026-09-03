package it.unicam.cs.mpgc.rpg125949.domain.ai;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Stats;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;
import it.unicam.cs.mpgc.rpg125949.domain.combat.DamageAbility;
import it.unicam.cs.mpgc.rpg125949.domain.combat.DamageCalculator;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Element;
import it.unicam.cs.mpgc.rpg125949.domain.combat.HealAbility;
import it.unicam.cs.mpgc.rpg125949.domain.combat.StandardDamageCalculator;
import it.unicam.cs.mpgc.rpg125949.domain.combat.StandardEffectivenessChart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica le tre intelligenze artificiali disponibili. Le prime prove
 * valgono per tutte: qualunque IA deve scegliere una delle abilita' che le
 * sono state offerte, senza mai restituire nulla.
 */
class EnemyAITest {

    private DamageCalculator calculator;
    private Fighter enemy;
    private Fighter hero;
    private Ability weakAttack;
    private Ability strongAttack;
    private Ability heal;

    @BeforeEach
    void setUp() {
        calculator = new StandardDamageCalculator(new StandardEffectivenessChart());
        enemy = new Fighter("Nemico", Element.FIRE, new Stats(100, 40, 20, 30));
        hero = new Fighter("Eroe", Element.NATURE, new Stats(100, 40, 20, 30));
        weakAttack = new DamageAbility("Colpo lieve", Element.NEUTRAL, 10, calculator);
        strongAttack = new DamageAbility("Colpo pesante", Element.NEUTRAL, 90, calculator);
        heal = new HealAbility("Ristoro", Element.WATER, 40);
    }

    private List<EnemyAI> allStrategies() {
        return List.of(new RandomAI(new Random(1)), new AggressiveAI(), new TacticalAI());
    }

    @Test
    void everyStrategyChoosesOneOfTheOfferedAbilities() {
        List<Ability> offered = List.of(weakAttack, strongAttack, heal);

        for (EnemyAI strategy : allStrategies()) {
            Ability chosen = strategy.chooseAbility(enemy, hero, offered);

            assertAll(
                    () -> assertNotNull(chosen, strategy.getClass().getSimpleName() + " non ha scelto nulla"),
                    () -> assertTrue(offered.contains(chosen),
                            strategy.getClass().getSimpleName() + " ha scelto un'abilita' non disponibile")
            );
        }
    }

    @Test
    void everyStrategyRejectsAnEmptyChoice() {
        for (EnemyAI strategy : allStrategies()) {
            assertAll(
                    () -> assertThrows(IllegalArgumentException.class,
                            () -> strategy.chooseAbility(enemy, hero, List.of())),
                    () -> assertThrows(NullPointerException.class,
                            () -> strategy.chooseAbility(enemy, hero, null)),
                    () -> assertThrows(NullPointerException.class,
                            () -> strategy.chooseAbility(null, hero, List.of(weakAttack))),
                    () -> assertThrows(NullPointerException.class,
                            () -> strategy.chooseAbility(enemy, null, List.of(weakAttack)))
            );
        }
    }

    /**
     * A parita' di seme il comportamento deve ripetersi identico: senza
     * questa garanzia una partita non sarebbe riproducibile e i test
     * diventerebbero instabili.
     */
    @Test
    void theRandomStrategyIsReproducibleGivenTheSameSeed() {
        List<Ability> offered = List.of(weakAttack, strongAttack, heal);

        List<Ability> firstRun = List.of(
                new RandomAI(new Random(42)).chooseAbility(enemy, hero, offered),
                new RandomAI(new Random(42)).chooseAbility(enemy, hero, offered));

        assertSame(firstRun.get(0), firstRun.get(1));
    }

    @Test
    void theAggressiveStrategyAlwaysPicksTheHardestHittingAbility() {
        Ability chosen = new AggressiveAI().chooseAbility(enemy, hero, List.of(weakAttack, strongAttack, heal));

        assertSame(strongAttack, chosen);
    }

    /**
     * La strategia aggressiva non deve mai curarsi, nemmeno in fin di vita:
     * e' cio' che la distingue da quella tattica.
     */
    @Test
    void theAggressiveStrategyAttacksEvenWhenNearlyDefeated() {
        enemy.takeDamage(95);

        Ability chosen = new AggressiveAI().chooseAbility(enemy, hero, List.of(strongAttack, heal));

        assertSame(strongAttack, chosen);
    }

    @Test
    void theTacticalStrategyHealsItselfWhenSeverelyWounded() {
        enemy.takeDamage(80);

        Ability chosen = new TacticalAI().chooseAbility(enemy, hero, List.of(strongAttack, heal));

        assertSame(heal, chosen);
    }

    @Test
    void theTacticalStrategyAttacksWhileStillHealthy() {
        Ability chosen = new TacticalAI().chooseAbility(enemy, hero, List.of(weakAttack, strongAttack, heal));

        assertSame(strongAttack, chosen);
    }

    /**
     * Se e' ferita ma non ha cure a disposizione, la strategia tattica non
     * deve bloccarsi: attacca con quello che ha.
     */
    @Test
    void theTacticalStrategyFallsBackToAttackingWhenItCannotHeal() {
        enemy.takeDamage(90);

        Ability chosen = new TacticalAI().chooseAbility(enemy, hero, List.of(weakAttack, strongAttack));

        assertSame(strongAttack, chosen);
    }

    /**
     * La scelta della IA non deve alterare lo stato dei combattenti: sceglie
     * soltanto, non agisce.
     */
    @Test
    void choosingDoesNotAlterTheFighters() {
        for (EnemyAI strategy : allStrategies()) {
            Fighter untouchedEnemy = new Fighter("Nemico", Element.FIRE, new Stats(100, 40, 20, 30));
            Fighter untouchedHero = new Fighter("Eroe", Element.NATURE, new Stats(100, 40, 20, 30));

            strategy.chooseAbility(untouchedEnemy, untouchedHero, List.of(weakAttack, strongAttack, heal));

            assertAll(
                    () -> assertEquals(100, untouchedEnemy.getCurrentHp()),
                    () -> assertEquals(100, untouchedHero.getCurrentHp())
            );
        }
    }
}
