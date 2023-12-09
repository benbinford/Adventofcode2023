package com.benjaminbinford.day9;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    int maxLineLength;

    List<int[]> lines;

    public int[][] getWorkingArea() {
        return new int[maxLineLength + 1][maxLineLength + 1];
    }

    public App(String input) {
        this.lines = input.lines().map(this::parseLine).toList();
        this.maxLineLength = this.lines.stream().mapToInt(l -> l.length).max().getAsInt();

    }

    public int[] parseLine(String line) {
        var ns = line.split("\\s+");
        return Arrays.stream(ns).mapToInt(Integer::parseInt).toArray();
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day9/input.txt");

        final var app = new App(input);

        long startTime = System.nanoTime();
        IO.answer(app.extrapolateOasis().sum());
        IO.answer(app.extrapolateOasisBackwards().sum());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime));
    }

    public IntStream extrapolateOasis() {
        return lines.stream().mapToInt(this::extrapolateLine);
    }

    public IntStream extrapolateOasisBackwards() {
        return lines.stream().map(this::reverseArray).mapToInt(this::extrapolateLine);
    }

    public int[] reverseArray(int[] a) {
        int[] b = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            b[a.length - i - 1] = a[i];
        }
        return b;
    }

    public int extrapolateLine(int[] line) {
        int[][] a = getWorkingArea();
        System.arraycopy(line, 0, a[0], 0, line.length);

        boolean found = false;

        int maxRow = 0;
        for (int j = 0; j < line.length; j++) {
            boolean allZeros = true;
            for (int i = 0; i < line.length - j - 1; i++) {
                a[j + 1][i] = a[j][i + 1] - a[j][i];
                if (a[j + 1][i] != 0) {
                    allZeros = false;
                }
            }
            maxRow = j;
            if (allZeros) {
                found = true;
                break;
            }
        }

        assert (found);

        for (int j = maxRow; j >= 0; j--) {
            int lastNumberInRow = a[j][line.length - j - 1];
            int lastNumberInNextRow = a[j + 1][line.length - j - 1];
            a[j][line.length - j] = lastNumberInRow + lastNumberInNextRow;
        }

        return a[0][line.length];
    }
}
