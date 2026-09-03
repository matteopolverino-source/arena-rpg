package it.unicam.cs.mpgc.rpg125949.domain.ai;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Avversario del primo livello di difficolta': sceglie a caso fra le abilita'
 * che ha a disposizione, senza valutarle.
 * <p>
 * La sorgente casuale viene ricevuta dall'esterno anziche' essere creata qui
 * dentro: fornendo un generatore inizializzato con un seme noto, le partite
 * diventano riproducibili e il comportamento verificabile con dei test.
 */
public class RandomAI extends AbstractEnemyAI {

    private final Random random;

    /**
     * Crea una strategia con sorgente casuale non prevedibile.
     */
    public RandomAI() {
        this(new Random());
    }

    /**
     * @param random sorgente di casualita' da usare; non nulla
     * @throws NullPointerException se {@code random} e' nullo
     */
    public RandomAI(Random random) {
        this.random = Objects.requireNonNull(random, "random non puo' essere null");
    }

    @Override
    protected Ability select(Fighter self, Fighter opponent, List<Ability> available) {
        return available.get(random.nextInt(available.size()));
    }
}
