package com.benjaminbinford.day6;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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
    void testRacesParser() {
        // Time: 7 15 30
        // Distance: 9 40 200
        Races rs = Races.parse(IO.getResource("com/benjaminbinford/day6/testinput.txt"));

        assertEquals(new Races(List.of(new Race(7, 9), new Race(15, 40), new Race(30, 200))), rs);

        assertEquals(288, rs.multiplyWins());

    }

    @Test
    void testRacesParser2() {
        // Time: 7 15 30
        // Distance: 9 40 200
        Races rs = Races.parse(IO.getResource("com/benjaminbinford/day6/testinput2.txt"));

        assertEquals(71503l, rs.multiplyWins());

    }
}
