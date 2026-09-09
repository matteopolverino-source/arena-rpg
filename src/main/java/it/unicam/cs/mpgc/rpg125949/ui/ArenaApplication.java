package it.unicam.cs.mpgc.rpg125949.ui;

import it.unicam.cs.mpgc.rpg125949.application.DefaultGameContent;
import it.unicam.cs.mpgc.rpg125949.application.GameService;
import it.unicam.cs.mpgc.rpg125949.application.port.GameRepository;
import it.unicam.cs.mpgc.rpg125949.domain.combat.SpeedTurnOrder;
import it.unicam.cs.mpgc.rpg125949.persistence.JsonGameRepository;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

/**
 * Applicazione JavaFX che ospita l'interfaccia grafica di Arena.
 * <p>
 * Ha due sole responsabilita': comporre le parti del gioco all'avvio e
 * mostrare una schermata alla volta. E' l'unico punto in cui si decide quali
 * implementazioni usare - i contenuti predefiniti, il salvataggio su file
 * JSON, l'ordine di turno per velocita' - e per questo l'unico da modificare
 * per cambiarle: nessuna delle classi sottostanti le nomina.
 * <p>
 * Nessuna regola di gioco vive in questo package: le schermate interrogano il
 * {@link GameService} e ne mostrano il risultato.
 */
public class ArenaApplication extends Application implements ScreenNavigator {

    private static final String WINDOW_TITLE = "Arena";
    private static final double WINDOW_WIDTH = 900;
    private static final double WINDOW_HEIGHT = 680;

    /** Il salvataggio vive nella cartella personale, non in quella del programma. */
    private static final Path SAVE_FILE =
            Path.of(System.getProperty("user.home"), ".arena-rpg", "partita.json");

    private GameService service;
    private Scene scene;

    @Override
    public void start(Stage stage) {
        GameRepository repository = new JsonGameRepository(SAVE_FILE);
        this.service = new GameService(new DefaultGameContent(), repository, new SpeedTurnOrder());

        this.scene = new Scene(new TitleView(service, this).build(), WINDOW_WIDTH, WINDOW_HEIGHT);
        applyStylesheet(scene);

        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.setMinWidth(760);
        stage.setMinHeight(600);
        stage.show();
    }

    @Override
    public void showTitleScreen() {
        show(new TitleView(service, this).build());
    }

    @Override
    public void showBattleScreen() {
        show(new BattleView(service, this).build());
    }

    @Override
    public void showOutcomeScreen() {
        show(new OutcomeView(service, this).build());
    }

    private void show(Parent screen) {
        scene.setRoot(screen);
    }

    private static void applyStylesheet(Scene target) {
        var stylesheet = ArenaApplication.class.getResource("/arena.css");
        if (stylesheet != null) {
            target.getStylesheets().add(stylesheet.toExternalForm());
        }
    }
}
