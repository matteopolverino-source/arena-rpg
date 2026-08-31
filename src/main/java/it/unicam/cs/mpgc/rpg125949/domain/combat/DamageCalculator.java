package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;

/**
 * Calcola il danno di un attacco.
 * <p>
 * La formula del danno e' una regola di bilanciamento, non una responsabilita'
 * del combattente ne' del motore di battaglia: isolarla dietro un'interfaccia
 * permette di sostituirla (ad esempio con una variante che introduce i colpi
 * critici) senza modificare nient'altro.
 */
public interface DamageCalculator {

    /**
     * @param attacker chi sferra l'attacco
     * @param defender chi lo subisce
     * @param power    potenza dell'abilita' usata; deve essere positiva
     * @param element  elemento dell'abilita' usata
     * @return il danno da applicare, sempre almeno pari a 1
     */
    int computeDamage(Fighter attacker, Fighter defender, int power, Element element);
}
