package it.unicam.cs.mpgc.rpg125949.domain.character;

import it.unicam.cs.mpgc.rpg125949.domain.combat.Element;

import java.util.Objects;

/**
 * Un combattente che partecipa alle battaglie.
 * <p>
 * A differenza di {@link Stats}, che e' un oggetto-valore, il combattente e'
 * un'entita': ha un'identita' propria e uno stato che evolve nel corso della
 * partita. Nome, elemento e statistiche di base restano immutabili, mentre i
 * punti vita correnti cambiano a ogni colpo subito o curato.
 * <p>
 * La responsabilita' di questa classe si ferma alla gestione della propria
 * salute: non decide quanto danno subire (compito del calcolo del danno) ne'
 * quando agire (compito della gestione dei turni). In questo modo le regole di
 * combattimento possono cambiare senza modificare il combattente.
 */
public class Fighter {

    private final String name;
    private final Element element;
    private final Stats stats;
    private int currentHp;

    /**
     * Crea un combattente al massimo della salute.
     *
     * @param name    nome del combattente; non nullo e non composto di soli spazi
     * @param element elemento di appartenenza; non nullo
     * @param stats   statistiche di base; non nulle
     * @throws NullPointerException     se uno degli argomenti e' nullo
     * @throws IllegalArgumentException se il nome e' vuoto o composto di soli spazi
     */
    public Fighter(String name, Element element, Stats stats) {
        Objects.requireNonNull(name, "name non puo' essere null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name non puo' essere vuoto");
        }
        this.name = name;
        this.element = Objects.requireNonNull(element, "element non puo' essere null");
        this.stats = Objects.requireNonNull(stats, "stats non possono essere null");
        this.currentHp = stats.maxHp();
    }

    public String getName() {
        return name;
    }

    public Element getElement() {
        return element;
    }

    public Stats getStats() {
        return stats;
    }

    public int getCurrentHp() {
        return currentHp;
    }

    /**
     * @return {@code true} se il combattente ha esaurito i punti vita e non
     *         puo' piu' agire
     */
    public boolean isDefeated() {
        return currentHp == 0;
    }

    /**
     * Sottrae punti vita, senza mai scendere sotto lo zero.
     *
     * @param amount danno da applicare; non negativo
     * @throws IllegalArgumentException se {@code amount} e' negativo
     */
    public void takeDamage(int amount) {
        requireNonNegative(amount);
        currentHp = Math.max(0, currentHp - amount);
    }

    /**
     * Ripristina punti vita, senza mai superare il massimo consentito dalle
     * statistiche di base.
     *
     * @param amount punti vita da ripristinare; non negativo
     * @throws IllegalArgumentException se {@code amount} e' negativo
     */
    public void heal(int amount) {
        requireNonNegative(amount);
        currentHp = (int) Math.min(stats.maxHp(), (long) currentHp + amount);
    }

    private static void requireNonNegative(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount non puo' essere negativo, ricevuto: " + amount);
        }
    }
}
