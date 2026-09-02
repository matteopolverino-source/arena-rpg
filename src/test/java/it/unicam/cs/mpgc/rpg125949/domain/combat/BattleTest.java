package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Stats;
import it.unicam.cs.mpgc.rpg125949.domain.character.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BattleTest {

    private DamageCalculator calculator;
    private TurnOrder turnOrder;

    @BeforeEach
    void setUp() {
        calculator = new StandardDamageCalculator(new StandardEffectivenessChart());
        turnOrder = new SpeedTurnOrder();
    }

    private Fighter fighter(String name, int maxHp, int attack, int defense, int speed) {
        return new Fighter(name, Element.NEUTRAL, new Stats(maxHp, attack, defense, speed));
    }

    private Ability attack(int power) {
        return new DamageAbility("Colpo", Element.NEUTRAL, power, calculator);
    }

    private Battle battleOf(Fighter player, Fighter enemy) {
        return new Battle(new Team(List.of(player)), new Team(List.of(enemy)), turnOrder);
    }

    @Test
    void theFasterFighterActsFirst() {
        Fighter slowPlayer = fighter("Lento", 100, 30, 10, 10);
        Fighter fastEnemy = fighter("Veloce", 100, 30, 10, 90);
        Battle battle = battleOf(slowPlayer, fastEnemy);

        List<TurnResult> results = battle.executeRound(attack(30), attack(30));

        assertAll(
                () -> assertEquals(2, results.size()),
                () -> assertSame(fastEnemy, results.get(0).actor()),
                () -> assertSame(slowPlayer, results.get(1).actor())
        );
    }

    @Test
    void anAttackTargetsTheOpposingFighter() {
        Fighter player = fighter("Eroe", 100, 40, 10, 90);
        Fighter enemy = fighter("Nemico", 100, 10, 10, 10);
        Battle battle = battleOf(player, enemy);

        List<TurnResult> results = battle.executeRound(attack(40), attack(10));

        assertAll(
                () -> assertSame(player, results.get(0).actor()),
                () -> assertSame(enemy, results.get(0).target()),
                () -> assertTrue(enemy.getCurrentHp() < 100)
        );
    }

    @Test
    void aHealTargetsWhoeverUsesIt() {
        Fighter player = fighter("Eroe", 100, 10, 10, 90);
        Fighter enemy = fighter("Nemico", 100, 10, 10, 10);
        player.takeDamage(50);
        Battle battle = battleOf(player, enemy);

        List<TurnResult> results = battle.executeRound(new HealAbility("Cura", Element.WATER, 30), attack(10));

        assertAll(
                () -> assertSame(player, results.get(0).actor()),
                () -> assertSame(player, results.get(0).target()),
                () -> assertEquals(30, results.get(0).effect())
        );
    }

    /**
     * Chi viene sconfitto prima del proprio turno non agisce: senza questa
     * regola un combattente gia' a terra riuscirebbe comunque a colpire.
     */
    @Test
    void aFighterDefeatedBeforeItsTurnDoesNotAct() {
        Fighter fastPlayer = fighter("Fulmine", 100, 200, 10, 99);
        Fighter fragileEnemy = fighter("Fragile", 5, 30, 1, 1);
        Battle battle = battleOf(fastPlayer, fragileEnemy);

        List<TurnResult> results = battle.executeRound(attack(100), attack(30));

        assertAll(
                () -> assertEquals(1, results.size(), "doveva agire solo il piu' veloce"),
                () -> assertSame(fastPlayer, results.get(0).actor()),
                () -> assertTrue(fragileEnemy.isDefeated()),
                () -> assertEquals(100, fastPlayer.getCurrentHp(), "il nemico sconfitto non doveva colpire")
        );
    }

    /**
     * Con squadre di piu' membri la battaglia non deve fermarsi alla caduta
     * del primo: il compagno successivo scende in campo e si continua.
     */
    @Test
    void sendsInTheNextCompanionAfterTheActiveFighterFalls() {
        Fighter hero = fighter("Eroe", 500, 200, 10, 99);
        Fighter firstEnemy = fighter("Guardia", 5, 10, 1, 1);
        Fighter secondEnemy = fighter("Capitano", 200, 10, 1, 1);
        Team enemies = new Team(List.of(firstEnemy, secondEnemy));
        Battle battle = new Battle(new Team(List.of(hero)), enemies, turnOrder);

        battle.executeRound(attack(100), attack(10));

        assertAll(
                () -> assertTrue(firstEnemy.isDefeated()),
                () -> assertSame(secondEnemy, enemies.getActiveFighter()),
                () -> assertFalse(battle.isOver(), "resta un nemico in piedi")
        );
    }

    @Test
    void theSecondCompanionCanThenFightTheFollowingRound() {
        Fighter hero = fighter("Eroe", 500, 200, 10, 99);
        Fighter firstEnemy = fighter("Guardia", 5, 10, 1, 1);
        Fighter secondEnemy = fighter("Capitano", 200, 60, 50, 1);
        Team enemies = new Team(List.of(firstEnemy, secondEnemy));
        Battle battle = new Battle(new Team(List.of(hero)), enemies, turnOrder);
        battle.executeRound(attack(100), attack(10));

        List<TurnResult> second = battle.executeRound(attack(10), attack(60));

        assertAll(
                () -> assertEquals(2, second.size()),
                () -> assertSame(secondEnemy, second.get(1).actor()),
                () -> assertTrue(hero.getCurrentHp() < 500, "il capitano doveva colpire l'eroe")
        );
    }

    @Test
    void isNotOverWhileBothTeamsHaveFightersStanding() {
        Battle battle = battleOf(fighter("Eroe", 100, 10, 50, 50), fighter("Nemico", 100, 10, 50, 50));

        assertAll(
                () -> assertFalse(battle.isOver()),
                () -> assertTrue(battle.getWinner().isEmpty())
        );
    }

    @Test
    void endsWhenATeamIsCompletelyDefeated() {
        Fighter player = fighter("Eroe", 100, 200, 10, 99);
        Fighter enemy = fighter("Nemico", 5, 10, 1, 1);
        Battle battle = battleOf(player, enemy);

        battle.executeRound(attack(100), attack(10));

        assertAll(
                () -> assertTrue(battle.isOver()),
                () -> assertTrue(battle.getWinner().isPresent()),
                () -> assertSame(battle.getPlayerTeam(), battle.getWinner().orElseThrow())
        );
    }

    @Test
    void refusesToContinueOnceTheBattleIsOver() {
        Fighter player = fighter("Eroe", 100, 200, 10, 99);
        Fighter enemy = fighter("Nemico", 5, 10, 1, 1);
        Battle battle = battleOf(player, enemy);
        battle.executeRound(attack(100), attack(10));

        assertThrows(IllegalStateException.class, () -> battle.executeRound(attack(100), attack(10)));
    }

    @Test
    void countsTheRoundsPlayed() {
        Battle battle = battleOf(fighter("Eroe", 500, 10, 50, 50), fighter("Nemico", 500, 10, 50, 40));

        assertEquals(0, battle.getRoundNumber());
        battle.executeRound(attack(10), attack(10));
        assertEquals(1, battle.getRoundNumber());
        battle.executeRound(attack(10), attack(10));
        assertEquals(2, battle.getRoundNumber());
    }

    @Test
    void returnsAReadOnlyRoundReport() {
        Battle battle = battleOf(fighter("Eroe", 500, 10, 50, 50), fighter("Nemico", 500, 10, 50, 40));

        List<TurnResult> results = battle.executeRound(attack(10), attack(10));

        assertThrows(UnsupportedOperationException.class, () -> results.remove(0));
    }

    @Test
    void rejectsAnInvalidConstruction() {
        Team team = new Team(List.of(fighter("Eroe", 100, 10, 10, 10)));
        Team other = new Team(List.of(fighter("Nemico", 100, 10, 10, 10)));

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new Battle(null, other, turnOrder)),
                () -> assertThrows(NullPointerException.class, () -> new Battle(team, null, turnOrder)),
                () -> assertThrows(NullPointerException.class, () -> new Battle(team, other, null))
        );
    }

    @Test
    void rejectsMissingAbilities() {
        Battle battle = battleOf(fighter("Eroe", 100, 10, 10, 50), fighter("Nemico", 100, 10, 10, 40));

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> battle.executeRound(null, attack(10))),
                () -> assertThrows(NullPointerException.class, () -> battle.executeRound(attack(10), null))
        );
    }
}
