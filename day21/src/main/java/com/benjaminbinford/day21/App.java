package com.benjaminbinford.day21;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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

    PointStep start;
    Point startPoint;

    record PointStep(int x, int y, int steps) {
    }

    record Point(int x, int y) {

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
                    start = new PointStep(i, j, steps);
                    startPoint = new Point(i, j);
                }
            }
        }

    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day21/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input, 64);

        // IO.answer(String.format("%d", app.availablePlots()));

        IO.answer(String.format("%d", app.availablePlotsInfinite()));

        // final var app2 = new App(input, 26501365);

        // IO.answer(String.format("%d", app2.availablePlotsInfinite()));

        var side = app.plots.length;
        var half = app.start.x;

        app.start = new PointStep(half, half, half);
        app.startPoint = new Point(half, half);

        long f0 = app.availablePlotsInfinite();

        app.start = new PointStep(half, half, half + side);
        long f1 = app.availablePlotsInfinite();

        app.start = new PointStep(half, half, half + 2 * side);
        long f2 = app.availablePlotsInfinite();

        // quadratic interpolation idea courtesy of thomasjevskij and others on reddit
        // # System of equations:
        // # f(0) = a*0**2 + b*0 + c = f0, so c = f0
        // # f(1) = a*1**2 + b*1 + c = f1, so a + b = f1 - f0
        // # f(2) = a*2**2 + b*2 + c = f2, so 4a + 2b = f2 - f0
        // # Gauss elimination gives: 2a = f2 - f0 - 2*(f1 - f0) = f2 - 2f1 + f0
        // # This gives: b = f1 - f0 - a
        long c = f0;
        long a = (f2 - 2l * f1 + f0) / 2l;
        long b = f1 - f0 - a;

        var n = (26501365l - half) / side;

        IO.answer(String.format("%d", a * n * n + b * n + c));

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }

    public int availablePlots() {

        var d = new Dijkstra<PointStep, IntWeight>(this::neighbors);

        d.addVertex(start, new IntWeight(0));
        d.calculate();

        // for (var i = start.steps(); i >= 0; i--) {
        // final var step = i;
        // var ps = d.findMatchingNodes((v, g) -> v.steps() == step);

        // IO.answer(
        // String.format("%d%n%n%s%n%n%n", start.steps() - i, visualize(-10, 20, -10,
        // 20, this.plots, ps, i)));

        // }

        var ps = d.findMatchingNodes((v, g) -> v.steps() == 0);

        IO.answer(String.format("%n%n%s%n%n%n", visualize(-135, 270, -135, 270, this.plots, ps, 0)));

        return d.findMatchingNodes((v, g) -> v.steps() == 0).size();

    }

    Consumer<Point> makeAdder(Set<Point> lastStep, Set<Point> currentStep) {
        return p -> {
            if (!lastStep.contains(p)) {
                currentStep.add(p);
            }
        };
    }

    public int availablePlotsInfinite() {

        var last2Step = new HashSet<Point>();
        var lastStep = new HashSet<Point>();
        var currentStep = new HashSet<Point>();
        var reachablePlots = this.start.steps % 2 == 0 ? 1 : 0;

        int step = 0;
        lastStep.add(startPoint);

        while (step < this.start.steps()) {
            for (var p : lastStep) {
                infiniteNeighbors(p, makeAdder(last2Step, currentStep));
            }
            step++;
            if (step % 2 == this.start.steps() % 2) {
                reachablePlots += currentStep.size();
            }

            var tmp = last2Step;
            last2Step = lastStep;
            lastStep = currentStep;

            currentStep = tmp;
            currentStep.clear();
            // if (step % 1_000 == 0) {
            // IO.answer(step);
            // IO.answer(lastStep.size());
            // }
        }

        return reachablePlots;

    }

    public static int computeModulus(int dividend, int divisor) {
        int modulus = dividend % divisor;
        if (modulus < 0) {
            modulus += divisor;
        }
        return modulus;
    }

    private void infiniteNeighbors(Point v, Consumer<Point> adder) {

        var x = v.x();
        var y = v.y();

        if (plots[computeModulus(y, height)][computeModulus(x - 1, width)] != '#') {
            adder.accept(new Point(x - 1, y));
        }
        if (plots[computeModulus(y, height)][computeModulus(x + 1, width)] != '#') {
            adder.accept(new Point(x + 1, y));
        }
        if (plots[computeModulus(y - 1, height)][computeModulus(x, width)] != '#') {
            adder.accept(new Point(x, y - 1));
        }
        if (plots[computeModulus(y + 1, height)][computeModulus(x, width)] != '#') {
            adder.accept(new Point(x, y + 1));
        }

    }

    private void neighbors(PointStep v, BiConsumer<PointStep, IntWeight> adder) {

        var x = v.x();
        var y = v.y();
        var remainingSteps = v.steps();
        var newSteps = remainingSteps - 1;
        if (newSteps >= 0) {

            if (plots[computeModulus(y, height)][computeModulus(x - 1, width)] != '#') {
                adder.accept(new PointStep(x - 1, y, newSteps), new IntWeight(1));
            }
            if (plots[computeModulus(y, height)][computeModulus(x + 1, width)] != '#') {
                adder.accept(new PointStep(x + 1, y, newSteps), new IntWeight(1));
            }
            if (plots[computeModulus(y - 1, height)][computeModulus(x, width)] != '#') {
                adder.accept(new PointStep(x, y - 1, newSteps), new IntWeight(1));
            }
            if (plots[computeModulus(y + 1, height)][computeModulus(x, width)] != '#') {
                adder.accept(new PointStep(x, y + 1, newSteps), new IntWeight(1));
            }
        }

    }

    private static String visualize(int beginJ, int endJ, int beginI, int endI, char[][] plots, Collection<Point> ps) {
        var height = plots.length;
        var width = plots[0].length;
        var sb = new StringBuilder();
        for (int j = beginJ; j <= endJ; j++) {
            for (int i = beginI; i <= endI; i++) {
                if (ps.contains(new Point(i, j))) {
                    sb.append('O');
                } else {
                    sb.append(plots[computeModulus(j, height)][computeModulus(i, width)]);
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private static String visualize(int beginJ, int endJ, int beginI, int endI, char[][] plots, List<PointStep> ps,
            int steps) {
        var height = plots.length;
        var width = plots[0].length;
        var sb = new StringBuilder();
        for (int j = beginJ; j <= endJ; j++) {
            for (int i = beginI; i <= endI; i++) {
                if (ps.contains(new PointStep(i, j, steps))) {
                    sb.append('O');
                } else {
                    sb.append(plots[computeModulus(j, height)][computeModulus(i, width)]);
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
