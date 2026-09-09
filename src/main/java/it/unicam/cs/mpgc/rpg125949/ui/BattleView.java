package it.unicam.cs.mpgc.rpg125949.ui;

import it.unicam.cs.mpgc.rpg125949.application.GameService;
import it.unicam.cs.mpgc.rpg125949.application.Tournament;
import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Battle;
import it.unicam.cs.mpgc.rpg125949.domain.combat.TargetType;
import it.unicam.cs.mpgc.rpg125949.domain.combat.TurnResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

/**
 * Schermata di combattimento.
 * <p>
 * Mostra lo stato della battaglia e inoltra al {@link GameService} la mossa
 * scelta dal giocatore. Non contiene alcuna regola di gioco: non calcola
 * danni, non decide chi agisce per primo, non sa quando una battaglia sia
 * conclusa. Si limita a chiedere e a mostrare.
 * <p>
 * L'unica cosa che le compete davvero e' tradurre i resoconti di turno in
 * frasi leggibili: il dominio restituisce numeri, la lingua vive qui.
 */
public class BattleView {

    private final GameService service;
    private final ScreenNavigator navigator;

    private final Label stageLabel = new Label();
    private final Label progressLabel = new Label();
    private final FighterPanel playerPanel = new FighterPanel("La tua squadra");
    private final FighterPanel enemyPanel = new FighterPanel("Avversario");
    private final FlowPane abilityBar = new FlowPane(8, 8);
    private final HBox rosterBar = new HBox(8);
    private final ObservableList<String> log = FXCollections.observableArrayList();
    private final ListView<String> logView = new ListView<>(log);

    private int lastAnnouncedStage;

    public BattleView(GameService service, ScreenNavigator navigator) {
        this.service = Objects.requireNonNull(service, "service non puo' essere null");
        this.navigator = Objects.requireNonNull(navigator, "navigator non puo' essere null");
    }

    public Parent build() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("screen");
        root.setPadding(new Insets(16));
        root.setTop(buildHeader());
        root.setCenter(buildArena());
        root.setBottom(buildControls());

        Tournament tournament = service.getTournament();
        lastAnnouncedStage = tournament.getCurrentStageNumber();
        announce("Inizia lo scontro: " + tournament.getCurrentStage().name() + ".");
        refresh();
        return root;
    }

    private VBox buildHeader() {
        stageLabel.getStyleClass().add("stage-name");
        progressLabel.getStyleClass().add("stage-progress");
        VBox header = new VBox(2, stageLabel, progressLabel);
        header.setPadding(new Insets(0, 0, 12, 0));
        return header;
    }

    private HBox buildArena() {
        HBox.setHgrow(playerPanel, Priority.ALWAYS);
        HBox.setHgrow(enemyPanel, Priority.ALWAYS);
        playerPanel.setMaxWidth(Double.MAX_VALUE);
        enemyPanel.setMaxWidth(Double.MAX_VALUE);

        HBox arena = new HBox(16, playerPanel, enemyPanel);
        arena.setAlignment(Pos.TOP_CENTER);
        return arena;
    }

    private VBox buildControls() {
        Label rosterTitle = new Label("Manda in campo");
        rosterTitle.getStyleClass().add("section-title");

        Label abilityTitle = new Label("Scegli la mossa");
        abilityTitle.getStyleClass().add("section-title");

        Label logTitle = new Label("Resoconto");
        logTitle.getStyleClass().add("section-title");
        logView.setPrefHeight(150);
        logView.setFocusTraversable(false);

        Button save = new Button("Salva partita");
        save.setOnAction(event -> {
            service.saveProgress();
            announce("Partita salvata.");
        });

        Button quit = new Button("Abbandona");
        quit.setOnAction(event -> navigator.showTitleScreen());

        HBox sessionBar = new HBox(8, save, quit);
        sessionBar.setAlignment(Pos.CENTER_RIGHT);

        VBox controls = new VBox(8, logTitle, logView, rosterTitle, rosterBar,
                abilityTitle, abilityBar, sessionBar);
        controls.setPadding(new Insets(14, 0, 0, 0));
        return controls;
    }

    /** Riallinea l'intera schermata allo stato attuale della partita. */
    private void refresh() {
        Tournament tournament = service.getTournament();
        Battle battle = tournament.getCurrentBattle();
        Fighter playerFighter = battle.getPlayerTeam().getActiveFighter();

        stageLabel.setText(tournament.getCurrentStage().name());
        progressLabel.setText("Tappa " + tournament.getCurrentStageNumber()
                + " di " + tournament.getTotalStages()
                + "  ·  turno " + (battle.getRoundNumber() + 1));

        playerPanel.show(playerFighter);
        enemyPanel.show(battle.getEnemyTeam().getActiveFighter());

        rebuildRosterBar(battle, playerFighter);
        rebuildAbilityBar(playerFighter);
    }

    private void rebuildRosterBar(Battle battle, Fighter active) {
        rosterBar.getChildren().clear();
        for (Fighter member : battle.getPlayerTeam().getFighters()) {
            Button button = new Button(member.getName() + "  " + FighterPanel.symbolOf(member.getElement()));
            button.setDisable(member.isDefeated() || member == active);
            button.setOnAction(event -> {
                service.switchActiveFighter(member);
                announce(member.getName() + " scende in campo.");
                refresh();
            });
            rosterBar.getChildren().add(button);
        }
    }

    private void rebuildAbilityBar(Fighter active) {
        abilityBar.getChildren().clear();
        for (Ability ability : active.getAbilities()) {
            Button button = new Button(ability.getName());
            button.getStyleClass().add("ability-button");
            button.setOnAction(event -> playRound(ability));
            abilityBar.getChildren().add(button);
        }
    }

    private void playRound(Ability chosen) {
        List<TurnResult> report = service.playRound(chosen);
        report.forEach(result -> announce(describe(result)));

        Tournament tournament = service.getTournament();
        if (tournament.isOver()) {
            navigator.showOutcomeScreen();
            return;
        }
        if (tournament.getCurrentStageNumber() != lastAnnouncedStage) {
            lastAnnouncedStage = tournament.getCurrentStageNumber();
            announce("Avversario sconfitto! La squadra si rimette in forze.");
            announce("Nuova sfida: " + tournament.getCurrentStage().name() + ".");
        }
        refresh();
    }

    /**
     * Traduce un resoconto di turno in una frase per il giocatore. E' l'unico
     * punto in cui il gioco parla italiano: il dominio produce solo numeri.
     */
    private String describe(TurnResult result) {
        if (result.ability().getTargetType() == TargetType.SELF) {
            return result.actor().getName() + " usa " + result.ability().getName()
                    + " e recupera " + result.effect() + " PV.";
        }
        return result.actor().getName() + " usa " + result.ability().getName()
                + " su " + result.target().getName() + ": " + result.effect() + " danni."
                + (result.target().isDefeated() ? "  " + result.target().getName() + " e' sconfitto!" : "");
    }

    private void announce(String message) {
        log.add(message);
        logView.scrollTo(log.size() - 1);
    }
}
