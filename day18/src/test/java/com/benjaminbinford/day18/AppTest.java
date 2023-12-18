package com.benjaminbinford.day18;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        final var input = IO.getResource("com/benjaminbinford/day18/testinput.txt");

        final var app = new App(input);

        // IO.answer(app);
        // assertEquals("""
        // ╔-----╗
        // |.....|
        // ╚-╗...|
        // ..|...|
        // ..|...|
        // ╔-╝.╔-╝
        // |...|..
        // ╚╗..╚-╗
        // .|....|
        // .╚----╝
        // """, app.toString());

        assertEquals(62l, app.lagoonSize());
    }

    // /**
    // * Rigorous Test :-)
    // */
    // @Test
    // void testBig() {
    // final var input = IO.getResource("com/benjaminbinford/day18/testinput.txt");

    // final var app = new App(input, App::translateInstruction);

    // app.fillLagoon();

    // assertEquals(952408144115l, app.lagoonSize());
    // }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testPart1() {
        final var input = IO.getResource("com/benjaminbinford/day18/input.txt");

        final var app = new App(input);

        assertEquals(46394, app.lagoonSize());
    }

    @ParameterizedTest
    @CsvSource({
            "R 6 (#70c710), R 461937 (none)",
            "D 5 (#0dc571), D 56407 (none)",
            "L 2 (#5713f0), R 356671 (none)",
            "D 2 (#d2c081), D 863240 (none)",
            "R 2 (#59c680), R 367720 (none)",
            "D 2 (#411b91), D 266681 (none)",
            "L 5 (#8ceee2), L 577262 (none)",
            "U 2 (#caa173), U 829975 (none)",
            "L 1 (#1b58a2), L 112010 (none)",
            "U 2 (#caa171), D 829975 (none)",
            "R 2 (#7807d2), L 491645 (none)",
            "U 3 (#a77fa3), U 686074 (none)",
            "L 2 (#015232), L 5411 (none)",
            "U 2 (#7a21e3), U 500254 (none)",
    })
    void testTranslateInstruction(String input, String expected) {
        assertEquals(expected, App.translateInstruction(input));
    }

}
