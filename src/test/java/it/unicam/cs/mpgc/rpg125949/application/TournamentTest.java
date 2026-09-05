package it.unicam.cs.mpgc.rpg125949.application;

import it.unicam.cs.mpgc.rpg125949.domain.ai.AggressiveAI;
import it.unicam.cs.mpgc.rpg125949.domain.ai.RandomAI;
import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Stats;
import it.unicam.cs.mpgc.rpg125949.domain.character.Team;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Battle;
import it.unicam.cs.mpgc.rpg125949.domain.combat.DamageAbility;
import it.unicam.cs.mpgc.rpg125949.domain.combat.DamageCalculator;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Element;
import it.unicam.cs.mpgc.rpg125949.domain.combat.SpeedTurnOrder;
import it.unicam.cs.mpgc.rpg125949.domain.combat.StandardDamageCalculator;
import it.unicam.cs.mpgc.rpg125949.domain.combat.StandardEffectivenessChart;
import it.unicam.cs.mpgc.rpg125949.domain.combat.TurnOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TournamentTest {

    private DamageCalculator calculator;
    private TurnOrder turnOrder;
    private Ability crushingBlow;
    private Ability featherTouch;

    @BeforeEach
    void setUp() {
        calculator = new StandardDamageCalculator(new StandardEffectivenessChart());
        turnOrder = new SpeedTurnOrder();
        crushingBlow = new DamageAbility("Colpo devastante", Element.NEUTRAL, 200, calculator);
        featherTouch = new DamageAbility("Sfioramento", Element.NEUTRAL, 1, calculator);
    }

    /** Un campione che abbatte chiunque in un turno solo. */
    private Fighter champion(String name) {
        return new Fighter(name, Element.NEUTRAL, new Stats(1000, 200, 100, 99), List.of(crushingBlow));
    }

    /** Una vittima sacrificale, incapace di fare male. */
    private Fighter weakling(String name) {
        return new Fighter(name, Element.NEUTRAL, new Stats(1, 1, 1, 1), List.of(featherTouch));
    }

    private TournamentStage stage(String name, Fighter enemy) {
        return new TournamentStage(name, new Team(List.of(enemy)), new AggressiveAI());
    }

    private Tournament tournamentWith(Team playerTeam, TournamentStage... stages) {
        return new Tournament(playerTeam, List.of(stages), turnOrder);
    }

    /** Gioca il turno corrente fino alla conclusione dello scontro. */
    private void fightToTheEnd(Tournament tournament) {
        Battle battle = tournament.getCurrentBattle();
        while (!battle.isOver()) {
            battle.executeRound(crushingBlow, featherTouch);
        }
        tournament.settleCurrentBattle();
    }

    @Test
    void startsAtTheFirstStage() {
        Tournament tournament = tournamentWith(new Team(List.of(champion("Eroe"))),
                stage("Ottavi", weakling("Recluta")), stage("Finale", weakling("Campione")));

        assertAll(
                () -> assertEquals(1, tournament.getCurrentStageNumber()),
                () -> assertEquals(2, tournament.getTotalStages()),
                () -> assertEquals("Ottavi", tournament.getCurrentStage().name()),
                () -> assertFalse(tournament.isOver())
        );
    }

    @Test
    void offersABattleAgainstTheCurrentOpponent() {
        TournamentStage first = stage("Ottavi", weakling("Recluta"));
        Team playerTeam = new Team(List.of(champion("Eroe")));
        Tournament tournament = new Tournament(playerTeam, List.of(first), turnOrder);

        Battle battle = tournament.getCurrentBattle();

        assertAll(
                () -> assertSame(playerTeam, battle.getPlayerTeam()),
                () -> assertSame(first.enemies(), battle.getEnemyTeam()),
                () -> assertSame(battle, tournament.getCurrentBattle(), "deve essere sempre la stessa battaglia")
        );
    }

    @Test
    void advancesToTheNextStageAfterAVictory() {
        Tournament tournament = tournamentWith(new Team(List.of(champion("Eroe"))),
                stage("Ottavi", weakling("Recluta")), stage("Finale", weakling("Campione")));

        fightToTheEnd(tournament);

        assertAll(
                () -> assertEquals(2, tournament.getCurrentStageNumber()),
                () -> assertEquals("Finale", tournament.getCurrentStage().name()),
                () -> assertFalse(tournament.isOver())
        );
    }

    /**
     * Fra un incontro e l'altro la squadra torna in forze, cosi' che ogni
     * sfida venga affrontata a parita' di condizioni.
     */
    @Test
    void restoresThePlayerTeamBetweenStages() {
        Fighter hero = champion("Eroe");
        Tournament tournament = tournamentWith(new Team(List.of(hero)),
                stage("Ottavi", weakling("Recluta")), stage("Finale", weakling("Campione")));
        hero.takeDamage(900);

        fightToTheEnd(tournament);

        assertEquals(1000, hero.getCurrentHp());
    }

    @Test
    void isWonOnceTheLastStageIsCleared() {
        Tournament tournament = tournamentWith(new Team(List.of(champion("Eroe"))),
                stage("Finale", weakling("Campione")));

        fightToTheEnd(tournament);

        assertAll(
                () -> assertTrue(tournament.isOver()),
                () -> assertTrue(tournament.isWon()),
                () -> assertFalse(tournament.isLost())
        );
    }

    @Test
    void isLostWhenThePlayerTeamFalls() {
        Tournament tournament = tournamentWith(new Team(List.of(weakling("Sfortunato"))),
                stage("Finale", champion("Mostro")));
        Battle battle = tournament.getCurrentBattle();
        while (!battle.isOver()) {
            battle.executeRound(featherTouch, crushingBlow);
        }

        tournament.settleCurrentBattle();

        assertAll(
                () -> assertTrue(tournament.isOver()),
                () -> assertTrue(tournament.isLost()),
                () -> assertFalse(tournament.isWon())
        );
    }

    @Test
    void refusesToSettleABattleStillInProgress() {
        Tournament tournament = tournamentWith(new Team(List.of(champion("Eroe"))),
                stage("Finale", champion("Rivale")));
        tournament.getCurrentBattle();

        assertThrows(IllegalStateException.class, tournament::settleCurrentBattle);
    }

    @Test
    void refusesToContinueOnceTheTournamentIsOver() {
        Tournament tournament = tournamentWith(new Team(List.of(champion("Eroe"))),
                stage("Finale", weakling("Campione")));
        fightToTheEnd(tournament);

        assertAll(
                () -> assertThrows(IllegalStateException.class, tournament::getCurrentBattle),
                () -> assertThrows(IllegalStateException.class, tournament::getCurrentStage)
        );
    }

    /**
     * Riprendere una partita salvata significa ricominciare da una tappa
     * intermedia conservando la numerazione originale, altrimenti il
     * giocatore vedrebbe azzerarsi il proprio avanzamento.
     */
    @Test
    void canBeResumedFromALaterStage() {
        Tournament tournament = new Tournament(new Team(List.of(champion("Eroe"))),
                List.of(stage("Ottavi", weakling("A")), stage("Semifinale", weakling("B")),
                        stage("Finale", weakling("C"))),
                turnOrder, 2);

        assertAll(
                () -> assertEquals(3, tournament.getCurrentStageNumber()),
                () -> assertEquals(3, tournament.getTotalStages()),
                () -> assertEquals("Finale", tournament.getCurrentStage().name())
        );
    }

    @Test
    void rejectsAStartingStageOutsideTheTournament() {
        Team playerTeam = new Team(List.of(champion("Eroe")));
        List<TournamentStage> stages = List.of(stage("Finale", weakling("Campione")));

        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Tournament(playerTeam, stages, turnOrder, -1)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Tournament(playerTeam, stages, turnOrder, stages.size()))
        );
    }

    @Test
    void rejectsAnInvalidConstruction() {
        Team playerTeam = new Team(List.of(champion("Eroe")));
        List<TournamentStage> stages = List.of(stage("Finale", weakling("Campione")));

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new Tournament(null, stages, turnOrder)),
                () -> assertThrows(NullPointerException.class, () -> new Tournament(playerTeam, null, turnOrder)),
                () -> assertThrows(NullPointerException.class, () -> new Tournament(playerTeam, stages, null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Tournament(playerTeam, List.of(), turnOrder))
        );
    }

    @Test
    void rejectsAnInvalidStage() {
        Team enemies = new Team(List.of(weakling("Recluta")));

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new TournamentStage(null, enemies, new RandomAI(new Random(1)))),
                () -> assertThrows(NullPointerException.class,
                        () -> new TournamentStage("Ottavi", null, new RandomAI(new Random(1)))),
                () -> assertThrows(NullPointerException.class,
                        () -> new TournamentStage("Ottavi", enemies, null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TournamentStage("  ", enemies, new RandomAI(new Random(1))))
        );
    }
}
