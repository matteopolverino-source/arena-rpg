package it.unicam.cs.mpgc.rpg125949.application;

import it.unicam.cs.mpgc.rpg125949.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;
import it.unicam.cs.mpgc.rpg125949.domain.combat.HealAbility;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Element;
import it.unicam.cs.mpgc.rpg125949.domain.combat.SpeedTurnOrder;
import it.unicam.cs.mpgc.rpg125949.domain.combat.TurnResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameServiceTest {

    /**
     * Archivio in memoria: e' un'implementazione vera della porta, non un
     * simulacro, e permette di verificare salvataggio e ripresa senza
     * toccare il disco.
     */
    private static final class InMemoryGameRepository implements GameRepository {
        private GameProgress stored;

        @Override
        public void save(GameProgress progress) {
            this.stored = progress;
        }

        @Override
        public Optional<GameProgress> load() {
            return Optional.ofNullable(stored);
        }

        @Override
        public void clear() {
            this.stored = null;
        }
    }

    private InMemoryGameRepository repository;
    private GameService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGameRepository();
        service = new GameService(new DefaultGameContent(), repository, new SpeedTurnOrder());
    }

    private Ability firstAbilityOfActivePlayerFighter() {
        return service.getTournament().getCurrentBattle()
                .getPlayerTeam().getActiveFighter().getAbilities().get(0);
    }

    @Test
    void startsANewGameAtTheFirstStage() {
        Tournament tournament = service.startNewGame();

        assertAll(
                () -> assertEquals(1, tournament.getCurrentStageNumber()),
                () -> assertFalse(tournament.isOver())
        );
    }

    @Test
    void refusesToActWithoutAGameInProgress() {
        assertAll(
                () -> assertThrows(IllegalStateException.class, () -> service.getTournament()),
                () -> assertThrows(IllegalStateException.class,
                        () -> service.playRound(new HealAbility("X", Element.NEUTRAL, 1)))
        );
    }

    /**
     * Il giocatore dichiara solo la propria mossa: quella dell'avversario la
     * decide la strategia della tappa, non l'interfaccia grafica.
     */
    @Test
    void playsARoundChoosingTheOpponentMoveOnItsOwn() {
        service.startNewGame();

        List<TurnResult> report = service.playRound(firstAbilityOfActivePlayerFighter());

        assertAll(
                () -> assertFalse(report.isEmpty()),
                () -> assertTrue(report.size() <= 2)
        );
    }

    @Test
    void rejectsAnAbilityTheActiveFighterDoesNotKnow() {
        service.startNewGame();
        Ability foreign = new HealAbility("Mossa altrui", Element.NEUTRAL, 10);

        assertThrows(IllegalArgumentException.class, () -> service.playRound(foreign));
    }

    @Test
    void advancesTheTournamentWhenTheBattleEnds() {
        Tournament tournament = service.startNewGame();
        Ability strike = firstAbilityOfActivePlayerFighter();

        int guard = 0;
        while (tournament.getCurrentStageNumber() == 1 && !tournament.isOver() && guard++ < 50) {
            service.playRound(strike);
        }

        assertTrue(tournament.getCurrentStageNumber() > 1 || tournament.isOver(),
                "la prima tappa doveva concludersi");
    }

    @Test
    void reportsThatThereIsNothingToResume() {
        assertTrue(service.resumeSavedGame().isEmpty());
    }

    @Test
    void savesTheProgressAndRestoresIt() {
        Tournament original = service.startNewGame();
        Fighter wounded = original.getPlayerTeam().getFighters().get(0);
        wounded.takeDamage(40);
        int expectedHealth = wounded.getCurrentHp();
        service.saveProgress();

        GameService other = new GameService(new DefaultGameContent(), repository, new SpeedTurnOrder());
        Tournament resumed = other.resumeSavedGame().orElseThrow();

        assertAll(
                () -> assertNotSame(original, resumed),
                () -> assertEquals(expectedHealth, resumed.getPlayerTeam().getFighters().get(0).getCurrentHp()),
                () -> assertEquals(original.getCurrentStageNumber(), resumed.getCurrentStageNumber())
        );
    }

    @Test
    void forgetsTheProgressWhenAskedTo() {
        service.startNewGame();
        service.saveProgress();

        service.discardSavedGame();

        assertTrue(repository.load().isEmpty());
    }

    @Test
    void rejectsAnInvalidConstruction() {
        GameContent content = new DefaultGameContent();

        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> new GameService(null, repository, new SpeedTurnOrder())),
                () -> assertThrows(NullPointerException.class,
                        () -> new GameService(content, null, new SpeedTurnOrder())),
                () -> assertThrows(NullPointerException.class,
                        () -> new GameService(content, repository, null))
        );
    }
}
