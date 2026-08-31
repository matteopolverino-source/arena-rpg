package it.unicam.cs.mpgc.rpg125949.domain.combat;

import java.util.Objects;

/**
 * Base comune alle abilita', che raccoglie cio' che ogni abilita' possiede a
 * prescindere dal proprio effetto: un nome e un elemento.
 * <p>
 * L'effetto vero e proprio resta a carico delle sottoclassi, che implementano
 * {@link Ability#applyTo}. Chi vuole aggiungere una nuova abilita' al gioco
 * estende questa classe e scrive solo il comportamento che la distingue.
 */
public abstract class AbstractAbility implements Ability {

    private final String name;
    private final Element element;

    /**
     * @param name    nome dell'abilita'; non nullo e non composto di soli spazi
     * @param element elemento dell'abilita'; non nullo
     * @throws NullPointerException     se un argomento e' nullo
     * @throws IllegalArgumentException se il nome e' vuoto o composto di soli spazi
     */
    protected AbstractAbility(String name, Element element) {
        Objects.requireNonNull(name, "name non puo' essere null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name non puo' essere vuoto");
        }
        this.name = name;
        this.element = Objects.requireNonNull(element, "element non puo' essere null");
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final Element getElement() {
        return element;
    }

    /**
     * Verifica che un valore numerico di configurazione sia positivo.
     *
     * @param value valore da controllare
     * @param field nome del parametro, usato nel messaggio d'errore
     * @return il valore stesso, se valido
     * @throws IllegalArgumentException se il valore non e' positivo
     */
    protected static int requirePositive(int value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " deve essere positivo, ricevuto: " + value);
        }
        return value;
    }
}
