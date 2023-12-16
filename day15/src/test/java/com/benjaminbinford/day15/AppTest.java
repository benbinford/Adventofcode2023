package com.benjaminbinford.day15;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    void shouldAnswerWithTrue() {
        final var input = "rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7";

        final var app = new App(input);

        assertEquals(1320, app.sumHashes());

        assertEquals(145, app.sumFocalLengths());
    }
}
