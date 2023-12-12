package com.benjaminbinford.day12;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
