package it.unicam.cs.mpgc.rpg125949.domain.combat;

import it.unicam.cs.mpgc.rpg125949.domain.character.Fighter;
import it.unicam.cs.mpgc.rpg125949.domain.character.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpeedTurnOrderTest {

    /** Repertorio minimo: un combattente deve conoscere almeno un'abilita'. */
    private static final List<Ability> ABILITIES =
            List.of(new HealAbility("Riposo", Element.NEUTRAL, 1));

    private TurnOrder turnOrder;

    @BeforeEach
    void setUp() {
        turnOrder = new SpeedTurnOrder();
    }

    private static Fighter withSpeed(String name, int speed) {
        return new Fighter(name, Element.NEUTRAL, new Stats(100, 20, 20, speed), ABILITIES);
    }

    @Test
    void putsTheFastestFighterFirst() {
        Fighter slow = withSpeed("Lento", 10);
        Fighter fast = withSpeed("Veloce", 90);
        Fighter medium = withSpeed("Medio", 50);

        List<Fighter> ordered = turnOrder.sort(List.of(slow, fast, medium));

        assertEquals(List.of("Veloce", "Medio", "Lento"), ordered.stream().map(Fighter::getName).toList());
    }

    /**
     * A parita' di velocita' l'ordine deve restare quello di partenza: un
     * ordinamento non deterministico renderebbe le battaglie irriproducibili
     * e i test instabili.
     */
    @Test
    void keepsTheOriginalOrderWhenSpeedsAreEqual() {
        Fighter first = withSpeed("Primo", 50);
        Fighter second = withSpeed("Secondo", 50);
        Fighter third = withSpeed("Terzo", 50);

        List<Fighter> ordered = turnOrder.sort(List.of(first, second, third));

        assertEquals(List.of("Primo", "Secondo", "Terzo"), ordered.stream().map(Fighter::getName).toList());
    }

    @Test
    void isDeterministicAcrossRepeatedCalls() {
        List<Fighter> fighters = List.of(withSpeed("A", 30), withSpeed("B", 30), withSpeed("C", 70));

        List<String> firstRun = turnOrder.sort(fighters).stream().map(Fighter::getName).toList();
        List<String> secondRun = turnOrder.sort(fighters).stream().map(Fighter::getName).toList();

        assertEquals(firstRun, secondRun);
    }

    @Test
    void doesNotModifyTheGivenCollection() {
        List<Fighter> source = new ArrayList<>(List.of(withSpeed("Lento", 10), withSpeed("Veloce", 90)));

        turnOrder.sort(source);

        assertEquals(List.of("Lento", "Veloce"), source.stream().map(Fighter::getName).toList());
    }

    @Test
    void returnsAReadOnlyResult() {
        List<Fighter> ordered = turnOrder.sort(List.of(withSpeed("Solo", 10)));

        assertThrows(UnsupportedOperationException.class, () -> ordered.add(withSpeed("Intruso", 99)));
    }

    @Test
    void handlesASingleFighter() {
        Fighter only = withSpeed("Solo", 42);

        assertEquals(List.of(only), turnOrder.sort(List.of(only)));
    }

    @Test
    void handlesAnEmptyCollection() {
        assertTrue(turnOrder.sort(List.of()).isEmpty());
    }

    @Test
    void rejectsInvalidInput() {
        List<Fighter> withNull = new ArrayList<>();
        withNull.add(withSpeed("Valido", 10));
        withNull.add(null);

        assertAll(
                () -> assertThrows(NullPointerException.class, () -> turnOrder.sort(null)),
                () -> assertThrows(NullPointerException.class, () -> turnOrder.sort(withNull))
        );
    }
}
