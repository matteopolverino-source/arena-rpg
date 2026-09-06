package it.unicam.cs.mpgc.rpg125949.application;

import it.unicam.cs.mpgc.rpg125949.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Team;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Battle;
import it.unicam.cs.mpgc.rpg125949.domain.combat.TurnOrder;
import it.unicam.cs.mpgc.rpg125949.domain.combat.TurnResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Punto di accesso unico ai casi d'uso del gioco: iniziare una partita,
 * riprenderne una salvata, giocare un turno, conservare l'avanzamento.
 * <p>
 * Esiste per tenere sottile l'interfaccia grafica. Quest'ultima si limita a
 * mostrare lo stato e a inoltrare la mossa scelta dal giocatore: non sa come
 * si decide la mossa dell'avversario, quando una battaglia sia conclusa o
 * come si passi alla tappa successiva. Di conseguenza sostituire JavaFX con
 * un'interfaccia web o mobile non richiede di riscrivere alcuna regola,
 * perche' nessuna regola vive nell'interfaccia.
 */
public class GameService {

    private final GameContent content;
    private final GameRepository repository;
    private final TurnOrder turnOrder;

    private Tournament tournament;

    /**
     * @param content    fornitore dei contenuti di gioco; non nullo
     * @param repository archivio dell'avanzamento; non nullo
     * @param turnOrder  criterio di ordinamento dei turni; non nullo
     * @throws NullPointerException se un argomento e' nullo
     */
    public GameService(GameContent content, GameRepository repository, TurnOrder turnOrder) {
        this.content = Objects.requireNonNull(content, "content non puo' essere null");
        this.repository = Objects.requireNonNull(repository, "repository non puo' essere null");
        this.turnOrder = Objects.requireNonNull(turnOrder, "turnOrder non puo' essere null");
    }

    /**
     * Comincia una partita da capo, sostituendo quella eventualmente in corso.
     *
     * @return il torneo appena iniziato
     */
    public Tournament startNewGame() {
        this.tournament = new Tournament(content.createPlayerTeam(), content.createStages(), turnOrder);
        return tournament;
    }

    /**
     * Riprende la partita salvata, se ne esiste una.
     * <p>
     * Statistiche, elementi e abilita' vengono ricostruiti dai contenuti di
     * gioco; dal salvataggio arrivano soltanto la tappa raggiunta e i punti
     * vita, cioe' le sole cose che cambiano nel corso di una partita.
     *
     * @return il torneo ripreso, oppure vuoto se non c'e' nulla da riprendere
     */
    public Optional<Tournament> resumeSavedGame() {
        Optional<GameProgress> saved = repository.load();
        if (saved.isEmpty()) {
            return Optional.empty();
        }
        GameProgress progress = saved.get();
        Team playerTeam = content.createPlayerTeam();
        applyStoredHealth(playerTeam, progress);

        List<TournamentStage> stages = content.createStages();
        int stageIndex = Math.min(progress.stageIndex(), stages.size() - 1);
        this.tournament = new Tournament(playerTeam, stages, turnOrder, stageIndex);
        return Optional.of(tournament);
    }

    /**
     * @return il torneo in corso
     * @throws IllegalStateException se nessuna partita e' stata avviata
     */
    public Tournament getTournament() {
        if (tournament == null) {
            throw new IllegalStateException("nessuna partita in corso");
        }
        return tournament;
    }

    /**
     * Gioca un turno: il giocatore dichiara la propria mossa, quella
     * dell'avversario viene decisa dalla strategia della tappa. Se lo scontro
     * si conclude, l'esito viene registrato e il torneo avanza.
     *
     * @param playerChoice abilita' scelta dal giocatore, fra quelle note al
     *                     combattente in campo
     * @return il resoconto delle azioni del turno
     * @throws NullPointerException     se {@code playerChoice} e' nullo
     * @throws IllegalStateException    se nessuna partita e' in corso
     * @throws IllegalArgumentException se il combattente in campo non conosce
     *                                  l'abilita' indicata
     */
    public List<TurnResult> playRound(Ability playerChoice) {
        Objects.requireNonNull(playerChoice, "playerChoice non puo' essere null");
        Tournament current = getTournament();
        Battle battle = current.getCurrentBattle();

        Fighter playerFighter = battle.getPlayerTeam().getActiveFighter();
        if (!playerFighter.getAbilities().contains(playerChoice)) {
            throw new IllegalArgumentException(
                    playerFighter.getName() + " non conosce l'abilita' " + playerChoice.getName());
        }

        Fighter enemyFighter = battle.getEnemyTeam().getActiveFighter();
        Ability enemyChoice = current.getCurrentStage().ai()
                .chooseAbility(enemyFighter, playerFighter, enemyFighter.getAbilities());

        List<TurnResult> report = battle.executeRound(playerChoice, enemyChoice);
        if (battle.isOver()) {
            current.settleCurrentBattle();
        }
        return report;
    }

    /**
     * Conserva l'avanzamento della partita in corso.
     *
     * @throws IllegalStateException se nessuna partita e' in corso
     */
    public void saveProgress() {
        Tournament current = getTournament();
        Map<String, Integer> health = new HashMap<>();
        for (Fighter fighter : current.getPlayerTeam().getFighters()) {
            health.put(fighter.getName(), fighter.getCurrentHp());
        }
        repository.save(new GameProgress(current.getCurrentStageNumber() - 1, health));
    }

    /**
     * Cancella l'avanzamento conservato. Ripetere l'operazione quando non
     * esiste alcun salvataggio non e' un errore.
     */
    public void discardSavedGame() {
        repository.clear();
    }

    private void applyStoredHealth(Team playerTeam, GameProgress progress) {
        for (Fighter fighter : playerTeam.getFighters()) {
            Integer storedHealth = progress.healthByFighter().get(fighter.getName());
            if (storedHealth != null) {
                // I combattenti nascono al massimo della salute: si tratta di
                // riportarli al valore salvato, non di ferirli ulteriormente.
                fighter.takeDamage(Math.max(0, fighter.getCurrentHp() - storedHealth));
            }
        }
    }
}
