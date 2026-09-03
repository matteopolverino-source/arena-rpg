package it.unicam.cs.mpgc.rpg125949.domain.ai;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;
import it.unicam.cs.mpgc.rpg125949.domain.combat.TargetType;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Avversario del terzo livello di difficolta': attacca finche' e' in forze,
 * ma si cura quando la situazione diventa critica.
 * <p>
 * Se e' ferito e non dispone di alcuna cura, ripiega sull'attacco piu'
 * efficace invece di restare inerte.
 */
public class TacticalAI extends AbstractEnemyAI {

    /**
     * Frazione di punti vita sotto la quale la strategia antepone la
     * sopravvivenza all'offesa.
     */
    private static final double CRITICAL_HEALTH_RATIO = 0.35;

    @Override
    protected Ability select(Fighter self, Fighter opponent, List<Ability> available) {
        if (isInCriticalCondition(self)) {
            Optional<Ability> restorative = mostRestorativeAmong(self, available);
            if (restorative.isPresent()) {
                return restorative.get();
            }
        }
        return mostDamagingAmong(self, opponent, available);
    }

    private boolean isInCriticalCondition(Fighter self) {
        return self.getCurrentHp() < self.getStats().maxHp() * CRITICAL_HEALTH_RATIO;
    }

    private Optional<Ability> mostRestorativeAmong(Fighter self, List<Ability> available) {
        return available.stream()
                .filter(ability -> ability.getTargetType() == TargetType.SELF)
                .max(Comparator.comparingInt(ability -> ability.estimateEffect(self, self)));
    }
}
