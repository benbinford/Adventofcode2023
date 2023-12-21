package com.benjaminbinford.day21;

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
        final var input = IO.getResource("com/benjaminbinford/day21/testinput.txt");

        final var app = new App(input, 6);

        assertEquals(16, app.availablePlots());
    }
}
