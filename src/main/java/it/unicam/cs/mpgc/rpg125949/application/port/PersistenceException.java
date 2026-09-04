package it.unicam.cs.mpgc.rpg125949.application.port;

/**
 * Segnala che un'operazione di salvataggio o caricamento non e' andata a buon
 * fine.
 * <p>
 * Nasconde a chi la riceve la natura del guasto - un file non accessibile, un
 * contenuto malformato, una connessione caduta - perche' il livello
 * applicativo non deve conoscere la tecnologia di memorizzazione per poter
 * reagire a un errore.
 */
public class PersistenceException extends RuntimeException {

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(String message) {
        super(message);
    }
}
