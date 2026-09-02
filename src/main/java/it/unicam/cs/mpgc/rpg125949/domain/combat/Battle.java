package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Una battaglia fra due squadre, giocata a turni.
 * <p>
 * Il compito di questa classe e' <em>coordinare</em>, non calcolare: chiede
 * l'ordine di azione a un {@link TurnOrder}, lascia che sia ogni
 * {@link Ability} a produrre il proprio effetto e si limita a stabilire chi
 * agisce, su chi, e quando lo scontro e' concluso. Nessuna formula di danno e
 * nessuna regola di bilanciamento risiede qui: sono responsabilita' di altre
 * classi, sostituibili senza toccare il motore.
 * <p>
 * Un turno si svolge cosi': entrambi gli schieramenti dichiarano l'abilita'
 * che intendono usare, l'ordine di azione viene calcolato, e chi risulta
 * sconfitto prima del proprio turno non agisce.
 */
public class Battle {

    private final Team playerTeam;
    private final Team enemyTeam;
    private final TurnOrder turnOrder;

    private int roundNumber;

    /**
     * @param playerTeam squadra del giocatore; non nulla
     * @param enemyTeam  squadra avversaria; non nulla
     * @param turnOrder  criterio con cui stabilire chi agisce per primo; non nullo
     * @throws NullPointerException se un argomento e' nullo
     */
    public Battle(Team playerTeam, Team enemyTeam, TurnOrder turnOrder) {
        this.playerTeam = Objects.requireNonNull(playerTeam, "playerTeam non puo' essere null");
        this.enemyTeam = Objects.requireNonNull(enemyTeam, "enemyTeam non puo' essere null");
        this.turnOrder = Objects.requireNonNull(turnOrder, "turnOrder non puo' essere null");
    }

    public Team getPlayerTeam() {
        return playerTeam;
    }

    public Team getEnemyTeam() {
        return enemyTeam;
    }

    /**
     * @return quanti turni sono stati giocati finora
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * @return {@code true} se una delle due squadre e' stata sconfitta
     */
    public boolean isOver() {
        return playerTeam.isDefeated() || enemyTeam.isDefeated();
    }

    /**
     * @return la squadra vincitrice, se la battaglia e' conclusa con un
     *         vincitore; vuoto se e' ancora in corso o se entrambe le squadre
     *         sono state sconfitte
     */
    public Optional<Team> getWinner() {
        if (enemyTeam.isDefeated() && !playerTeam.isDefeated()) {
            return Optional.of(playerTeam);
        }
        if (playerTeam.isDefeated() && !enemyTeam.isDefeated()) {
            return Optional.of(enemyTeam);
        }
        return Optional.empty();
    }

    /**
     * Gioca un turno completo.
     *
     * @param playerAbility abilita' scelta dal giocatore; non nulla
     * @param enemyAbility  abilita' scelta dall'avversario; non nulla
     * @return il resoconto in sola lettura delle azioni effettivamente
     *         eseguite, nell'ordine in cui sono avvenute; chi e' stato
     *         sconfitto prima del proprio turno non vi compare
     * @throws NullPointerException  se una delle due abilita' e' nulla
     * @throws IllegalStateException se la battaglia e' gia' conclusa
     */
    public List<TurnResult> executeRound(Ability playerAbility, Ability enemyAbility) {
        Objects.requireNonNull(playerAbility, "playerAbility non puo' essere null");
        Objects.requireNonNull(enemyAbility, "enemyAbility non puo' essere null");
        if (isOver()) {
            throw new IllegalStateException("la battaglia e' gia' conclusa");
        }

        Fighter playerFighter = playerTeam.getActiveFighter();
        Fighter enemyFighter = enemyTeam.getActiveFighter();

        List<TurnResult> report = new ArrayList<>();
        for (Fighter actor : turnOrder.sort(List.of(playerFighter, enemyFighter))) {
            // Chi e' caduto sotto i colpi di un avversario piu' rapido perde il turno.
            if (actor.isDefeated()) {
                continue;
            }
            boolean isPlayerSide = actor == playerFighter;
            Ability ability = isPlayerSide ? playerAbility : enemyAbility;
            Fighter target = resolveTarget(actor, ability, isPlayerSide ? enemyFighter : playerFighter);

            report.add(new TurnResult(actor, ability, target, ability.applyTo(actor, target)));
        }

        roundNumber++;
        return List.copyOf(report);
    }

    /**
     * Individua il bersaglio di un'abilita' chiedendolo all'abilita' stessa,
     * senza doverne conoscere il tipo concreto.
     */
    private Fighter resolveTarget(Fighter actor, Ability ability, Fighter opponent) {
        return ability.getTargetType() == TargetType.SELF ? actor : opponent;
    }
}
