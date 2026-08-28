package it.unicam.cs.mpgc.rpg125949.domain.combat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardEffectivenessChartTest {

    private EffectivenessChart chart;

    @BeforeEach
    void setUp() {
        chart = new StandardEffectivenessChart();
    }

    @Test
    void superEffectiveAttacksAreAmplified() {
        assertAll(
                () -> assertEquals(2.0, chart.multiplier(Element.FIRE, Element.NATURE)),
                () -> assertEquals(2.0, chart.multiplier(Element.NATURE, Element.WATER)),
                () -> assertEquals(2.0, chart.multiplier(Element.WATER, Element.FIRE))
        );
    }

    @Test
    void resistedAttacksAreReduced() {
        assertAll(
                () -> assertEquals(0.5, chart.multiplier(Element.NATURE, Element.FIRE)),
                () -> assertEquals(0.5, chart.multiplier(Element.WATER, Element.NATURE)),
                () -> assertEquals(0.5, chart.multiplier(Element.FIRE, Element.WATER))
        );
    }

    @Test
    void attacksAgainstTheSameElementAreNeutral() {
        assertAll(
                () -> assertEquals(1.0, chart.multiplier(Element.FIRE, Element.FIRE)),
                () -> assertEquals(1.0, chart.multiplier(Element.WATER, Element.WATER)),
                () -> assertEquals(1.0, chart.multiplier(Element.NATURE, Element.NATURE))
        );
    }

    @Test
    void neutralElementIsNeitherStrongNorWeak() {
        for (Element other : Element.values()) {
            assertAll(
                    () -> assertEquals(1.0, chart.multiplier(Element.NEUTRAL, other)),
                    () -> assertEquals(1.0, chart.multiplier(other, Element.NEUTRAL))
            );
        }
    }

    /**
     * Protegge da dimenticanze: aggiungendo un nuovo elemento senza aggiornare
     * la tabella, questo test fallisce subito invece di produrre danni sbagliati
     * silenziosamente durante una partita.
     */
    @Test
    void everyPairOfElementsHasAMultiplier() {
        for (Element attacker : Element.values()) {
            for (Element defender : Element.values()) {
                double value = chart.multiplier(attacker, defender);
                assertTrue(value > 0.0,
                        "moltiplicatore mancante o non valido per " + attacker + " contro " + defender);
            }
        }
    }

    @Test
    void rejectsNullElements() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> chart.multiplier(null, Element.FIRE)),
                () -> assertThrows(NullPointerException.class, () -> chart.multiplier(Element.FIRE, null))
        );
    }
}
