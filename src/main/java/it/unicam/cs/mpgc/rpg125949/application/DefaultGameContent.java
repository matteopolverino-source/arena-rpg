package it.unicam.cs.mpgc.rpg125949.application;

import it.unicam.cs.mpgc.rpg125949.domain.ai.AggressiveAI;
import it.unicam.cs.mpgc.rpg125949.domain.ai.EnemyAI;
import it.unicam.cs.mpgc.rpg125949.domain.ai.RandomAI;
import it.unicam.cs.mpgc.rpg125949.domain.ai.TacticalAI;
import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Stats;
import it.unicam.cs.mpgc.rpg125949.domain.character.Team;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Ability;
import it.unicam.cs.mpgc.rpg125949.domain.combat.DamageAbility;
import it.unicam.cs.mpgc.rpg125949.domain.combat.DamageCalculator;
import it.unicam.cs.mpgc.rpg125949.domain.combat.Element;
import it.unicam.cs.mpgc.rpg125949.domain.combat.HealAbility;
import it.unicam.cs.mpgc.rpg125949.domain.combat.StandardDamageCalculator;
import it.unicam.cs.mpgc.rpg125949.domain.combat.StandardEffectivenessChart;

import java.util.List;
import java.util.Objects;

/**
 * Contenuti della campagna predefinita: tre combattenti dai ruoli distinti e
 * cinque sfide di difficolta' crescente.
 * <p>
 * Il torneo alterna gli elementi degli avversari di proposito: nessun
 * combattente della squadra e' efficace contro tutti, quindi superare le
 * ultime tappe richiede di scegliere chi mandare in campo e non solo di
 * ripetere il colpo piu' forte.
 * <p>
 * Ogni chiamata costruisce oggetti nuovi, perche' i combattenti accumulano
 * ferite: riusare le stesse istanze farebbe iniziare una nuova partita con i
 * danni di quella precedente.
 */
public class DefaultGameContent implements GameContent {

    private final DamageCalculator calculator;

    /**
     * Costruisce i contenuti con la formula di danno predefinita.
     */
    public DefaultGameContent() {
        this(new StandardDamageCalculator(new StandardEffectivenessChart()));
    }

    /**
     * @param calculator formula di danno con cui costruire le abilita' offensive
     * @throws NullPointerException se {@code calculator} e' nullo
     */
    public DefaultGameContent(DamageCalculator calculator) {
        this.calculator = Objects.requireNonNull(calculator, "calculator non puo' essere null");
    }

    @Override
    public Team createPlayerTeam() {
        return new Team(List.of(
                new Fighter("Kael", Element.FIRE, new Stats(130, 48, 32, 42), List.of(
                        attack("Fendente igneo", Element.FIRE, 45),
                        attack("Colpo rapido", Element.NEUTRAL, 28),
                        heal("Respiro di brace", Element.FIRE, 35))),
                new Fighter("Mira", Element.WATER, new Stats(105, 52, 24, 65), List.of(
                        attack("Freccia di ghiaccio", Element.WATER, 46),
                        attack("Raffica di dardi", Element.NEUTRAL, 30),
                        heal("Ristoro sorgivo", Element.WATER, 30))),
                new Fighter("Toren", Element.NATURE, new Stats(160, 38, 45, 25), List.of(
                        attack("Radice spezzante", Element.NATURE, 42),
                        attack("Colpo di scudo", Element.NEUTRAL, 26),
                        heal("Corteccia rigenerante", Element.NATURE, 45)))));
    }

    @Override
    public List<TournamentStage> createStages() {
        return List.of(
                stage("Recluta della sabbia", new RandomAI(),
                        new Fighter("Recluta", Element.NEUTRAL, new Stats(80, 30, 20, 30), List.of(
                                attack("Colpo maldestro", Element.NEUTRAL, 30)))),

                stage("Duellante del vicolo", new RandomAI(),
                        new Fighter("Duellante", Element.WATER, new Stats(110, 38, 28, 48), List.of(
                                attack("Stoccata gelida", Element.WATER, 38),
                                attack("Affondo", Element.NEUTRAL, 28)))),

                stage("Sciamano delle radici", new AggressiveAI(),
                        new Fighter("Sciamano", Element.NATURE, new Stats(130, 44, 32, 38), List.of(
                                attack("Rovo pungente", Element.NATURE, 42),
                                heal("Linfa vitale", Element.NATURE, 30)))),

                stage("Golem di ossidiana", new AggressiveAI(),
                        new Fighter("Golem", Element.FIRE, new Stats(190, 50, 48, 18), List.of(
                                attack("Pugno fuso", Element.FIRE, 48),
                                attack("Schianto", Element.NEUTRAL, 36)))),

                stage("Campione dell'Arena", new TacticalAI(),
                        new Fighter("Campione", Element.NEUTRAL, new Stats(220, 55, 42, 55), List.of(
                                attack("Lama del campione", Element.NEUTRAL, 52),
                                attack("Colpo mortale", Element.NEUTRAL, 70),
                                heal("Secondo fiato", Element.NEUTRAL, 50)))));
    }

    private TournamentStage stage(String name, EnemyAI ai, Fighter... enemies) {
        return new TournamentStage(name, new Team(List.of(enemies)), ai);
    }

    private Ability attack(String name, Element element, int power) {
        return new DamageAbility(name, element, power, calculator);
    }

    private Ability heal(String name, Element element, int amount) {
        return new HealAbility(name, element, amount);
    }
}
