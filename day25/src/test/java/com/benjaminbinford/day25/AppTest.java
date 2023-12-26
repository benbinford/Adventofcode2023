package com.benjaminbinford.day25;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
    void shouldAnswerWithTrue() {
        final var input = IO.getResource("com/benjaminbinford/day25/testinput.txt");

        final var app = new App(input);

        var ps = App.Partition.partition(app.g);
        assertEquals(3, ps.edges().size());
        assertTrue(ps.edges().contains(Tuple2.of("pzl", "hfx")));
        assertTrue(ps.edges().contains(Tuple2.of("cmg", "bvb")));
        assertTrue(ps.edges().contains(Tuple2.of("jqt", "nvd")));

        var g1Vertices = List.of("cmg", "frs", "lhk", "lsr", "nvd", "pzl", "qnr", "rsh", "rzs");
        var g2Vertices = List.of("bvb", "hfx", "jqt", "ntq", "rhn", "xhk");

        assertTrue(ps.g1().size() == 9 || ps.g2().size() == 9);
        assertTrue(ps.g1().size() == 6 | ps.g2().size() == 6);

        assertTrue(ps.g1().getVertices().containsAll(g1Vertices) || ps.g2().getVertices().containsAll(g1Vertices));
        assertTrue(ps.g1().getVertices().containsAll(g2Vertices) || ps.g2().getVertices().containsAll(g2Vertices));
    }
}
