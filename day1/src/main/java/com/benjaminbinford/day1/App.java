package com.benjaminbinford.day1;

import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.benjaminbinford.utils.IO;

public class App {
    public static void main(String[] args) {

        var lines = IO.getResourceLines("com/benjaminbinford/day1/input.txt");

        var app = new App();
        IO.answer(app.calibrate(lines.stream(), app::calibrateLine));

        IO.answer(app.calibrate(lines.stream(), app::calibrateLine2));

    }

    Number calibrate(Stream<String> lines, Function<? super String, ? extends Number> mapper) {
        return lines.map(mapper)
                .collect(Collectors.summingLong(Number::longValue));

    }

    Number calibrateLine(String line) {
        return calibrateInternal(line, decimalNumbers);

    }

    private static Pattern decimalNumbers = Pattern
            .compile("(\\d)");

    private static Pattern numbers = Pattern
            .compile("(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)|(\\d)");

    int nextNumber(MatchResult m) {
        switch (m.group()) {
            case "one":
                return 1;
            case "two":
                return 2;
            case "three":
                return 3;
            case "four":
                return 4;
            case "five":
                return 5;
            case "six":
                return 6;
            case "seven":
                return 7;
            case "eight":
                return 8;
            case "nine":
                return 9;
            default:
                return Integer.parseInt(m.group());
        }
    }

    private Number calibrateInternal(String line, Pattern pattern) {

        int first = -1;
        int last = -1;

        var matcher = pattern.matcher(line);
        var seq = line;
        while (matcher.find()) {
            if (first == -1) {
                first = nextNumber(matcher.toMatchResult());
            }
            last = nextNumber(matcher.toMatchResult());

            seq = seq.substring(1);
            matcher.reset(seq);
        }

        if (first == -1) {
            return 0;
        } else {
            return (first * 10) + last;
        }
    }

    Number calibrateLine2(String line) {
        return calibrateInternal(line, numbers);
    }
}
