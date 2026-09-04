package it.unicam.cs.mpgc.rpg125949.application;

import it.unicam.cs.mpgc.rpg125949.domain.ai.EnemyAI;
import it.unicam.cs.mpgc.rpg125949.domain.character.Team;

import java.util.Objects;

/**
 * Una tappa del torneo: l'avversario da affrontare e la strategia che lo
 * governa.
 * <p>
 * Legare la strategia alla tappa invece che al torneo permette di comporre
 * scale di difficolta' diverse - crescenti, alternate, tematiche - senza
 * modificare il torneo, che si limita a usare cio' che la tappa dichiara.
 *
 * @param name    nome con cui la tappa viene presentata al giocatore
 * @param enemies squadra da sconfiggere
 * @param ai      strategia che decide le mosse dell'avversario
 */
public record TournamentStage(String name, Team enemies, EnemyAI ai) {

    public TournamentStage {
        Objects.requireNonNull(name, "name non puo' essere null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("name non puo' essere vuoto");
        }
        Objects.requireNonNull(enemies, "enemies non puo' essere null");
        Objects.requireNonNull(ai, "ai non puo' essere null");
    }
}
