package com.benjaminbinford.day19;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.benjaminbinford.day19.App.Op;
import com.benjaminbinford.day19.App.PartRange;
import com.benjaminbinford.day19.App.Range;
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
        final var input = IO.getResource("com/benjaminbinford/day19/testinput.txt");

        final var app = new App(input);

        assertEquals(11, app.getWorkflows().size());

        assertEquals(Optional.of("A"), app.getWorkflows().get("lnx").getConstant());
        assertEquals("qqz", app.getWorkflows().get("in").apply(app.getParts().get(0)));
        assertEquals("qs", app.getWorkflows().get("qqz").apply(app.getParts().get(0)));
        assertEquals("lnx", app.getWorkflows().get("qs").apply(app.getParts().get(0)));
        assertEquals("A", app.getWorkflows().get("lnx").apply(app.getParts().get(0)));

        app.optimizeConstants();

        assertEquals(7540, app.partTotal(app.getParts().get(0)));
        assertEquals(0, app.partTotal(app.getParts().get(1)));
        assertEquals(4623, app.partTotal(app.getParts().get(2)));
        assertEquals(0, app.partTotal(app.getParts().get(3)));
        assertEquals(6951, app.partTotal(app.getParts().get(4)));

        assertEquals(19114l, app.partTotals());

    }

    @Test
    void testOptimizeConstants() {
        final var input = IO.getResource("com/benjaminbinford/day19/testinput.txt");
        final var app = new App(input);

        app.optimizeConstants();

        // System.out
        // .println("\n\n" + app.getWorkflows().entrySet().stream().map(e -> e.getKey()
        // + "{" + e.getValue() + "}")
        // .reduce((a, b) -> a + "\n" + b)
        // .orElse(""));

        assertEquals(8, app.getWorkflows().size());

    }

    @Test
    void testFindCombos() {
        final var input = IO.getResource("com/benjaminbinford/day19/testinput.txt");
        final var app = new App(input);

        app.optimizeConstants();

        assertEquals(167409079868000l, app.partCombinations());

    }

    @ParameterizedTest
    @CsvSource({ ">,5,20,2,5,20,-1,-1",
            ">,5,20,10,11,20,5,10",
            ">,5,20,20,-1,-1,5,20",
            "<,5,20,21,5,20,-1,-1",
            "<,5,20,15,5,14,15,20",
            "<,5,20,3,-1,-1,5,20", })

    void testFindRangeSplits(String op, int rangeMin, int rangeMax, int value, int range1Min, int range2Max,
            int rangeRestMin,
            int rangeRestMax) {

        var rs = Op.of(op).split(new PartRange(Map.of("m", new Range(rangeMin, rangeMax))), "m", value);
        if (range1Min < 0) {
            assertNull(rs[0]);
        } else {
            assertEquals(range1Min, rs[0].ranges().get("m").min());
            assertEquals(range2Max, rs[0].ranges().get("m").max());
        }

        if (rangeRestMin < 0) {
            assertNull(rs[1]);
        } else {
            assertEquals(rangeRestMin, rs[1].ranges().get("m").min());
            assertEquals(rangeRestMax, rs[1].ranges().get("m").max());
        }
    }

}
