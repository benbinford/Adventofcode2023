package com.benjaminbinford.day11;

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
    void testExpand() {
        final var input = IO.getResource("com/benjaminbinford/day11/testinput.txt");

        final var app = new App(input);

        assertEquals("""
                ....#........
                .........#...
                #............
                .............
                .............
                ........#....
                .#...........
                ............#
                .............
                .............
                .........#...
                #....#.......
                """, app.toString());
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testFindShortestPath() {
        final var input = IO.getResource("com/benjaminbinford/day11/testinput.txt");

        final var app = new App(input);

        assertEquals(36, app.getGalaxyPairs().size());

        assertEquals(374, app.getGalaxyPairDistanceSums());
    }
}
