package com.benjaminbinford.day21;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.benjaminbinford.utils.IO;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTrue() {
        final var input = IO.getResource("com/benjaminbinford/day21/testinput.txt");

        final var app = new App(input, 6);

        assertEquals(16, app.availablePlots());
        assertEquals(16, app.availablePlotsInfinite());
    }

    @ParameterizedTest
    @CsvSource({ "6, 16",
            "10,50",
            "50, 1594",
            "100, 6536",
            "500, 167004",
            "1000, 668697",
            "5000, 16733044" })
    void shouldAnswerWithTrue2(int steps, int plots) {
        final var input = IO.getResource("com/benjaminbinford/day21/testinput.txt");

        final var app = new App(input, steps);

        assertEquals(plots, app.availablePlotsInfinite());
    }
}
