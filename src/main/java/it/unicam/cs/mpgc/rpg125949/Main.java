package it.unicam.cs.mpgc.rpg125949;

import it.unicam.cs.mpgc.rpg125949.ui.ArenaApplication;
import javafx.application.Application;

/**
 * Punto di ingresso dell'applicazione.
 * <p>
 * Questa classe non estende {@link Application} di proposito: delegando
 * l'avvio ad {@link ArenaApplication} l'applicazione resta eseguibile anche
 * quando JavaFX non e' presente sul module path, evitando l'errore
 * "JavaFX runtime components are missing".
 * <p>
 * La sua unica responsabilita' e' avviare l'interfaccia utente. Una futura
 * versione headless (server, CLI, mobile) puo' sostituire questa classe
 * senza toccare il resto del progetto.
 */
public final class Main {

    private Main() {
        // Classe di utilita': non deve essere istanziata.
    }

    public static void main(String[] args) {
        Application.launch(ArenaApplication.class, args);
    }
}
