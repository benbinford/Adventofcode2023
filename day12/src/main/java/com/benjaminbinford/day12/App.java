package com.benjaminbinford.day12;

import java.util.List;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    private final List<String> lineRecords;

    public App(String input) {
        lineRecords = input.lines().toList();
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day12/input.txt");

        long startTime = System.nanoTime();

        final var app = new App(input);
        IO.answer(app.sumArrangements());
        IO.answer(app.sumExplodedArrangements());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 100_000));
    }

    public long sumExplodedArrangements() {
        return lineRecords.parallelStream().mapToLong(ConditionRecord::findExplodedArrangements).sum();
    }

    public long sumArrangements() {
        return lineRecords.stream().map(ConditionRecord::of).mapToLong(ConditionRecord::findArrangements).sum();
    }

    public Long sumExplodedArrangementsLong() {
        return lineRecords.stream().map(ConditionRecord::explode)
                .mapToLong(ConditionRecord::findArrangements).sum();
    }
}
