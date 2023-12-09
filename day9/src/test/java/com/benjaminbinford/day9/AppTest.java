package com.benjaminbinford.day9;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    void setOasis1() {
        final var input = """
                0 3 6 9 12 15
                1 3 6 10 15 21
                10 13 16 21 30 45
                """;

        final var app = new App(input);

        assertArrayEquals(new int[] { 18, 28, 68 }, app.extrapolateOasis().toArray());

    }

    @Test
    void testExtrapolateLine1() {
        assertEquals(18, new App("0 3 6 9 12 15").extrapolateLine(new int[] { 0, 3, 6, 9, 12, 15 }));
    }

    @Test
    void testExtrapolateLine2() {
        assertEquals(28, new App("1 3 6 10 15 21").extrapolateLine(new int[] { 1, 3, 6, 10, 15, 21 }));
    }

    @Test
    void testExtrapolateLine3Backwards() {
        assertEquals(5, new App("45 30 21 16 13 10").extrapolateLine(new int[] { 45, 30, 21, 16, 13, 10 }));
    }

}
