package it.unicam.cs.mpgc.rpg125949.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Applicazione JavaFX che ospita l'interfaccia grafica di Arena.
 * <p>
 * Responsabilita': creare la finestra principale e mostrare la schermata
 * iniziale. Non contiene logica di gioco: quest'ultima risiede nei package
 * {@code domain} e {@code application}, che non dipendono da JavaFX.
 */
public class ArenaApplication extends Application {

    private static final String WINDOW_TITLE = "Arena";
    private static final double WINDOW_WIDTH = 900;
    private static final double WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage stage) {
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(new Scene(createTitleScreen(), WINDOW_WIDTH, WINDOW_HEIGHT));
        stage.show();
    }

    /**
     * Costruisce la schermata iniziale.
     *
     * @return il nodo radice della schermata iniziale
     */
    private VBox createTitleScreen() {
        Label title = new Label("ARENA");
        title.setStyle("-fx-font-size: 64px; -fx-font-weight: bold;");

        Label subtitle = new Label("Combattimenti a squadre, a turni");
        subtitle.setStyle("-fx-font-size: 16px;");

        Button newGame = new Button("Nuova partita");
        newGame.setDisable(true);

        VBox root = new VBox(16, title, subtitle, newGame);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        return root;
    }
}
