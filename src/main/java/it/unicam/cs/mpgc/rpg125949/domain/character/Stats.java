package it.unicam.cs.mpgc.rpg125949.domain.character;

/**
 * Statistiche di combattimento di un personaggio.
 * <p>
 * E' un oggetto-valore immutabile: due istanze con gli stessi valori sono
 * equivalenti e possono essere usate indifferentemente. L'immutabilita' evita
 * che una modifica accidentale durante una battaglia alteri le statistiche di
 * base del combattente, che restano il riferimento per calcolare bonus,
 * malus e avanzamenti di livello.
 *
 * @param maxHp   punti vita massimi; deve essere maggiore di zero
 * @param attack  valore di attacco; non puo' essere negativo
 * @param defense valore di difesa; non puo' essere negativo
 * @param speed   velocita', determina l'ordine dei turni; non puo' essere negativa
 */
public record Stats(int maxHp, int attack, int defense, int speed) {

    /**
     * Verifica che i valori ricevuti descrivano un personaggio valido.
     *
     * @throws IllegalArgumentException se i punti vita massimi non sono
     *                                  positivi o se una delle altre
     *                                  statistiche e' negativa
     */
    public Stats {
        requirePositive(maxHp, "maxHp");
        requireNonNegative(attack, "attack");
        requireNonNegative(defense, "defense");
        requireNonNegative(speed, "speed");
    }

    private static void requirePositive(int value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " deve essere positivo, ricevuto: " + value);
        }
    }

    private static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " non puo' essere negativo, ricevuto: " + value);
        }
    }
}
