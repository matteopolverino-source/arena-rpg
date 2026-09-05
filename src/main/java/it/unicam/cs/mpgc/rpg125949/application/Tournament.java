package it.unicam.cs.mpgc.rpg125949.application;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Team;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Battle;
import it.unicam.cs.mpgc.rpg125949.domain.combat.TurnOrder;

import java.util.List;
import java.util.Objects;

/**
 * Il percorso che il giocatore affronta: una successione di tappe da
 * superare una dopo l'altra.
 * <p>
 * Appartiene al livello applicativo, non al dominio: coordina gli oggetti di
 * dominio per realizzare il caso d'uso "gioca il torneo", senza contenere
 * regole di combattimento. Non conosce ne' JavaFX ne' alcun formato di
 * salvataggio, quindi la stessa logica di avanzamento vale identica per
 * un'interfaccia desktop, web o mobile.
 * <p>
 * Fra una tappa e l'altra la squadra del giocatore viene rimessa in forze:
 * ogni sfida va affrontata a parita' di condizioni, cosi' che a decidere sia
 * la scelta delle mosse e non l'usura accumulata.
 */
public class Tournament {

    private final Team playerTeam;
    private final List<TournamentStage> stages;
    private final TurnOrder turnOrder;

    private int stageIndex;
    private Battle currentBattle;
    private boolean defeated;

    /**
     * @param playerTeam squadra del giocatore; non nulla
     * @param stages     tappe da affrontare, in ordine; non nulle e non vuote
     * @param turnOrder  criterio di ordinamento dei turni; non nullo
     * @throws NullPointerException     se un argomento e' nullo
     * @throws IllegalArgumentException se non viene indicata alcuna tappa
     */
    public Tournament(Team playerTeam, List<TournamentStage> stages, TurnOrder turnOrder) {
        this(playerTeam, stages, turnOrder, 0);
    }

    /**
     * Riprende un torneo a partire da una tappa gia' raggiunta.
     * <p>
     * Le tappe precedenti restano nell'elenco anche se non verranno giocate:
     * servono a conservare la numerazione originale, cosi' che riprendendo una
     * partita il giocatore ritrovi l'avanzamento che aveva e non un torneo
     * apparentemente piu' corto.
     *
     * @param playerTeam        squadra del giocatore; non nulla
     * @param stages            elenco completo delle tappe; non nullo e non vuoto
     * @param turnOrder         criterio di ordinamento dei turni; non nullo
     * @param startingStageIndex tappa da cui ripartire, contata da zero
     * @throws NullPointerException     se un argomento e' nullo
     * @throws IllegalArgumentException se non viene indicata alcuna tappa o se
     *                                  la tappa di partenza non esiste
     */
    public Tournament(Team playerTeam, List<TournamentStage> stages, TurnOrder turnOrder,
                      int startingStageIndex) {
        this.playerTeam = Objects.requireNonNull(playerTeam, "playerTeam non puo' essere null");
        Objects.requireNonNull(stages, "stages non puo' essere null");
        if (stages.isEmpty()) {
            throw new IllegalArgumentException("un torneo deve avere almeno una tappa");
        }
        if (startingStageIndex < 0 || startingStageIndex >= stages.size()) {
            throw new IllegalArgumentException(
                    "la tappa di partenza deve essere compresa fra 0 e " + (stages.size() - 1)
                            + ", ricevuta: " + startingStageIndex);
        }
        this.stages = List.copyOf(stages);
        this.turnOrder = Objects.requireNonNull(turnOrder, "turnOrder non puo' essere null");
        this.stageIndex = startingStageIndex;
    }

    public Team getPlayerTeam() {
        return playerTeam;
    }

    /**
     * @return il numero della tappa in corso, a partire da 1
     */
    public int getCurrentStageNumber() {
        return stageIndex + 1;
    }

    public int getTotalStages() {
        return stages.size();
    }

    /**
     * @return la tappa attualmente da affrontare
     * @throws IllegalStateException se il torneo e' gia' concluso
     */
    public TournamentStage getCurrentStage() {
        requireInProgress();
        return stages.get(stageIndex);
    }

    /**
     * Restituisce la battaglia della tappa in corso, creandola alla prima
     * richiesta e poi conservandola: chiamate ripetute ottengono sempre lo
     * stesso scontro, cosi' che l'interfaccia possa interrogarlo piu' volte
     * senza azzerarne l'andamento.
     *
     * @throws IllegalStateException se il torneo e' gia' concluso
     */
    public Battle getCurrentBattle() {
        requireInProgress();
        if (currentBattle == null) {
            currentBattle = new Battle(playerTeam, stages.get(stageIndex).enemies(), turnOrder);
        }
        return currentBattle;
    }

    /**
     * Registra l'esito della battaglia in corso: in caso di vittoria si passa
     * alla tappa successiva, in caso di sconfitta il torneo termina.
     *
     * @throws IllegalStateException se non c'e' una battaglia in corso o se
     *                               questa non e' ancora conclusa
     */
    public void settleCurrentBattle() {
        requireInProgress();
        if (currentBattle == null || !currentBattle.isOver()) {
            throw new IllegalStateException("la battaglia in corso non e' ancora conclusa");
        }
        if (playerTeam.isDefeated()) {
            defeated = true;
            return;
        }
        stageIndex++;
        currentBattle = null;
        if (!isWon()) {
            restorePlayerTeam();
        }
    }

    /**
     * @return {@code true} se il torneo si e' concluso, per vittoria o sconfitta
     */
    public boolean isOver() {
        return isWon() || isLost();
    }

    /**
     * @return {@code true} se tutte le tappe sono state superate
     */
    public boolean isWon() {
        return !defeated && stageIndex >= stages.size();
    }

    /**
     * @return {@code true} se la squadra del giocatore e' stata sconfitta
     */
    public boolean isLost() {
        return defeated;
    }

    private void restorePlayerTeam() {
        for (Fighter fighter : playerTeam.getFighters()) {
            fighter.heal(fighter.getStats().maxHp());
        }
    }

    private void requireInProgress() {
        if (isOver()) {
            throw new IllegalStateException("il torneo e' gia' concluso");
        }
    }
}
