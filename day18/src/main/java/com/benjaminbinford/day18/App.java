package com.benjaminbinford.day18;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.UnaryOperator;
import java.util.stream.LongStream;

import com.benjaminbinford.utils.AdventException;
import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    record Line(long start, long end) implements Comparable<Line> {
        Line(long start, long end) {
            if (start > end) {
                this.end = start;
                this.start = end;
            } else {
                this.start = start;
                this.end = end;
            }
        }

        @Override
        public int compareTo(Line o) {
            return Long.compare(start, o.start);
        }

        public long length() {
            return end - start + 1;
        }
    }

    record Point(long x, long y) {

        boolean onHorizontalLine(SortedSet<Line> lines) {
            return lines.stream().anyMatch(l -> l.start <= x && x <= l.end);
        }

        boolean onVerticalLine(SortedSet<Line> lines) {
            return lines.stream().anyMatch(l -> l.start <= y && y <= l.end);
        }
    }

    enum Bend {
        UL, UR, DL, DR;

        public char toChar() {
            return switch (this) {
                case UL -> '╗'; // never prior
                case UR -> '╔';

                case DL -> '╝'; // never prio
                case DR -> '╚';
            };
        }

        public boolean cutting(Bend b) {
            return switch (this) {
                case UL -> b == DR;
                case UR -> b == DL;
                case DL -> b == UR;
                case DR -> b == UL;
            };
        }
    }

    SortedMap<Long, SortedSet<Line>> verticalLines;
    SortedMap<Long, SortedSet<Line>> horizontalLines;
    Map<Point, Bend> intersections;

    Point upperLeft;
    Point lowerRight;

    enum Dir {
        U, D, L, R;

        public boolean isVertical() {
            return this == U || this == D;
        }

        public long inc() {
            return this == D || this == R ? 1 : -1;
        }
    }

    enum State {
        INSIDE, OUTSIDE, ONLINE_FROM_OUTSIDE, ONLINE_FROM_INSIDE;

        State toggle() {
            return this == INSIDE ? OUTSIDE : INSIDE;
        }
    }

    public long lagoonSize() {

        long interior = LongStream.rangeClosed(upperLeft.y, lowerRight.y).parallel().map(j -> {
            long sum = 0l;
            var state = State.OUTSIDE;
            var x = 0l;
            Bend entryBend = null;
            for (var lines : verticalLines.entrySet()) {
                var p = new Point(0, j);
                if (p.onVerticalLine(lines.getValue())) {
                    State newState;
                    if (intersections.containsKey(new Point(lines.getKey(), j))) {
                        var bend = intersections.get(new Point(lines.getKey(), j));
                        if (bend == Bend.DR || bend == Bend.UR) {
                            if (state == State.INSIDE) {
                                newState = State.ONLINE_FROM_INSIDE;
                            } else {
                                newState = State.ONLINE_FROM_OUTSIDE;
                            }
                            entryBend = bend;
                        } else if (bend == Bend.UL || bend == Bend.DL) {
                            assert (entryBend != null);

                            if (state == State.ONLINE_FROM_INSIDE) {
                                newState = entryBend.cutting(bend) ? State.OUTSIDE : State.INSIDE;
                            } else {
                                newState = entryBend.cutting(bend) ? State.INSIDE : State.OUTSIDE;
                            }
                            entryBend = null;
                        } else {
                            throw new AdventException("Unknown Bend");
                        }
                    }

                    else {
                        newState = state.toggle();

                    }

                    if (newState == State.INSIDE) {
                        x = lines.getKey() + 1;
                    } else if (newState == State.OUTSIDE || newState == State.ONLINE_FROM_INSIDE) {
                        if (state != State.ONLINE_FROM_OUTSIDE && state != State.ONLINE_FROM_INSIDE) {
                            var amt = lines.getKey() - x;

                            sum += amt;
                        }
                    }

                    state = newState;

                }

            }
            if (j % 1_000_000 == 0) {
                IO.answer(j);
            }
            return sum;
        }).sum();

        IO.answer(String.format("interior %d to sum", interior));

        long hlinesLength = horizontalLines.values().stream().flatMap(l -> l.stream()).mapToLong(Line::length).sum();
        IO.answer(String.format("hlinesLength %d to sum", hlinesLength));
        long vlinesLength = verticalLines.values().stream().flatMap(l -> l.stream()).mapToLong(Line::length).sum();
        IO.answer(String.format("vlinesLength %d to sum", vlinesLength));
        long vertices = intersections.size();
        IO.answer(String.format("vertices %d to sum", vertices / 2));
        return interior + hlinesLength + vlinesLength - vertices / 2;
    }

    public App(String input) {
        this(input, s -> s);
    }

    record Command(Dir dir, long distance) {
        public static Command of(String s) {
            var parts = s.split(" ");
            return new Command(Dir.valueOf(parts[0]), Long.parseLong(parts[1]));
        }
    }

    public App(String input, UnaryOperator<String> translate) {

        verticalLines = new TreeMap<>();
        horizontalLines = new TreeMap<>();
        intersections = new HashMap<>();

        long turtleX = 0;
        long turtleY = 0;

        var lineReps = input.split("\n");
        var lines = Arrays.stream(lineReps).map(translate).map(Command::of).toArray(Command[]::new);

        Dir finalLineDir = lines[lines.length - 1].dir();
        Dir firstLineDir = lines[0].dir();

        for (var i = 0; i < lines.length; i++) {
            var line = lines[i];

            var priorLineDir = i == 0 ? finalLineDir : lines[i - 1].dir();
            var nextLineDir = i == lines.length - 1 ? firstLineDir
                    : lines[i + 1].dir();

            if (line.dir().isVertical()) {
                var lineSet = verticalLines.computeIfAbsent(turtleX, k -> new TreeSet<>());
                long inc = line.dir().inc();
                var l = new Line(turtleY, turtleY + inc * line.distance());
                lineSet.add(l);
                if (turtleY == l.start) {
                    intersections.put(new Point(turtleX, l.start), priorLineDir == Dir.L ? Bend.UR : Bend.UL);
                    intersections.put(new Point(turtleX, l.end), nextLineDir == Dir.R ? Bend.DR : Bend.DL);

                    turtleY = l.end;
                } else {
                    intersections.put(new Point(turtleX, l.end), priorLineDir == Dir.L ? Bend.DR : Bend.DL);
                    intersections.put(new Point(turtleX, l.start), nextLineDir == Dir.R ? Bend.UR : Bend.UL);
                    turtleY = l.start;
                }

            } else {
                var lineSet = horizontalLines.computeIfAbsent(turtleY, k -> new TreeSet<>());
                long inc = line.dir().inc();
                var l = new Line(turtleX + inc, turtleX + inc * line.distance());
                lineSet.add(l);
                if (turtleX + inc == l.start) {
                    turtleX = l.end;
                } else {
                    turtleX = l.start;
                }
            }

        }
        upperLeft = new Point(verticalLines.firstKey(), horizontalLines.firstKey());
        lowerRight = new Point(verticalLines.lastKey(), horizontalLines.lastKey());
    }

    public String visualize() {
        StringBuilder sb = new StringBuilder();
        for (var j = upperLeft.y; j <= lowerRight.y; j++) {
            for (var i = upperLeft.x; i <= lowerRight.x; i++) {
                var point = new Point(i, j);
                SortedSet<Line> verts = verticalLines.get(point.x);
                SortedSet<Line> horzs = horizontalLines.get(point.y);
                if (intersections.containsKey(point)) {
                    sb.append(intersections.get(point).toChar());

                } else if (verts != null && point.onVerticalLine(verts)) {
                    sb.append('|');

                } else if (horzs != null && point.onHorizontalLine(horzs)) {
                    sb.append('-');

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
        final var app1 = new App(input);

        IO.answer(app1.lagoonSize());

        final var app = new App(input, App::translateInstruction);

        IO.answer(app.lagoonSize());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }

}