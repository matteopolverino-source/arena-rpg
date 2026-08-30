package it.unicam.cs.mpgc.rpg125949.domain.character;

import java.util.List;
import java.util.Objects;

/**
 * Squadra di combattenti schierata in battaglia.
 * <p>
 * La squadra e' responsabile della propria composizione e di quale combattente
 * sia attualmente in campo. Non conosce le regole della battaglia: si limita a
 * garantire che lo schieramento resti sempre valido, rifiutando composizioni
 * illegali e cambi verso combattenti sconfitti o estranei.
 * <p>
 * La composizione e' fissata alla creazione e non e' modificabile: chi ottiene
 * la lista dei combattenti riceve una vista in sola lettura, cosi' che nessuno
 * possa aggirare i controlli aggiungendo o rimuovendo membri dall'esterno.
 */
public class Team {

    /** Numero massimo di combattenti schierabili in una squadra. */
    public static final int MAX_SIZE = 4;

    private final List<Fighter> fighters;
    private Fighter activeFighter;

    /**
     * Crea una squadra a partire dai combattenti indicati, mettendo in campo
     * il primo dell'elenco.
     *
     * @param fighters combattenti che compongono la squadra
     * @throws NullPointerException     se l'elenco o uno dei combattenti e' nullo
     * @throws IllegalArgumentException se l'elenco e' vuoto o supera {@link #MAX_SIZE}
     */
    public Team(List<Fighter> fighters) {
        Objects.requireNonNull(fighters, "fighters non puo' essere null");
        if (fighters.isEmpty()) {
            throw new IllegalArgumentException("una squadra deve avere almeno un combattente");
        }
        if (fighters.size() > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "una squadra puo' avere al massimo " + MAX_SIZE
                            + " combattenti, ricevuti: " + fighters.size());
        }
        // Copia difensiva: la squadra non deve dipendere dalla lista del chiamante,
        // che potrebbe essere modificata in seguito.
        this.fighters = List.copyOf(fighters);
        this.activeFighter = this.fighters.get(0);
    }

    /**
     * @return i combattenti della squadra, in sola lettura
     */
    public List<Fighter> getFighters() {
        return fighters;
    }

    /**
     * @return il combattente attualmente in campo
     */
    public Fighter getActiveFighter() {
        return activeFighter;
    }

    /**
     * @return quanti combattenti compongono la squadra
     */
    public int size() {
        return fighters.size();
    }

    /**
     * Manda in campo un altro combattente della squadra.
     *
     * @param fighter combattente da schierare
     * @throws NullPointerException     se {@code fighter} e' nullo
     * @throws IllegalArgumentException se il combattente non appartiene alla
     *                                  squadra oppure e' stato sconfitto
     */
    public void switchTo(Fighter fighter) {
        Objects.requireNonNull(fighter, "fighter non puo' essere null");
        if (!fighters.contains(fighter)) {
            throw new IllegalArgumentException(
                    fighter.getName() + " non appartiene a questa squadra");
        }
        if (fighter.isDefeated()) {
            throw new IllegalArgumentException(
                    fighter.getName() + " e' stato sconfitto e non puo' scendere in campo");
        }
        this.activeFighter = fighter;
    }

    /**
     * @return {@code true} se ogni combattente della squadra e' stato sconfitto
     */
    public boolean isDefeated() {
        return fighters.stream().allMatch(Fighter::isDefeated);
    }
}
