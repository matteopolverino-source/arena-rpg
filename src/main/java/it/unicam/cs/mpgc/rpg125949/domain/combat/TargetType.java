package it.unicam.cs.mpgc.rpg125949.domain.combat;

/**
 * Indica su chi vada applicata un'abilita'.
 * <p>
 * E' l'abilita' stessa a dichiararlo, cosi' che il motore di battaglia possa
 * individuare il bersaglio senza conoscere il tipo concreto dell'abilita' che
 * sta eseguendo.
 */
public enum TargetType {

    /** L'abilita' si applica al combattente che la usa. */
    SELF,

    /** L'abilita' si applica al combattente avversario. */
    OPPONENT
}
