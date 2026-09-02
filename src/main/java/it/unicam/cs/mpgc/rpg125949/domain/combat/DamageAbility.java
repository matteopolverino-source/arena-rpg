package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;

import java.util.Objects;

/**
 * Abilita' che infligge danno a un bersaglio.
 * <p>
 * Non calcola il danno da se': lo chiede a un {@link DamageCalculator} ricevuto
 * alla costruzione. In questo modo la formula di bilanciamento puo' cambiare
 * senza che le abilita' vengano modificate.
 */
public class DamageAbility extends AbstractAbility {

    private final int power;
    private final DamageCalculator calculator;

    /**
     * @param name       nome dell'abilita'; non nullo e non composto di soli spazi
     * @param element    elemento dell'abilita'; non nullo
     * @param power      potenza dell'abilita'; deve essere positiva
     * @param calculator formula con cui calcolare il danno; non nulla
     * @throws NullPointerException     se un argomento obbligatorio e' nullo
     * @throws IllegalArgumentException se il nome e' vuoto o la potenza non e' positiva
     */
    public DamageAbility(String name, Element element, int power, DamageCalculator calculator) {
        super(name, element);
        this.power = requirePositive(power, "power");
        this.calculator = Objects.requireNonNull(calculator, "calculator non puo' essere null");
    }

    /**
     * {@inheritDoc}
     *
     * @return sempre {@link TargetType#OPPONENT}: un attacco colpisce
     *         l'avversario
     */
    @Override
    public TargetType getTargetType() {
        return TargetType.OPPONENT;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Interroga il {@link DamageCalculator} senza applicare il risultato.
     */
    @Override
    public int estimateEffect(Fighter user, Fighter target) {
        Objects.requireNonNull(user, "user non puo' essere null");
        Objects.requireNonNull(target, "target non puo' essere null");
        return calculator.computeDamage(user, target, power, getElement());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Il valore restituito e' la differenza fra i punti vita del bersaglio
     * prima e dopo il colpo: se il bersaglio aveva meno punti vita del danno
     * calcolato, l'abilita' riporta solo quelli effettivamente tolti.
     */
    @Override
    public int applyTo(Fighter user, Fighter target) {
        Objects.requireNonNull(user, "user non puo' essere null");
        Objects.requireNonNull(target, "target non puo' essere null");

        int healthBefore = target.getCurrentHp();
        target.takeDamage(estimateEffect(user, target));
        return healthBefore - target.getCurrentHp();
    }
}
