package com.benjaminbinford.day21;

import java.util.List;
import java.util.function.BiConsumer;

import com.benjaminbinford.utils.Dijkstra;
import com.benjaminbinford.utils.IO;
import com.benjaminbinford.utils.IntWeight;

/**
 * Hello world!
 *
 */
public class App {

    int height;
    int width;
    char[][] plots;

    Point start;

    record Point(int x, int y, int steps) {
    }

    public App(String input, int steps) {

        var lines = input.split("\n");
        height = lines.length;
        width = lines[0].length();
        plots = new char[height][width];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                plots[j][i] = lines[j].charAt(i);
                if (plots[j][i] == 'S') {
                    start = new Point(i, j, steps);
                }
            }
        }

    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day21/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input, 63);

        IO.answer(String.format("%d", app.availablePlots()));
        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }

    public int availablePlots() {

        var d = new Dijkstra<Point, IntWeight>(this::neighbors);

        d.addVertex(start, new IntWeight(0));
        d.calculate();

        // if (false) {
        for (var i = start.steps(); i >= 0; i--) {
            final var step = i;
            var ps = d.findMatchingNodes((v, g) -> v.steps() == step);

            IO.answer(String.format("%d%n%n%s%n%n%n", start.steps() - i, visualize(this.plots, ps, i)));

        }
        // }

        // var ps = d.findMatchingNodes((v, g) -> v.steps() == 0);

        // IO.answer(String.format("%n%n%s%n%n%n", visualize(this.plots, ps, 0)));

        return d.findMatchingNodes((v, g) -> v.steps() == 0).size();

    }

    private void neighbors(Point v, BiConsumer<Point, IntWeight> adder) {

        var x = v.x();
        var y = v.y();
        var remainingSteps = v.steps();
        var newSteps = remainingSteps - 1;
        if (newSteps >= 0) {

            if (x > 0 && plots[y][x - 1] != '#') {
                adder.accept(new Point(x - 1, y, newSteps), new IntWeight(1));
            }
            if (x < width - 1 && plots[y][x + 1] != '#') {
                adder.accept(new Point(x + 1, y, newSteps), new IntWeight(1));
            }
            if (y > 0 && plots[y - 1][x] != '#') {
                adder.accept(new Point(x, y - 1, newSteps), new IntWeight(1));
            }
            if (y < height - 1 && plots[y + 1][x] != '#') {
                adder.accept(new Point(x, y + 1, newSteps), new IntWeight(1));
            }
        }

    }

    private static String visualize(char[][] plots, List<Point> ps, int steps) {
        var height = plots.length;
        var width = plots[0].length;
        var sb = new StringBuilder();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if (ps.contains(new Point(i, j, steps))) {
                    sb.append('O');
                } else {
                    sb.append(plots[j][i]);
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
