package com.benjaminbinford.day12;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * Unit test for simple App.
 */
class AppTest {
    @ParameterizedTest
    @CsvFileSource(resources = "/com/benjaminbinford/day12/testinput.txt", delimiter = '|')
    void shouldAnswerWithTrue(String line, int expected) {

        final var app = new App(line);
        assertEquals(expected, app.sumArrangements());

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/com/benjaminbinford/day12/testinputmine.txt", delimiter = '|')
    void testmine(String line, int expected) {

        final var app = new App(line.replace("X", ""));
        assertEquals(expected, app.sumArrangements());

    }

    @ParameterizedTest
    @CsvFileSource(resources = "/com/benjaminbinford/day12/testinput5.txt", delimiter = '|')
    void shouldAnswerWithTrue5(String line, int expected) {

        final var app = new App(line.replace("X", ""));
        assertEquals(app.sumExplodedArrangementsLong(), app.sumExplodedArrangements());

    }

    @Test
    void testDontRun() {
        // final var app = new App(
        // "?#.#.?###???.#?#.?. 1,1,6,1,1,1");
        // assertEquals(2, app.sumArrangements());
        // final var app2 = new App(
        // "#.#.?###???.#?#.?. 1,1,6,1,1,1");
        // assertEquals(2, app2.sumArrangements());
        final var app3 = new App(
                "#.#.?###???.#?#.?.? 1,1,6,1,1,1");
        assertEquals(4, app3.sumArrangements());
    }
}
