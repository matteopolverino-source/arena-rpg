package it.unicam.cs.mpgc.rpg125949.ui;

/**
 * Consente a una schermata di chiederne un'altra senza conoscere chi le
 * gestisca.
 * <p>
 * Ogni schermata dichiara dove vuole andare, non come arrivarci: e' cio' che
 * permette di aggiungere o riordinare le schermate senza modificarle a una a
 * una.
 */
public interface ScreenNavigator {

    /** Mostra la schermata iniziale. */
    void showTitleScreen();

    /** Mostra la schermata di battaglia della partita in corso. */
    void showBattleScreen();

    /** Mostra l'esito finale del torneo, vittorioso o meno. */
    void showOutcomeScreen();
}
