package com.benjaminbinford.day17;

import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;

import com.benjaminbinford.utils.AdventException;
import com.benjaminbinford.utils.IO;

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

        arena = new java.util.HashMap<>();
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

    int nodeIds = 0;

    Map<Node, Node> arena;

    class Node {
        int x;
        int y;
        int straitLength;
        Dir dir;
        int gScore;
        int fScore;
        int nodeId;
        boolean processed = false;
        Node parent;

        public void setProcessed(boolean processed) {
            this.processed = processed;
        }

        public boolean getProcessed() {
            return processed;
        }

        int hScore() {
            return Math.abs(x + 1 - width) + Math.abs(y + 1 - height);
        }

        void setG(int g) {
            this.gScore = g;
            this.fScore = g + hScore();
        }

        int getF() {
            return fScore;
        }

        int getG() {
            return gScore;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + x;
            result = prime * result + y;
            result = prime * result + straitLength;
            result = prime * result + ((dir == null) ? 0 : dir.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Node other = (Node) obj;
            if (x != other.x)
                return false;
            if (y != other.y)
                return false;
            if (straitLength != other.straitLength)
                return false;
            if (dir != other.dir)
                return false;
            return true;
        }

        Node(int x, int y, int straitLength, Dir dir, int gScoreSoFar) {
            this.x = x;
            this.y = y;
            this.straitLength = straitLength;
            this.dir = dir;
            this.nodeId = nodeIds++;

            setG(gScoreSoFar + entryWeights[y][x]);
        }

        void addChildren(int minStraight, int maxStraight, PriorityQueue<Node> q) {

            RelativeDir[] relativeDirs;
            if (straitLength < minStraight) {
                relativeDirs = STEP_FORWARD;
            } else {
                relativeDirs = STEP_ALL;
            }
            for (RelativeDir relativeDir : relativeDirs) {
                int xDelta = dir.xDelta(relativeDir);
                int yDelta = dir.yDelta(relativeDir);
                Dir newDir = dir.newDir(relativeDir);
                if (x + xDelta >= 0 && x + xDelta < width && y + yDelta >= 0 && y + yDelta < height
                        && straitLength <= maxStraight) {
                    allocNode(this, x + xDelta, y + yDelta,
                            relativeDir.newStraightLength(straitLength), newDir,
                            gScore, q);
                }
            }

        }

        static int fComparison(Node a, Node b) {
            if (a.fScore == b.fScore) {
                return Integer.compare(a.nodeId, b.nodeId);
            }
            return Integer.compare(a.fScore, b.fScore);
        }

        static int gComparison(Node a, Node b) {
            if (a.gScore == b.gScore) {
                return Integer.compare(a.nodeId, b.nodeId);
            }
            return Integer.compare(a.gScore, b.gScore);
        }
    }

    public int heatLoss(int minStraight, int maxStraight) {

        arena.clear();
        nodeIds = 0;

        PriorityQueue<Node> q = new PriorityQueue<>(Node::gComparison);

        q.add(new Node(0, 0, 1, Dir.E, -entryWeights[0][0]));
        q.add(new Node(0, 0, 1, Dir.S, -entryWeights[0][0]));

        while (!q.isEmpty()) {
            Node u = q.poll();
            if (u.processed) {
                continue;
            }

            u.setProcessed(true);
            u.addChildren(minStraight, maxStraight, q);
        }

        var a = new ArrayList<>(arena.keySet().stream()
                .filter(n -> n.x == width - 1 && n.y == height - 1 && n.straitLength >= minStraight)
                .toList());
        a.sort(Node::gComparison);

        // IO.answer("\n" + displayPath(a.get(0)));

        return a.get(0).getG();
    }

    private String displayPath(Node node) {
        StringBuilder sb = new StringBuilder();
        char[][] display = new char[height][width];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                display[j][i] = '.';// Integer.toString(entryWeights[j][i]).charAt(0);
            }
        }
        int gValidation = 0;
        int nodeCount = 0;
        while (node != null) {
            nodeCount++;
            display[node.y][node.x] = node.dir.toChar();
            gValidation += entryWeights[node.y][node.x];
            // IO.answer(String.format("Node %d: %d,%d %s %d %d", node.nodeId, node.x,
            // node.y, node.dir,
            // entryWeights[node.y][node.x], node.getG()));
            node = node.parent;
        }

        // IO.answer("Node validation: " + gValidation);
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                sb.append(display[j][i]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public void allocNode(Node parent, int i, int j, int newStraightLength, Dir dir, int gScore,
            PriorityQueue<Node> q) {
        Node n = new Node(i, j, newStraightLength, dir, gScore);
        n.parent = parent;
        Node existing = arena.computeIfAbsent(n, k -> n);

        if (existing.getG() > n.getG()) {
            existing.setG(n.getG());
            q.remove(existing);
            q.add(existing);
        } else if (n == existing) {
            q.add(existing);
        }

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
