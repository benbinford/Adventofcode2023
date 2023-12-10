package com.benjaminbinford.day10;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.benjaminbinford.utils.IO;

/**
 * Unit test for simple App.
 */
class AppTest {

    @Test
    void testFindMaxDistance1() {
        final var input = IO.getResource("com/benjaminbinford/day10/testinput.txt");

        final var app = new App(input, 'F');

        assertEquals(4, app.findMaxDistance());

    }

    @Test
    void testFindMaxDistance2() {
        final var input = IO.getResource("com/benjaminbinford/day10/testinput2.txt");

        final var app = new App(input, 'F');

        assertEquals(8, app.findMaxDistance());

    }

    @Test
    void testFindArea3() {
        final var input = IO.getResource("com/benjaminbinford/day10/testinput3.txt");

        final var app = new App(input, 'F');

        assertEquals(4, app.findAreaEnclosed());

    }

    @Test
    void testFindArea4() {
        final var input = IO.getResource("com/benjaminbinford/day10/testinput4.txt");

        final var app = new App(input, 'F');

        assertEquals(8, app.findAreaEnclosed());

    }

    @Test
    void testFindArea5() {
        final var input = IO.getResource("com/benjaminbinford/day10/testinput5.txt");

        final var app = new App(input, '7');

        assertEquals(10, app.findAreaEnclosed());

    }
}
