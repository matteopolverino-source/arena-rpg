package it.unicam.cs.mpgc.rpg125949.domain.ai;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;

import java.util.List;

/**
 * Avversario del secondo livello di difficolta': sferra sempre il colpo piu'
 * potente di cui dispone e non si cura mai, nemmeno in fin di vita.
 * <p>
 * E' prevedibile e proprio per questo battibile: il giocatore che intuisce il
 * suo schema puo' sfruttarlo. La differenza con {@link TacticalAI} sta tutta
 * nell'assenza di istinto di sopravvivenza.
 */
public class AggressiveAI extends AbstractEnemyAI {

    @Override
    protected Ability select(Fighter self, Fighter opponent, List<Ability> available) {
        return mostDamagingAmong(self, opponent, available);
    }
}
