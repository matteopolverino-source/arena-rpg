package it.unicam.cs.mpgc.rpg125949.domain.character;

import it.unicam.cs.mpgc.rpg125949.domain.combat.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeamTest {

    private static final Stats STATS = new Stats(100, 20, 15, 30);

    private Fighter kael;
    private Fighter mira;
    private Team team;

    @BeforeEach
    void setUp() {
        kael = new Fighter("Kael", Element.FIRE, STATS);
        mira = new Fighter("Mira", Element.WATER, STATS);
        team = new Team(List.of(kael, mira));
    }

    @Test
    void startsWithTheFirstFighterActive() {
        assertSame(kael, team.getActiveFighter());
    }

    @Test
    void knowsHowManyFightersItHas() {
        assertEquals(2, team.size());
    }

    @Test
    void switchesTheActiveFighter() {
        team.switchTo(mira);

        assertSame(mira, team.getActiveFighter());
    }

    @Test
    void refusesToSwitchToADefeatedFighter() {
        mira.takeDamage(100);

        assertThrows(IllegalArgumentException.class, () -> team.switchTo(mira));
    }

    @Test
    void refusesToSwitchToAFighterThatDoesNotBelongToTheTeam() {
        Fighter stranger = new Fighter("Toren", Element.NATURE, STATS);

        assertThrows(IllegalArgumentException.class, () -> team.switchTo(stranger));
    }

    @Test
    void isNotDefeatedWhileAtLeastOneFighterStands() {
        kael.takeDamage(100);

        assertFalse(team.isDefeated());
    }

    @Test
    void isDefeatedOnlyWhenEveryFighterIsDefeated() {
        kael.takeDamage(100);
        mira.takeDamage(100);

        assertTrue(team.isDefeated());
    }

    @Test
    void rejectsAnEmptyTeam() {
        assertThrows(IllegalArgumentException.class, () -> new Team(List.of()));
    }

    @Test
    void rejectsMoreFightersThanAllowed() {
        List<Fighter> tooMany = new ArrayList<>();
        for (int i = 0; i <= Team.MAX_SIZE; i++) {
            tooMany.add(new Fighter("Combattente " + i, Element.NEUTRAL, STATS));
        }

        assertThrows(IllegalArgumentException.class, () -> new Team(tooMany));
    }

    @Test
    void rejectsNullValues() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> new Team(null)),
                () -> assertThrows(NullPointerException.class, () -> team.switchTo(null))
        );
    }

    /**
     * La squadra non deve poter essere alterata dall'esterno aggirando i suoi
     * controlli: chi la interroga riceve una vista in sola lettura.
     */
    @Test
    void doesNotLeakItsInternalState() {
        List<Fighter> source = new ArrayList<>(List.of(kael, mira));
        Team built = new Team(source);

        source.clear();

        assertAll(
                () -> assertEquals(2, built.size()),
                () -> assertThrows(UnsupportedOperationException.class,
                        () -> built.getFighters().add(kael))
        );
    }
}
