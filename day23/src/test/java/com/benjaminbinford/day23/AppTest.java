package com.benjaminbinford.day23;

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
        final var input = IO.getResource("com/benjaminbinford/day23/testinput.txt");

        final var app = new App(input);

        assertEquals(94, app.longestHike());
    }

    @Test
    void shouldAnswerWithTrue2() {
        final var input = IO.getResource("com/benjaminbinford/day23/testinput.txt");

        final var app = new App(input);
        app.buildGraph(app::addNeighborsNoIce);
        assertEquals(154, app.longestHikeNoIce());
    }
}
