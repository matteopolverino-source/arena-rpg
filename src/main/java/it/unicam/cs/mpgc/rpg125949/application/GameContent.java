package it.unicam.cs.mpgc.rpg125949.application;

import it.unicam.cs.mpgc.rpg125949.domain.character.Team;

import java.util.List;

/**
 * Fornisce i contenuti di una partita: chi il giocatore schiera e quali sfide
 * dovra' superare.
 * <p>
 * Tenere i contenuti dietro un'interfaccia separa <em>cosa</em> si gioca da
 * <em>come</em> si gioca: una diversa implementazione puo' leggerli da un file
 * o da un servizio remoto, o proporre un torneo piu' lungo, senza che il
 * torneo, il motore di battaglia o l'interfaccia grafica ne risentano.
 * <p>
 * Ogni chiamata deve restituire oggetti nuovi: i combattenti accumulano ferite
 * durante la partita, quindi riusarli farebbe cominciare una nuova partita con
 * i danni della precedente.
 */
public interface GameContent {

    /**
     * @return una squadra del giocatore appena creata, al massimo della salute
     */
    Team createPlayerTeam();

    /**
     * @return le tappe del torneo, in ordine di difficolta' crescente, con
     *         avversari appena creati
     */
    List<TournamentStage> createStages();
}
