package com.benjaminbinford.day16;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        final var input = IO.getResource("com/benjaminbinford/day16/testinput.txt");

        final var app = new App(input);

        assertEquals(46, app.energize());
    }

    @Test
    void shouldAnswerWithTrue2() {
        final var input = IO.getResource("com/benjaminbinford/day16/testinput.txt");

        final var app = new App(input);

        assertEquals(51, app.energizeMaximum());
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTrue3() {
        final var input = IO.getResource("com/benjaminbinford/day16/input.txt");

        final var app = new App(input);

        assertEquals(7979, app.energize());
    }

    @Test
    void shouldAnswerWithTrue24() {
        final var input = IO.getResource("com/benjaminbinford/day16/input.txt");

        final var app = new App(input);

        assertEquals(8437, app.energizeMaximum());
    }
}
