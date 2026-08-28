package it.unicam.cs.mpgc.rpg125949.domain.combat;

/**
 * Elemento di appartenenza di un combattente o di un'abilita'.
 * <p>
 * Le relazioni di forza e debolezza fra elementi non sono definite qui ma in
 * una {@link EffectivenessChart}: l'enumerazione descrive solo quali elementi
 * esistono, mentre il bilanciamento del gioco resta sostituibile.
 */
public enum Element {
    FIRE, WATER, NATURE, NEUTRAL
}
