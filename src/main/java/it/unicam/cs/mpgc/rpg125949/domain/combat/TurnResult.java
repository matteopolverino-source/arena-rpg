package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;

import java.util.Objects;

/**
 * Resoconto di una singola azione eseguita durante un turno.
 * <p>
 * Descrive che cosa e' accaduto in termini di dominio - chi ha agito, con
 * quale abilita', su chi e con quale effetto - lasciando all'interfaccia
 * utente il compito di trasformarlo in una frase leggibile. Il dominio non
 * produce testo: se lo facesse, una futura versione web o mobile si
 * ritroverebbe i messaggi in italiano incorporati nella logica di gioco.
 *
 * @param actor   combattente che ha agito
 * @param ability abilita' utilizzata
 * @param target  combattente su cui l'abilita' e' stata applicata
 * @param effect  entita' dell'effetto prodotto, in punti vita
 */
public record TurnResult(Fighter actor, Ability ability, Fighter target, int effect) {

    public TurnResult {
        Objects.requireNonNull(actor, "actor non puo' essere null");
        Objects.requireNonNull(ability, "ability non puo' essere null");
        Objects.requireNonNull(target, "target non puo' essere null");
    }
}
