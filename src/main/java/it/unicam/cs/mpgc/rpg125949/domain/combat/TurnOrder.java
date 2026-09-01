package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;

import java.util.Collection;
import java.util.List;

/**
 * Stabilisce in che ordine i combattenti agiscono durante un turno.
 * <p>
 * E' una regola di gioco a se' stante, separata dal motore di battaglia: una
 * variante che introduca, ad esempio, abilita' capaci di anticipare il proprio
 * turno si realizza con una nuova implementazione, senza modificare chi la usa.
 */
public interface TurnOrder {

    /**
     * Dispone i combattenti nell'ordine in cui devono agire.
     *
     * @param fighters combattenti che partecipano al turno; non nullo e privo
     *                 di elementi nulli
     * @return una lista in sola lettura, ordinata da chi agisce per primo a
     *         chi agisce per ultimo
     * @throws NullPointerException se la collezione o uno dei combattenti e' nullo
     */
    List<Fighter> sort(Collection<Fighter> fighters);
}
