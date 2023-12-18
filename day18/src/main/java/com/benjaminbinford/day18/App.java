package com.benjaminbinford.day18;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    static final String NONE = "none";

    static class State {
        boolean lagoon;
        String color;
        String command;
        long count;

        public State(boolean lagoon, String color) {
            this.lagoon = lagoon;
            this.color = color;
            this.command = NONE;
        }

        public State() {
            this.lagoon = false;
            this.color = NONE;
            this.command = NONE;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getCommand() {
            return command;
        }

        public void inc() {
            count++;
        }

        public long count() {
            return count;
        }
    }

    record Point(int x, int y) {
    }

    Map<Point, State> map = new HashMap<>();
    List<State> states = new ArrayList<>();
    Point upperLeft;
    Point lowerRight;

    enum Dir {
        U(0, -1), D(0, 1), L(-1, 0), R(1, 0);

        private final int xDelta;
        private final int yDelta;

        Dir(int xDelta, int yDelta) {
            this.xDelta = xDelta;
            this.yDelta = yDelta;
        }

        public Point next(Point point) {
            return new Point(point.x + xDelta, point.y + yDelta);
        }

        public Optional<Point> next(Point point, Point upperLeft, Point lowerRight) {
            if (point.x + xDelta < upperLeft.x || point.x + xDelta > lowerRight.x || point.y + yDelta < upperLeft.y
                    || point.y + yDelta > lowerRight.y) {
                return Optional.empty();
            }
            return Optional.of(new Point(point.x + xDelta, point.y + yDelta));
        }
    }

    private State addState(State s) {
        states.add(s);
        return s;
    }

    public long lagoonSize() {
        return states.stream().filter(s -> s.lagoon).mapToLong(s -> s.count).sum();
    }

    public App(String input) {
        this(input, s -> s);
    }

    public App(String input, UnaryOperator<String> translate) {

        int minX = 0;
        int maxX = 0;
        int minY = 0;
        int maxY = 0;
        Point turtle = new Point(0, 0);

        State initialState = addState(new State(true, NONE));
        initialState.inc();
        map.put(turtle, initialState);

        for (var line : input.split("\n")) {
            var parts = translate.apply(line).split(" ");
            var color = parts[2];
            var distance = Integer.parseInt(parts[1]);
            var dir = Dir.valueOf(parts[0]);
            var state = addState(new State(true, color));
            state.setCommand(line);
            for (var i = 0; i < distance; i++) {
                turtle = dir.next(turtle);
                if (!map.containsKey(turtle)) {
                    state.inc();
                }
                map.put(turtle, state);
            }

            minX = Math.min(minX, turtle.x);
            maxX = Math.max(maxX, turtle.x);
            minY = Math.min(minY, turtle.y);
            maxY = Math.max(maxY, turtle.y);
        }

        upperLeft = new Point(minX, minY);
        lowerRight = new Point(maxX, maxY);

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (var j = upperLeft.y; j <= lowerRight.y; j++) {
            for (var i = upperLeft.x; i <= lowerRight.x; i++) {
                var point = new Point(i, j);
                var state = map.get(point);
                if (state == null) {
                    sb.append('?');
                } else if (state.lagoon) {
                    sb.append('#');
                } else {
                    sb.append(".");
                }
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static String translateInstruction(String line) {
        var parts = line.split(" ");
        var color = parts[2].substring(2, parts[2].length() - 2);
        var dir = parts[2].subSequence(parts[2].length() - 2, parts[2].length() - 1);

        var distance = Integer.toString(Integer.parseInt(color, 16));

        return switch (dir.toString()) {
            case "3" -> String.format("U %s (none)", distance);
            case "1" -> String.format("D %s (none)", distance);
            case "2" -> String.format("L %s (none)", distance);
            case "0" -> String.format("R %s (none)", distance);
            default -> throw new IllegalArgumentException("Unexpected value: " + dir);
        };
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day18/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input, App::translateInstruction);
        IO.answer(app.upperLeft);
        IO.answer(app.lowerRight);

        // app.fillLagoon();
        // IO.answer(app.lagoonSize());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }

    public void fillLagoon() {

        var groundPresent = true;
        while (groundPresent) {
            groundPresent = false;
            for (var j = upperLeft.y; j <= lowerRight.y; j++) {
                for (var i = upperLeft.x; i <= lowerRight.x; i++) {
                    var point = new Point(i, j);
                    if (map.containsKey(point)) {
                        continue;
                    }
                    var state = addState(new State(true, NONE));
                    floodFrom(point, state);
                    groundPresent = true;
                }
            }
        }
    }

    private void floodFrom(Point initial, State state) {

        ArrayList<Point> nextPoints = new ArrayList<>();
        nextPoints.add(initial);
        while (!nextPoints.isEmpty()) {
            var point = nextPoints.removeLast();
            if (map.containsKey(point)) {
                continue;
            }
            map.put(point, state);
            state.inc();
            if (point.x <= upperLeft.x || point.x >= lowerRight.x || point.y <= upperLeft.y
                    || point.y >= lowerRight.y) {
                // we touched the edge
                state.lagoon = false;
            }
            for (var dir : Dir.values()) {
                var next = dir.next(point, upperLeft, lowerRight);
                if (next.isEmpty() || map.containsKey(next.get())) {
                    continue;
                }
                nextPoints.add(next.get());
            }
        }

    }
}