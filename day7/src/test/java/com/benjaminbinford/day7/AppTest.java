package com.benjaminbinford.day7;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Collectors;

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
    void testTotalWinning() {
        var g = Game.of(IO.getResource("com/benjaminbinford/day7/testinput.txt"));
        assertEquals(6440, g.totalWinnings());

        var g2 = Game.ofJokers(IO.getResource("com/benjaminbinford/day7/testinput.txt"));
        System.out.println(g2.hands().stream().map(Hand::toString).collect(Collectors.joining("\n")));
        assertEquals(5905, g2.totalWinnings());
    }
}
