package com.benjaminbinford.day14;

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
        final var input = IO.getResource("com/benjaminbinford/day14/testinput.txt");

        final var app = new App(input);

        app.slideNorth();

        assertEquals("""
                OOOO.#.O..
                OO..#....#
                OO..O##..O
                O..#.OO...
                ........#.
                ..#....#.#
                ..O..#.O.O
                ..O.......
                #....###..
                #....#....""", app.toString());

        assertEquals(136, app.findLoad());

    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testSpins() {
        final var input = IO.getResource("com/benjaminbinford/day14/testinput.txt");

        final var app = new App(input);

        app.spin();

        assertEquals("""
                .....#....
                ....#...O#
                ...OO##...
                .OO#......
                .....OOO#.
                .O#...O#.#
                ....O#....
                ......OOOO
                #...O###..
                #..OO#....""", app.toString());

        app.spin();

        assertEquals("""
                .....#....
                ....#...O#
                .....##...
                ..O#......
                .....OOO#.
                .O#...O#.#
                ....O#...O
                .......OOO
                #..OO###..
                #.OOO#...O""", app.toString());

        app.spin();

        assertEquals("""
                .....#....
                ....#...O#
                .....##...
                ..O#......
                .....OOO#.
                .O#...O#.#
                ....O#...O
                .......OOO
                #...O###.O
                #.OOO#...O""", app.toString());

    }

    @Test
    void testLotsOfSpins() {
        final var input = IO.getResource("com/benjaminbinford/day14/testinput.txt");

        final var app = new App(input);

        assertEquals(64, app.findSpinCycle());

    }
}
