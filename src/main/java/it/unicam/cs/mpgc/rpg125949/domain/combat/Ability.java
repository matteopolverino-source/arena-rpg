package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;

/**
 * Azione che un combattente puo' eseguire durante il proprio turno.
 * <p>
 * Ogni abilita' decide da se' che effetto produrre: aggiungere una nuova
 * abilita' al gioco significa scrivere una nuova implementazione di questa
 * interfaccia, senza modificare il motore di battaglia ne' le abilita'
 * gia' esistenti.
 * <p>
 * Il metodo restituisce un numero e non una frase: comporre i messaggi da
 * mostrare al giocatore e' compito dell'interfaccia utente, non del dominio.
 */
public interface Ability {

    /**
     * @return il nome con cui l'abilita' viene presentata al giocatore
     */
    String getName();

    /**
     * @return l'elemento a cui l'abilita' appartiene
     */
    Element getElement();

    /**
     * @return su chi va applicata questa abilita'
     */
    TargetType getTargetType();

    /**
     * Stima l'effetto che l'abilita' produrrebbe, senza produrlo.
     * <p>
     * Permette a un'intelligenza artificiale di confrontare fra loro le
     * abilita' disponibili senza doverne conoscere il tipo concreto: senza
     * questo metodo l'unica alternativa sarebbe ispezionare la classe delle
     * abilita' una per una, vanificando la loro sostituibilita'.
     * <p>
     * Il valore e' teorico: non tiene conto dei punti vita residui del
     * bersaglio, cosi' che due abilita' molto potenti restino distinguibili
     * anche contro un avversario ormai allo stremo.
     *
     * @param user   combattente che userebbe l'abilita'
     * @param target combattente su cui verrebbe usata
     * @return l'entita' prevista dell'effetto
     * @throws NullPointerException se uno dei due combattenti e' nullo
     */
    int estimateEffect(Fighter user, Fighter target);

    /**
     * Esegue l'abilita'.
     *
     * @param user   combattente che usa l'abilita'
     * @param target combattente su cui viene usata
     * @return l'entita' dell'effetto realmente prodotto, cioe' il danno
     *         inflitto o i punti vita ripristinati; puo' essere inferiore al
     *         valore teorico se il bersaglio era gia' quasi sconfitto o
     *         quasi sano
     * @throws NullPointerException se uno dei due combattenti e' nullo
     */
    int applyTo(Fighter user, Fighter target);
}
