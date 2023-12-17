package com.benjaminbinford.day16;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.benjaminbinford.day16.App.Dir;
import com.benjaminbinford.day16.App.Energizer;
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

        Energizer energizer = app.new Energizer();
        energizer.energize(app.cellIndex(0, 0, Dir.RIGHT));
        IO.answer(energizer);
        assertEquals(46, energizer.getEnergy());
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
