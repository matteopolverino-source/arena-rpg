package it.unicam.cs.mpgc.rpg125949.application;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultGameContentTest {

    private GameContent content;

    @BeforeEach
    void setUp() {
        content = new DefaultGameContent();
    }

    @Test
    void providesAPlayerTeamReadyToFight() {
        Team team = content.createPlayerTeam();

        assertAll(
                () -> assertFalse(team.getFighters().isEmpty()),
                () -> assertFalse(team.isDefeated()),
                () -> team.getFighters().forEach(fighter ->
                        assertFalse(fighter.getAbilities().isEmpty(),
                                fighter.getName() + " non ha alcuna abilita'"))
        );
    }

    @Test
    void providesAnOpponentForEveryStage() {
        List<TournamentStage> stages = content.createStages();

        assertAll(
                () -> assertTrue(stages.size() >= 3, "il torneo deve avere almeno tre tappe"),
                () -> stages.forEach(stage -> assertFalse(stage.enemies().isDefeated(),
                        stage.name() + " schiera avversari gia' sconfitti"))
        );
    }

    @Test
    void givesEveryStageItsOwnOpponents() {
        List<TournamentStage> stages = content.createStages();

        Set<Team> distinct = new HashSet<>();
        stages.forEach(stage -> distinct.add(stage.enemies()));

        assertEquals(stages.size(), distinct.size(), "due tappe condividono la stessa squadra");
    }

    /**
     * Iniziare una nuova partita non deve ereditare le ferite di quella
     * precedente: ogni richiesta produce oggetti nuovi e intatti.
     */
    @Test
    void buildsFreshObjectsOnEveryRequest() {
        Team first = content.createPlayerTeam();
        first.getFighters().forEach(fighter -> fighter.takeDamage(fighter.getStats().maxHp()));

        Team second = content.createPlayerTeam();

        assertAll(
                () -> assertNotSame(first, second),
                () -> assertTrue(first.isDefeated()),
                () -> assertFalse(second.isDefeated(), "la nuova squadra ha ereditato le ferite della precedente")
        );
    }

    @Test
    void buildsFreshOpponentsOnEveryRequest() {
        List<TournamentStage> first = content.createStages();
        first.forEach(stage -> stage.enemies().getFighters()
                .forEach(fighter -> fighter.takeDamage(fighter.getStats().maxHp())));

        List<TournamentStage> second = content.createStages();

        assertAll(
                () -> assertTrue(first.get(0).enemies().isDefeated()),
                () -> assertFalse(second.get(0).enemies().isDefeated(),
                        "i nuovi avversari hanno ereditato le ferite dei precedenti")
        );
    }

    @Test
    void givesEveryFighterAUniqueName() {
        List<Fighter> fighters = content.createPlayerTeam().getFighters();

        Set<String> names = new HashSet<>();
        fighters.forEach(fighter -> names.add(fighter.getName()));

        assertEquals(fighters.size(), names.size(),
                "i nomi devono essere distinti, il salvataggio li usa come chiave");
    }
}
