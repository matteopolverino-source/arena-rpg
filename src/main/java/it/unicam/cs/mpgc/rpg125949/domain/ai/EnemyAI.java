package it.unicam.cs.mpgc.rpg125949.domain.ai;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;

import java.util.List;

/**
 * Criterio con cui un avversario controllato dal calcolatore sceglie la
 * propria mossa.
 * <p>
 * Ogni livello di difficolta' del torneo corrisponde a una diversa
 * implementazione: alzare la sfida significa aggiungere una classe, non
 * inserire condizioni dentro il motore di battaglia, che infatti non sa
 * nemmeno che gli avversari siano governati da una strategia.
 */
public interface EnemyAI {

    /**
     * Sceglie quale abilita' usare, senza usarla.
     *
     * @param self      combattente controllato dalla strategia
     * @param opponent  combattente avversario
     * @param available abilita' fra cui scegliere; non vuota
     * @return una delle abilita' disponibili
     * @throws NullPointerException     se un argomento e' nullo
     * @throws IllegalArgumentException se non viene offerta alcuna abilita'
     */
    Ability chooseAbility(Fighter self, Fighter opponent, List<Ability> available);
}
