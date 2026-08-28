package it.unicam.cs.mpgc.rpg125949.domain.combat;

/**
 * Tabella che stabilisce quanto un elemento sia efficace contro un altro.
 * <p>
 * E' un'interfaccia per separare la regola di bilanciamento dal calcolo del
 * danno: si puo' introdurre una tabella alternativa senza modificare il motore
 * di combattimento.
 */
public interface EffectivenessChart {

    /**
     * @param attacker elemento dell'attacco
     * @param defender elemento di chi subisce l'attacco
     * @return il fattore per cui moltiplicare il danno, sempre maggiore di zero
     */
    double multiplier(Element attacker, Element defender);
}
