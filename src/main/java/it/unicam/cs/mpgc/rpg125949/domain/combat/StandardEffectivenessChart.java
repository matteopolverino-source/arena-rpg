package it.unicam.cs.mpgc.rpg125949.domain.combat;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Tabella di efficacia predefinita, costruita su un ciclo di vantaggi:
 * il fuoco brucia la natura, la natura assorbe l'acqua, l'acqua spegne il
 * fuoco. L'elemento {@link Element#NEUTRAL} non partecipa al ciclo e non
 * gode ne' subisce alcun vantaggio.
 * <p>
 * Le relazioni sono memorizzate in una mappa: la ricerca del moltiplicatore
 * avviene in tempo costante e non dipende dal numero di elementi. Aggiungere
 * un elemento al gioco significa aggiungere una voce a {@code STRONG_AGAINST},
 * senza toccare il motore di combattimento.
 */
public class StandardEffectivenessChart implements EffectivenessChart {

    private static final double SUPER_EFFECTIVE = 2.0;
    private static final double RESISTED = 0.5;
    private static final double NORMAL = 1.0;

    /** Associa a ogni elemento quello contro cui e' particolarmente efficace. */
    private static final Map<Element, Element> STRONG_AGAINST = buildCycle();

    private static Map<Element, Element> buildCycle() {
        Map<Element, Element> cycle = new EnumMap<>(Element.class);
        cycle.put(Element.FIRE, Element.NATURE);
        cycle.put(Element.NATURE, Element.WATER);
        cycle.put(Element.WATER, Element.FIRE);
        return Map.copyOf(cycle);
    }

    @Override
    public double multiplier(Element attacker, Element defender) {
        Objects.requireNonNull(attacker, "attacker non puo' essere null");
        Objects.requireNonNull(defender, "defender non puo' essere null");

        if (STRONG_AGAINST.get(attacker) == defender) {
            return SUPER_EFFECTIVE;
        }
        if (STRONG_AGAINST.get(defender) == attacker) {
            return RESISTED;
        }
        return NORMAL;
    }
}
