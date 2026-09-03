package it.unicam.cs.mpgc.rpg125949.domain.ai;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;
import it.unicam.cs.mpgc.rpg125949.domain.combat.TargetType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Base comune alle strategie avversarie.
 * <p>
 * Fissa la parte invariante della scelta - la validazione degli argomenti -
 * e delega alle sottoclassi il solo criterio con cui decidere. In questo modo
 * nessuna strategia puo' dimenticare un controllo, e chi ne scrive una nuova
 * si concentra unicamente sul comportamento che la caratterizza.
 */
public abstract class AbstractEnemyAI implements EnemyAI {

    @Override
    public final Ability chooseAbility(Fighter self, Fighter opponent, List<Ability> available) {
        Objects.requireNonNull(self, "self non puo' essere null");
        Objects.requireNonNull(opponent, "opponent non puo' essere null");
        Objects.requireNonNull(available, "available non puo' essere null");
        if (available.isEmpty()) {
            throw new IllegalArgumentException("occorre almeno un'abilita' fra cui scegliere");
        }
        return select(self, opponent, available);
    }

    /**
     * Applica il criterio di scelta proprio della strategia.
     *
     * @param self      combattente controllato dalla strategia
     * @param opponent  combattente avversario
     * @param available abilita' disponibili, garantite non vuote
     * @return l'abilita' scelta, fra quelle disponibili
     */
    protected abstract Ability select(Fighter self, Fighter opponent, List<Ability> available);

    /**
     * Individua l'abilita' offensiva che produrrebbe l'effetto maggiore.
     * <p>
     * Il confronto avviene tramite {@link Ability#estimateEffect}, quindi non
     * richiede di sapere di che tipo di abilita' si tratti: una nuova abilita'
     * offensiva entra automaticamente nel confronto.
     *
     * @return l'attacco piu' efficace, o la prima abilita' disponibile se la
     *         strategia non dispone di alcun attacco
     */
    protected static Ability mostDamagingAmong(Fighter self, Fighter opponent, List<Ability> available) {
        return available.stream()
                .filter(ability -> ability.getTargetType() == TargetType.OPPONENT)
                .max(Comparator.comparingInt(ability -> ability.estimateEffect(self, opponent)))
                .orElseGet(() -> available.get(0));
    }
}
