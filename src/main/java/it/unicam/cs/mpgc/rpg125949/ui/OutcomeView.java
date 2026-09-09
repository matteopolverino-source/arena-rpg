package it.unicam.cs.mpgc.rpg125949.ui;

import it.unicam.cs.mpgc.rpg125949.application.GameService;
import it.unicam.cs.mpgc.rpg125949.application.Tournament;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * Schermata conclusiva: annuncia se il torneo e' stato vinto o perso e
 * riporta il giocatore all'inizio.
 * <p>
 * Il salvataggio viene eliminato: una partita conclusa non e' piu'
 * riprendibile, e lasciarne traccia farebbe comparire un pulsante "Riprendi"
 * che riporterebbe a uno scontro gia' deciso.
 */
public class OutcomeView {

    private final GameService service;
    private final ScreenNavigator navigator;

    public OutcomeView(GameService service, ScreenNavigator navigator) {
        this.service = Objects.requireNonNull(service, "service non puo' essere null");
        this.navigator = Objects.requireNonNull(navigator, "navigator non puo' essere null");
    }

    public Parent build() {
        Tournament tournament = service.getTournament();
        boolean victorious = tournament.isWon();
        service.discardSavedGame();

        Label headline = new Label(victorious ? "TRIONFO" : "SCONFITTA");
        headline.getStyleClass().addAll("game-title", victorious ? "outcome-win" : "outcome-loss");

        Label detail = new Label(victorious
                ? "Hai superato tutte le " + tournament.getTotalStages() + " tappe dell'Arena."
                : "La tua squadra e' caduta alla tappa " + tournament.getCurrentStageNumber()
                        + " di " + tournament.getTotalStages() + ".");
        detail.getStyleClass().add("game-subtitle");

        Button again = new Button("Torna al menu");
        again.getStyleClass().add("primary-button");
        again.setOnAction(event -> navigator.showTitleScreen());

        VBox root = new VBox(16, headline, detail, again);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.getStyleClass().add("screen");
        return root;
    }
}
