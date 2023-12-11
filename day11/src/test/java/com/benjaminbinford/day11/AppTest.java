package com.benjaminbinford.day11;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.typemeta.funcj.tuples.Tuple2;

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

        final var app = new App(input, 2);

        final List<Tuple2<Long, Long>> gs = app.getGalaxyCenters().stream()
                .map(g -> Tuple2.of(g.getRow(), g.getCol()))
                .collect(Collectors.toList());

        assertEquals(List.of(
                Tuple2.of(0l, 4l),
                Tuple2.of(1l, 9l),
                Tuple2.of(2l, 0l),
                Tuple2.of(5l, 8l),
                Tuple2.of(6l, 1l),
                Tuple2.of(7l, 12l),
                Tuple2.of(10l, 9l),
                Tuple2.of(11l, 0l),
                Tuple2.of(11l, 5l)

        ), gs);
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testFindShortestPath() {
        final var input = IO.getResource("com/benjaminbinford/day11/testinput.txt");

        final var app = new App(input, 2);

        assertEquals(36, app.getGalaxyPairs().size());

        assertEquals(374, app.getGalaxyPairDistanceSums());
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testFindShortestPath10() {
        final var input = IO.getResource("com/benjaminbinford/day11/testinput.txt");

        final var app = new App(input, 10);

        assertEquals(36, app.getGalaxyPairs().size());

        assertEquals(1030, app.getGalaxyPairDistanceSums());
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testFindShortestPath100() {
        final var input = IO.getResource("com/benjaminbinford/day11/testinput.txt");

        final var app = new App(input, 100);

        assertEquals(36, app.getGalaxyPairs().size());

        assertEquals(8410, app.getGalaxyPairDistanceSums());
    }
}
