package it.unicam.cs.mpgc.rpg125949.ui;

import it.unicam.cs.mpgc.rpg125949.application.GameService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Schermata iniziale: presenta il gioco e offre di cominciare una partita
 * nuova o di riprendere quella salvata.
 */
public class TitleView {

    private final GameService service;
    private final ScreenNavigator navigator;

    public TitleView(GameService service, ScreenNavigator navigator) {
        this.service = Objects.requireNonNull(service, "service non puo' essere null");
        this.navigator = Objects.requireNonNull(navigator, "navigator non puo' essere null");
    }

    public Parent build() {
        Label title = new Label("ARENA");
        title.getStyleClass().add("game-title");

        Label subtitle = new Label("Combattimenti a squadre, a turni");
        subtitle.getStyleClass().add("game-subtitle");

        Button newGame = new Button("Nuova partita");
        newGame.getStyleClass().add("primary-button");
        newGame.setOnAction(event -> {
            service.startNewGame();
            navigator.showBattleScreen();
        });

        Button resume = new Button("Riprendi partita");
        Label feedback = new Label();
        feedback.getStyleClass().add("feedback-text");
        resume.setOnAction(event -> {
            if (service.resumeSavedGame().isPresent()) {
                navigator.showBattleScreen();
            } else {
                feedback.setText("Nessuna partita salvata da riprendere.");
            }
        });

        VBox root = new VBox(14, title, subtitle, newGame, resume, feedback);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("screen");
        return root;
    }
}
