package com.benjaminbinford.day5;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.typemeta.funcj.parser.Input;

import com.benjaminbinford.utils.IO;

/**
 * Unit test for simple App.
 */
class AppTest {

    @Test
    void testMappingParsing() {

        var input = "seed-to-soil map:\n" + //
                "50 98 2\n" + //
                "52 50 48";

        var i = Almanac.mappingRecord.parse(Input.of(input)).getOrThrow();

        assertEquals(
                new Mapping("seed-to-soil",
                        new TreeSet<Conversion>(
                                List.of(new Conversion(50, 98, 100, 2, -48), new Conversion(52, 50, 98, 48, -2)))),
                i);

    }

    @Test
    void testSeedParsing() {

        var input = "seeds: 79 14 55 13";

        var i = Almanac.seedParser.parse(Input.of(input)).getOrThrow();

        assertEquals(List.of(79l, 14l, 55l, 13l), i);

    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testParsing() {
        var input = IO.getResource("com/benjaminbinford/day5/testinput.txt");

        var almanac = Almanac.parse(input);
        assertEquals(List.of(79l, 14l, 55l, 13l), almanac.seeds());
        assertEquals(7, almanac.maps().size());
        assertTrue(true);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "79, 82",
            "14, 43",
            "55, 86",
            "13, 35"
    })
    void testMapLocation(long seed, long expected) {
        var input = IO.getResource("com/benjaminbinford/day5/testinput.txt");

        var almanac = Almanac.parse(input);

        System.out.println(almanac);

        assertEquals(expected, almanac.applyMaps(seed));
    }

    @Test
    void testMinLocation() {
        var input = IO.getResource("com/benjaminbinford/day5/testinput.txt");

        var almanac = Almanac.parse(input);

        assertEquals(35, almanac.minLocation());
    }

    @Test
    void testMinLocationExpanded() {
        var input = IO.getResource("com/benjaminbinford/day5/testinput.txt");

        var almanac = Almanac.parse(input);

        assertEquals(46, almanac.minLocationExpandedSeeds());
    }

}
