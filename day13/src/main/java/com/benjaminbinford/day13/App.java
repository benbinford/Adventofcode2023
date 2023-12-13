package com.benjaminbinford.day13;

import java.util.Arrays;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    public App(String input) {
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day13/input.txt");

        long startTime = System.nanoTime();

        IO.answer(Arrays.stream(input.split("\n\n")).map(Board::of).mapToInt(Board::reflect).sum());
        IO.answer(Arrays.stream(input.split("\n\n")).map(Board::of).mapToInt(b -> b.reflect(1)).sum());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 100_000));
    }
}
