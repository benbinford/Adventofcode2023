package com.benjaminbinford.day17;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

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
        final var input = IO.getResource("com/benjaminbinford/day17/testinput.txt");

        final var app = new App(input);

        assertEquals(102, app.heatLoss(1, 3));
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTrueUltra2() {
        final var input = """
                111111111111
                999999999991
                999999999991
                999999999991
                999999999991
                    """;
        final var app = new App(input);

        assertEquals(71, app.heatLoss(4, 10));
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTrueUltra3() {
        final var input = """
                111199999999
                999199999999
                999199999999
                999199999999
                999199999999
                999199999999
                999199999999
                999199999999
                999199999999
                999199999999
                999199999999
                999111111119
                    """;
        final var app = new App(input);

        assertEquals(86, app.heatLoss(4, 10));
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTrueUltra() {
        final var input = IO.getResource("com/benjaminbinford/day17/testinput.txt");

        final var app = new App(input);

        assertEquals(94, app.heatLoss(4, 10));
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTru2() {
        final var input = IO.getResource("com/benjaminbinford/day17/input.txt");

        final var app = new App(input);

        assertEquals(963, app.heatLoss(1, 3));
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTru4() {
        final var input = IO.getResource("com/benjaminbinford/day17/input.txt");

        final var app = new App(input);

        assertEquals(1178, app.heatLoss(4, 10));
    }
}
