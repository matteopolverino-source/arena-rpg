package it.unicam.cs.mpgc.rpg125949.application;

import java.util.Map;
import java.util.Objects;

/**
 * Fotografia dell'avanzamento di una partita, nella forma minima necessaria a
 * riprenderla.
 * <p>
 * Contiene soltanto valori elementari - un numero e una mappa di numeri - e
 * nessun oggetto di dominio. E' una scelta deliberata: cosi' il salvataggio
 * puo' essere tradotto in qualunque formato (JSON, righe di database, campi di
 * un servizio remoto) senza che il formato debba conoscere combattenti,
 * abilita' o strategie, e senza che il dominio debba adattarsi a essere
 * serializzato.
 * <p>
 * Cio' che non compare qui - statistiche, elementi, repertorio di mosse - non
 * viene salvato perche' non cambia durante la partita: al caricamento viene
 * ricostruito dal catalogo dei combattenti.
 *
 * @param stageIndex      tappa raggiunta, contata da zero; non negativa
 * @param healthByFighter punti vita correnti di ogni combattente della squadra,
 *                        indicizzati per nome; nessun valore negativo
 */
public record GameProgress(int stageIndex, Map<String, Integer> healthByFighter) {

    public GameProgress {
        if (stageIndex < 0) {
            throw new IllegalArgumentException("stageIndex non puo' essere negativo, ricevuto: " + stageIndex);
        }
        Objects.requireNonNull(healthByFighter, "healthByFighter non puo' essere null");
        healthByFighter.forEach((name, health) -> {
            Objects.requireNonNull(name, "il nome di un combattente non puo' essere null");
            Objects.requireNonNull(health, "i punti vita di " + name + " non possono essere null");
            if (health < 0) {
                throw new IllegalArgumentException(
                        "i punti vita di " + name + " non possono essere negativi, ricevuti: " + health);
            }
        });
        // Copia difensiva: un salvataggio gia' creato non deve poter cambiare.
        healthByFighter = Map.copyOf(healthByFighter);
    }
}
