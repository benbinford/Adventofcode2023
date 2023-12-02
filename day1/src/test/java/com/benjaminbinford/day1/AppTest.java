package com.benjaminbinford.day1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @Test
    void testCalibrate() {
        App app = new App();
        String input = "1abc2\npqr3stu8vwx\na1b2c3d4e5f\ntreb7uchet";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Number result = app.calibrate(reader, app::calibrateLine);
        assertEquals(142, result.intValue());
    }

    @Test
    void testCalibrateLine2() {
        App app = new App();
        String input = "two1nine\n" + //
                "eightwothree\n" + //
                "abcone2threexyz\n" + //
                "xtwone3four\n" + //
                "4nineeightseven2\n" + //
                "zoneight234\n" + //
                "7pqrstsixteen";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Number result = app.calibrate(reader, app::calibrateLine2);
        assertEquals(281, result.intValue());
    }

    @Test
    void testCalibrateLine() {
        App app = new App();
        String line = "1abc2";
        Number result = app.calibrateLine(line);
        assertEquals(12, result.intValue());
    }

    @ParameterizedTest
    @CsvSource({
            "two1nine, 29",
            "eightwothree, 83",
            "abcone2threexyz, 13",
            "xtwone3four, 24",
            "zoneight234, 14",
            "4nineeightseven2, 42",
            "7pqrstsixteen, 76",
            "pmjjpggvhkrq2, 22",
            "15two6six, 16",
            "46one, 41",
            "five232349, 59",
            "nineninegxknqzpsix28, 98",
            "sixsevenfivefourxf4mzhmkztwonepzt, 61"
    })
    void testCalibrateLine2_1(String line, int expected) {
        App app = new App();
        Number result = app.calibrateLine2(line);
        assertEquals(expected, result.intValue());
    }

}