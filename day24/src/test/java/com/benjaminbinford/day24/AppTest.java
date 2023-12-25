package com.benjaminbinford.day24;

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
        final var input = IO.getResource("com/benjaminbinford/day24/testinput.txt");

        final var app = new App(input);

        assertEquals(2, app.countInsideIntersections2D(new App.Vector3(7, 7, 7), new App.Vector3(27, 27, 27)));
    }

    @Test
    void testParsing() {
        var rep = "19, 13, 30 @ -2, 1, -2";

        assertEquals(
                new App.Hailstone(
                        new App.Vector3(19, 13, 30),
                        new App.Vector3(-2, 1, -2)),
                App.Hailstone.of3D(rep));
        assertEquals(
                new App.Hailstone(
                        new App.Vector3(19, 13, 0),
                        new App.Vector3(-2, 1, 0)),
                App.Hailstone.of2D(rep));
    }

    @ParameterizedTest
    @CsvSource({
            "19,13,30,-2,1,-2,  18,19,22, -1,-1,-2, PathInside(14.333'15.333'0.000)",
            "19,13,30,-2,1,-2, 20,25,34,-2,-2,-4, PathInside(11.667'16.667'0.000)",
            "19, 13, 30,-2, 1, -2, 12, 31, 28 , -1, -2, -1, PathOutside(6.200'19.400'0.000)",
            "19, 13, 30, -2, 1, -2, 20, 19, 15, 1, -5, -3,  PathEarlier(A)",
            "18, 19, 22, -1, -1, -2, 20, 25, 34, -2, -2, -4, PathNonIntersecting",
            "18, 19, 22, -1, -1, -2, 12, 31, 28, -1, -2, -1, PathOutside(-6.000'-5.000'0.000)",
            "18, 19, 22,-1, -1, -2, 20, 19, 15,  1, -5, -3,  PathEarlier(BOTH)",
            "20, 25, 34, -2, -2, -4, 12, 31, 28, -1, -2, -1, PathOutside(-2.000'3.000'0.000)",
            "20, 25, 34, -2, -2, -4, 20, 19, 15,1, -5, -3, PathEarlier(B)",
            "12, 31, 28, -1, -2, -1, 20, 19, 15, 1, -5, -3, PathEarlier(BOTH)"

    })
    void testParallel(double px1, double py1, double pz1, double vx1, double vy1, double vz1,
            double px2, double py2, double pz2, double vx2, double vy2, double vz2, String result) {

        var h1 = new App.Hailstone(
                new App.Vector3(px1, py1, pz1),
                new App.Vector3(vx1, vy1, vz1));
        var h2 = new App.Hailstone(
                new App.Vector3(px2, py2, pz2),
                new App.Vector3(vx2, vy2, vz2));

        assertEquals(result, h1.intersects2d(h2, new App.Vector3(7, 7, 7), new App.Vector3(27, 27, 27)).toString());

    }

}
