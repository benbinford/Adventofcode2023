package com.benjaminbinford.day22;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        final var input = IO.getResource("com/benjaminbinford/day22/testinput.txt");

        final var app = new App(input);

        var a = app.getBrickById("A").get();
        var b = app.getBrickById("B").get();
        var c = app.getBrickById("C").get();
        var d = app.getBrickById("D").get();
        var e = app.getBrickById("E").get();
        var f = app.getBrickById("F").get();
        var g = app.getBrickById("G").get();

        assertEquals(1, a.lowestZ());
        assertEquals(1, a.highestZ());
        assertFalse(a.disintegratable());
        assertTrue(a.getSupportedBy().isEmpty());
        assertEquals(2, a.getSupports().size());
        assertTrue(a.getSupports().contains(b));
        assertTrue(a.getSupports().contains(c));

        assertEquals(2, b.lowestZ());
        assertEquals(2, b.highestZ());
        assertTrue(b.disintegratable());
        assertEquals(1, b.getSupportedBy().size());
        assertTrue(b.getSupportedBy().contains(a));
        assertEquals(2, b.getSupports().size());
        assertTrue(b.getSupports().contains(d));
        assertTrue(b.getSupports().contains(e));

        assertEquals(2, c.lowestZ());
        assertEquals(2, c.highestZ());
        assertTrue(c.disintegratable());
        assertEquals(1, c.getSupportedBy().size());
        assertTrue(c.getSupportedBy().contains(a));
        assertEquals(2, c.getSupports().size());
        assertTrue(c.getSupports().contains(d));
        assertTrue(c.getSupports().contains(e));

        assertEquals(3, d.lowestZ());
        assertEquals(3, d.highestZ());
        assertTrue(d.disintegratable());
        assertEquals(2, d.getSupportedBy().size());
        assertTrue(d.getSupportedBy().contains(b));
        assertTrue(d.getSupportedBy().contains(c));
        assertEquals(1, d.getSupports().size());
        assertTrue(d.getSupports().contains(f));

        assertEquals(3, e.lowestZ());
        assertEquals(3, e.highestZ());
        assertTrue(e.disintegratable());
        assertEquals(2, e.getSupportedBy().size());
        assertTrue(e.getSupportedBy().contains(b));
        assertTrue(e.getSupportedBy().contains(c));
        assertEquals(1, e.getSupports().size());
        assertTrue(e.getSupports().contains(f));

        assertEquals(4, f.lowestZ());
        assertEquals(4, f.highestZ());
        assertFalse(f.disintegratable());
        assertEquals(2, f.getSupportedBy().size());
        assertTrue(f.getSupportedBy().contains(d));
        assertTrue(f.getSupportedBy().contains(e));
        assertEquals(1, f.getSupports().size());
        assertTrue(f.getSupports().contains(g));

        assertEquals(5, g.lowestZ());
        assertEquals(6, g.highestZ());
        assertTrue(g.disintegratable());
        assertEquals(1, g.getSupportedBy().size());
        assertTrue(g.getSupportedBy().contains(f));
        assertTrue(g.getSupports().isEmpty());

        assertEquals(5, app.disintegrationCount());
    }

    @Test
    void testChainReaction() {

        final var input = IO.getResource("com/benjaminbinford/day22/testinput.txt");

        final var app = new App(input);

        var a = app.getBrickById("A").get();
        var b = app.getBrickById("B").get();
        var c = app.getBrickById("C").get();
        var d = app.getBrickById("D").get();
        var e = app.getBrickById("E").get();
        var f = app.getBrickById("F").get();
        var g = app.getBrickById("G").get();

        assertEquals(6, a.chainReaction());
        assertEquals(0, b.chainReaction());
        assertEquals(0, c.chainReaction());
        assertEquals(0, d.chainReaction());
        assertEquals(0, e.chainReaction());
        assertEquals(0, f.chainReaction());
        assertEquals(1, g.chainReaction());
    }

}
