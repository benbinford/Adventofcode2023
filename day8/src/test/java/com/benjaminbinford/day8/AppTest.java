package com.benjaminbinford.day8;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.benjaminbinford.utils.IO;

/**
 * Unit test for simple App.
 */
class AppTest {

    @Test
    void testInput1() {
        final var input = IO.getResource("com/benjaminbinford/day8/testinput.txt");

        final var app = new App(input);
        final var result = app.runPart1();

        assertEquals(6, result);

    }

    @Test
    void testInput2() {
        final var input = IO.getResource("com/benjaminbinford/day8/testinput2.txt");

        final var app = new App(input);
        final var result = app.runPart1();

        assertEquals(2, result);

    }

    @Test
    void testInput3() {
        final var input = IO.getResource("com/benjaminbinford/day8/testinput3.txt");

        final var app = new App(input);
        final var result = app.runSimultaneous();

        assertEquals(6l, result);
    }

}
