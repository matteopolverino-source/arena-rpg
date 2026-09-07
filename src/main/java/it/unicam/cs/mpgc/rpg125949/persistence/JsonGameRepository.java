package it.unicam.cs.mpgc.rpg125949.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.unicam.cs.mpgc.rpg125949.application.GameProgress;
import it.unicam.cs.mpgc.rpg125949.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg125949.application.port.PersistenceException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

/**
 * Conserva l'avanzamento in un file JSON sul disco.
 * <p>
 * E' l'unico punto del progetto che sappia cosa sia JSON. Il resto del
 * programma conosce solo la porta {@link GameRepository}, quindi sostituire
 * questa classe con una che scriva su un database o su un servizio remoto non
 * richiede modifiche altrove: basta costruire il gioco passandogli l'altra
 * implementazione.
 * <p>
 * Nessun guasto della libreria di serializzazione trapela verso l'esterno:
 * viene tradotto in {@link PersistenceException}, cosi' chi ha salvato non
 * deve sapere in che formato lo si sia fatto per poter gestire un errore.
 */
public class JsonGameRepository implements GameRepository {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectWriter WRITER = MAPPER.writerWithDefaultPrettyPrinter();

    private final Path saveFile;

    /**
     * @param saveFile file in cui conservare l'avanzamento; non nullo. La
     *                 cartella che lo contiene viene creata se manca.
     * @throws NullPointerException se {@code saveFile} e' nullo
     */
    public JsonGameRepository(Path saveFile) {
        this.saveFile = Objects.requireNonNull(saveFile, "saveFile non puo' essere null");
    }

    @Override
    public void save(GameProgress progress) {
        Objects.requireNonNull(progress, "progress non puo' essere null");
        try {
            Path parent = saveFile.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(saveFile, WRITER.writeValueAsString(progress));
        } catch (IOException cause) {
            throw new PersistenceException("impossibile salvare la partita in " + saveFile, cause);
        }
    }

    @Override
    public Optional<GameProgress> load() {
        if (!Files.exists(saveFile)) {
            return Optional.empty();
        }
        try {
            return Optional.of(MAPPER.readValue(Files.readString(saveFile), GameProgress.class));
        } catch (IOException | IllegalArgumentException cause) {
            // Comprende sia il file illeggibile sia il contenuto malformato o
            // incoerente: per chi chiama la differenza non cambia nulla.
            throw new PersistenceException("il salvataggio in " + saveFile + " non e' leggibile", cause);
        }
    }

    @Override
    public void clear() {
        try {
            Files.deleteIfExists(saveFile);
        } catch (IOException cause) {
            throw new PersistenceException("impossibile eliminare il salvataggio " + saveFile, cause);
        }
    }
}
