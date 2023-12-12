package com.benjaminbinford.day12;

import java.util.List;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    private final List<ConditionRecord> records;

    public App(String input) {
        records = input.lines().map(ConditionRecord::of).toList();
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day12/input.txt");

        long startTime = System.nanoTime();

        final var app = new App(input);
        IO.answer(app.sumArrangements());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 100_000));
    }

    public long sumArrangements() {
        return records.stream().mapToLong(ConditionRecord::findArrangements).sum();
    }
}
