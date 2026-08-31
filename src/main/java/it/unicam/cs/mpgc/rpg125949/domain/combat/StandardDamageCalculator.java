package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;

import java.util.Objects;

/**
 * Formula di danno predefinita.
 * <p>
 * Il danno cresce con l'attacco di chi colpisce e con la potenza dell'abilita',
 * e cala con la difesa di chi subisce; il risultato viene poi moltiplicato per
 * l'efficacia elementare fornita dalla {@link EffectivenessChart}.
 * <p>
 * Il danno di base e' calcolato per intero <em>prima</em> di applicare
 * l'efficacia: cosi' un attacco superefficace vale esattamente il doppio di uno
 * neutro, senza scarti dovuti agli arrotondamenti.
 * <p>
 * Un attacco infligge sempre almeno un punto di danno: senza questa regola un
 * difensore molto corazzato diventerebbe invulnerabile e la battaglia non
 * potrebbe concludersi.
 */
public class StandardDamageCalculator implements DamageCalculator {

    /** Peso della difesa nella formula: piu' alto, meno incidono gli attacchi. */
    private static final int DEFENSE_SCALE = 2;

    private static final int MINIMUM_DAMAGE = 1;

    private final EffectivenessChart chart;

    /**
     * @param chart tabella di efficacia da consultare; non nulla
     * @throws NullPointerException se {@code chart} e' nulla
     */
    public StandardDamageCalculator(EffectivenessChart chart) {
        this.chart = Objects.requireNonNull(chart, "chart non puo' essere null");
    }

    @Override
    public int computeDamage(Fighter attacker, Fighter defender, int power, Element element) {
        Objects.requireNonNull(attacker, "attacker non puo' essere null");
        Objects.requireNonNull(defender, "defender non puo' essere null");
        Objects.requireNonNull(element, "element non puo' essere null");
        if (power <= 0) {
            throw new IllegalArgumentException("power deve essere positiva, ricevuta: " + power);
        }

        // I calcoli usano long per non incorrere in overflow con statistiche elevate.
        // Il divisore non scende mai sotto 1: le statistiche ammettono difesa nulla.
        long divisor = Math.max(1L, (long) defender.getStats().defense() * DEFENSE_SCALE);
        long baseDamage = (long) attacker.getStats().attack() * power / divisor;

        double effectiveness = chart.multiplier(element, defender.getElement());
        long finalDamage = (long) (baseDamage * effectiveness);

        return (int) Math.clamp(finalDamage, MINIMUM_DAMAGE, Integer.MAX_VALUE);
    }
}
