package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Ordine di turno basato sulla velocita': agisce per primo il combattente
 * con la statistica di velocita' piu' alta.
 * <p>
 * A parita' di velocita' viene mantenuto l'ordine di partenza. La scelta e'
 * deliberata: un criterio casuale renderebbe le battaglie irriproducibili e
 * i test non ripetibili. L'ordinamento della libreria standard e' stabile,
 * quindi gli elementi considerati equivalenti conservano la posizione
 * relativa che avevano.
 */
public class SpeedTurnOrder implements TurnOrder {

    private static final Comparator<Fighter> BY_DESCENDING_SPEED =
            Comparator.comparingInt((Fighter fighter) -> fighter.getStats().speed()).reversed();

    @Override
    public List<Fighter> sort(Collection<Fighter> fighters) {
        Objects.requireNonNull(fighters, "fighters non puo' essere null");

        List<Fighter> ordered = new ArrayList<>(fighters);
        for (Fighter fighter : ordered) {
            Objects.requireNonNull(fighter, "la collezione non puo' contenere combattenti nulli");
        }
        ordered.sort(BY_DESCENDING_SPEED);

        // List.copyOf restituisce una lista immutabile: l'ordine di turno
        // calcolato non deve poter essere alterato da chi lo riceve.
        return List.copyOf(ordered);
    }
}
