package it.unicam.cs.mpgc.rpg125949.ui;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Element;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 * Riquadro che mostra lo stato di un combattente: nome, elemento e punti vita.
 * <p>
 * Non conserva alcuno stato proprio: a ogni aggiornamento rilegge il
 * combattente. Cosi' non puo' mostrare informazioni divergenti da quelle reali,
 * che e' l'errore piu' comune quando un'interfaccia duplica i dati del modello.
 */
public class FighterPanel extends VBox {

    private static final double WOUNDED_RATIO = 0.5;
    private static final double CRITICAL_RATIO = 0.25;

    private final Label nameLabel = new Label();
    private final Label healthLabel = new Label();
    private final ProgressBar healthBar = new ProgressBar(1.0);

    public FighterPanel(String heading) {
        super(6);
        setPadding(new Insets(14));
        getStyleClass().add("fighter-panel");

        Label headingLabel = new Label(heading);
        headingLabel.getStyleClass().add("panel-heading");
        nameLabel.getStyleClass().add("fighter-name");
        healthLabel.getStyleClass().add("health-text");
        healthBar.setMaxWidth(Double.MAX_VALUE);
        healthBar.setPrefHeight(18);

        getChildren().addAll(headingLabel, nameLabel, healthBar, healthLabel);
    }

    /**
     * Riallinea il riquadro allo stato attuale del combattente indicato.
     *
     * @param fighter combattente da rappresentare
     */
    public void show(Fighter fighter) {
        int maxHp = fighter.getStats().maxHp();
        double ratio = (double) fighter.getCurrentHp() / maxHp;

        nameLabel.setText(fighter.getName() + "  " + symbolOf(fighter.getElement()));
        healthLabel.setText(fighter.getCurrentHp() + " / " + maxHp + " PV");
        healthBar.setProgress(ratio);

        healthBar.getStyleClass().removeAll("health-healthy", "health-wounded", "health-critical");
        healthBar.getStyleClass().add(healthStyleFor(ratio));
    }

    private static String healthStyleFor(double ratio) {
        if (ratio <= CRITICAL_RATIO) {
            return "health-critical";
        }
        if (ratio <= WOUNDED_RATIO) {
            return "health-wounded";
        }
        return "health-healthy";
    }

    /**
     * Traduce l'elemento in un simbolo. La corrispondenza vive qui e non
     * nell'enumerazione perche' e' una scelta di presentazione: un'altra
     * interfaccia potrebbe usare icone, colori o nomi per esteso.
     */
    static String symbolOf(Element element) {
        return switch (element) {
            case FIRE -> "🔥";
            case WATER -> "💧";
            case NATURE -> "🌿";
            case NEUTRAL -> "⬜";
        };
    }
}
