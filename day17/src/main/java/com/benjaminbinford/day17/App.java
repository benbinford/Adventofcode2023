package com.benjaminbinford.day17;

import java.util.List;
import java.util.function.Predicate;

import com.benjaminbinford.utils.AdventException;
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
    int[][] entryWeights;

    public App(String input) {

        var lines = input.split("\n");
        height = lines.length;
        width = lines[0].length();
        entryWeights = new int[height][width];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                entryWeights[j][i] = Integer.parseInt(lines[j].charAt(i) + "");
            }
        }

    }

    enum Dir {
        N, S, E, W;

        private AdventException invalid(Object o) {
            return new AdventException("Invalid direction: " + o.toString());
        }

        public char toChar() {
            switch (this) {
                case N:
                    return '^';
                case S:
                    return 'v';
                case E:
                    return '>';
                case W:
                    return '<';
                default:
                    throw invalid(this);
            }
        }

        public int xDelta(RelativeDir dir) {
            switch (this) {
                case N:
                    switch (dir) {
                        case L:
                            return -1;
                        case R:
                            return 1;
                        case F:
                            return 0;
                        default:
                            throw invalid(dir);
                    }
                case S:
                    switch (dir) {
                        case L:
                            return 1;
                        case R:
                            return -1;
                        case F:
                            return 0;
                        default:
                            throw invalid(dir);
                    }
                case E:
                    switch (dir) {
                        case L:
                            return 0;
                        case R:
                            return 0;
                        case F:
                            return 1;
                        default:
                            throw invalid(dir);
                    }
                case W:
                    switch (dir) {
                        case L:
                            return 0;
                        case R:
                            return 0;
                        case F:
                            return -1;
                        default:
                            throw invalid(dir);
                    }
                default:
                    throw invalid(dir);
            }
        }

        public int yDelta(RelativeDir dir) {
            switch (this) {
                case N:
                    switch (dir) {
                        case L:
                            return 0;
                        case R:
                            return 0;
                        case F:
                            return -1;
                        default:
                            throw invalid(dir);
                    }
                case S:
                    switch (dir) {
                        case L:
                            return 0;
                        case R:
                            return 0;
                        case F:
                            return 1;
                        default:
                            throw invalid(dir);
                    }
                case E:
                    switch (dir) {
                        case L:
                            return -1;
                        case R:
                            return 1;
                        case F:
                            return 0;
                        default:
                            throw invalid(dir);
                    }
                case W:
                    switch (dir) {
                        case L:
                            return 1;
                        case R:
                            return -1;
                        case F:
                            return 0;
                        default:
                            throw invalid(dir);
                    }
                default:
                    throw invalid(dir);
            }
        }

        public Dir newDir(RelativeDir dir) {
            switch (this) {
                case N:
                    switch (dir) {
                        case L:
                            return W;
                        case R:
                            return E;
                        case F:
                            return N;
                        default:
                            throw invalid(dir);
                    }
                case S:
                    switch (dir) {
                        case L:
                            return E;
                        case R:
                            return W;
                        case F:
                            return S;
                        default:
                            throw invalid(dir);
                    }
                case E:
                    switch (dir) {
                        case L:
                            return N;
                        case R:
                            return S;
                        case F:
                            return E;
                        default:
                            throw invalid(dir);
                    }
                case W:
                    switch (dir) {
                        case L:
                            return S;
                        case R:
                            return N;
                        case F:
                            return W;
                        default:
                            throw invalid(dir);
                    }
                default:
                    throw invalid(dir);
            }
        }
    }

    enum RelativeDir {
        L, R, F;

        public int newStraightLength(int straitLength) {
            int newStraightLength;
            if (this == RelativeDir.F) {
                newStraightLength = straitLength + 1;
            } else {
                newStraightLength = 1;
            }

            return newStraightLength;
        }
    }

    private static final RelativeDir[] STEP_ALL = new RelativeDir[] { RelativeDir.L, RelativeDir.R, RelativeDir.F };
    private static final RelativeDir[] STEP_FORWARD = new RelativeDir[] { RelativeDir.F };

    record CrucibleVertex(int x, int y, int straitLength, Dir dir) {
    }

    private String displayPath(List<CrucibleVertex> path) {
        StringBuilder sb = new StringBuilder();
        char[][] display = new char[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                display[j][i] = '.';
            }
        }

        for (var node : path) {
            display[node.y][node.x] = node.dir.toChar();
        }

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                sb.append(display[j][i]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public int heatLoss(int minStraight, int maxStraight) {

        var dijkstra = new Dijkstra<CrucibleVertex, IntWeight>((v, add) -> {

            RelativeDir[] relativeDirs;
            if (v.straitLength < minStraight) {
                relativeDirs = STEP_FORWARD;
            } else {
                relativeDirs = STEP_ALL;
            }
            for (RelativeDir relativeDir : relativeDirs) {
                int xDelta = v.dir.xDelta(relativeDir);
                int yDelta = v.dir.yDelta(relativeDir);
                Dir newDir = v.dir.newDir(relativeDir);
                var newX = v.x + xDelta;
                var newY = v.y + yDelta;
                var newStraightLength = relativeDir.newStraightLength(v.straitLength);
                if (newX >= 0 && newX < width && newY >= 0 && newY < height
                        && newStraightLength <= maxStraight) {
                    add.accept(new CrucibleVertex(newX, newY, newStraightLength, newDir),
                            new IntWeight(entryWeights[newY][newX]));
                }
            }

        });

        dijkstra.addVertex(new CrucibleVertex(0, 0, 1, Dir.E), new IntWeight(0));
        dijkstra.addVertex(new CrucibleVertex(0, 0, 1, Dir.S), new IntWeight(0));

        dijkstra.calculate();

        Predicate<CrucibleVertex> goal = n -> n.x == width - 1 && n.y == height - 1 && n.straitLength >= minStraight;

        IO.answer(displayPath(dijkstra.getPath(goal)));

        return dijkstra.findMin(goal).weight();

    }

    public static void main(String[] args) {
        // 1063
        // 1003
        // 1123
        // ***1178
        final var input = IO.getResource("com/benjaminbinford/day17/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        IO.answer(app.heatLoss(1, 3));
        IO.answer(app.heatLoss(4, 10));

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }
}
