package it.unicam.cs.mpgc.rpg125949.application.port;

import it.unicam.cs.mpgc.rpg125949.application.GameProgress;

import java.util.Optional;

/**
 * Porta attraverso cui il gioco salva e recupera l'avanzamento di una partita.
 * <p>
 * E' definita qui, accanto a chi la usa, e non insieme all'implementazione:
 * il livello applicativo dichiara di che cosa ha bisogno, e sono le
 * implementazioni ad adeguarsi. Questa e' l'inversione delle dipendenze in
 * pratica: il gioco non sa se l'avanzamento finisca in un file JSON, in un
 * database o su un servizio remoto, e sostituire l'uno con l'altro non
 * comporta alcuna modifica alla logica di gioco.
 */
public interface GameRepository {

    /**
     * Registra l'avanzamento, sostituendo quello eventualmente gia' presente.
     *
     * @param progress avanzamento da conservare; non nullo
     * @throws NullPointerException  se {@code progress} e' nullo
     * @throws PersistenceException  se il salvataggio non va a buon fine
     */
    void save(GameProgress progress);

    /**
     * Recupera l'ultimo avanzamento registrato.
     *
     * @return l'avanzamento salvato, oppure vuoto se non ne esiste alcuno
     * @throws PersistenceException se il salvataggio esiste ma non e' leggibile
     */
    Optional<GameProgress> load();

    /**
     * Elimina l'avanzamento registrato, se presente. Ripetere l'operazione su
     * un salvataggio inesistente non e' un errore.
     *
     * @throws PersistenceException se la cancellazione non va a buon fine
     */
    void clear();
}
