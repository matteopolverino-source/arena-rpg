package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;

import java.util.Objects;

/**
 * Abilita' che ripristina punti vita a un bersaglio.
 * <p>
 * Dal punto di vista del motore di battaglia e' indistinguibile da
 * un'abilita' offensiva: entrambe sono {@link Ability} e vengono usate allo
 * stesso modo. E' questa intercambiabilita' che permette di aggiungere nuovi
 * tipi di abilita' senza modificare chi le usa.
 */
public class HealAbility extends AbstractAbility {

    private final int amount;

    /**
     * @param name    nome dell'abilita'; non nullo e non composto di soli spazi
     * @param element elemento dell'abilita'; non nullo
     * @param amount  punti vita ripristinati; deve essere positivo
     * @throws NullPointerException     se un argomento obbligatorio e' nullo
     * @throws IllegalArgumentException se il nome e' vuoto o l'ammontare non e' positivo
     */
    public HealAbility(String name, Element element, int amount) {
        super(name, element);
        this.amount = requirePositive(amount, "amount");
    }

    /**
     * {@inheritDoc}
     *
     * @return sempre {@link TargetType#SELF}: una cura ristora chi la lancia
     */
    @Override
    public TargetType getTargetType() {
        return TargetType.SELF;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Il valore restituito e' la differenza fra i punti vita del bersaglio
     * dopo e prima della cura: se il bersaglio era quasi sano, l'abilita'
     * riporta solo i punti effettivamente ripristinati.
     */
    @Override
    public int applyTo(Fighter user, Fighter target) {
        Objects.requireNonNull(user, "user non puo' essere null");
        Objects.requireNonNull(target, "target non puo' essere null");

        int healthBefore = target.getCurrentHp();
        target.heal(amount);
        return target.getCurrentHp() - healthBefore;
    }
}
