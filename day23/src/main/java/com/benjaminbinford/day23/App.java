package com.benjaminbinford.day23;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.typemeta.funcj.tuples.Tuple2;
import org.typemeta.funcj.tuples.Tuple3;

import com.benjaminbinford.utils.Dijkstra;
import com.benjaminbinford.utils.IO;
import com.benjaminbinford.utils.IntWeight;
import com.benjaminbinford.utils.Neighbors;

/**
 * Hello world!
 *
 */
public class App {

    enum Dir {
        N, S, E, W;
    }

    record Point2(int x, int y) {

    }

    record Cell(int x, int y, boolean vertex) {

    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day23/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        IO.answer(app.longestHike());

        IO.answer(app.longestHikeNoIce());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }

    char[][] maze;
    int width;
    int height;

    public App(String input) {

        var lines = input.split("\n");
        height = lines.length;
        width = lines[0].length();
        maze = new char[height][width];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                maze[j][i] = lines[j].charAt(i);
            }
        }

    }

    record Edge(
            int from,
            int to,
            IntWeight weight) {
    }

    class Graph {
        Map<Cell, Integer> reverseCells;
        Map<Integer, Cell> cells;
        Map<Cell, List<Edge>> edges;
        Set<Cell> vertices;

        public Graph(Neighbors<Cell, IntWeight> neighbors) {
            this.cells = new HashMap<>();
            this.edges = new HashMap<>();
            this.reverseCells = new HashMap<>();
            this.vertices = new HashSet<>();

            // build cells and edges
            buildCells(neighbors);

            buildVertices();

            // for (var c : vertices) {
            // for (var e : getChildren(c)) {
            // System.out.printf("%s.%s -> %s.%s [label=%s]\n", c.x, c.y, e.get1().x,
            // e.get1().y,
            // e.get2().weight());
            // }
            // }

        }

        public boolean isVertex(Cell cell) {
            return cell.vertex();
        }

        public boolean isVertex(int cellId) {
            return isVertex(cells.get(cellId));
        }

        private void buildVertices() {

            ArrayList<Cell> frontier = new ArrayList<>();
            frontier.add(cells.get(0));
            while (!frontier.isEmpty()) {
                var c = frontier.removeLast();
                vertices.add(c);
                for (var child : getChildren(c)) {
                    extendRun(c, frontier, child);
                }
            }

        }

        private void extendRun(Cell c, ArrayList<Cell> frontier, Tuple2<Cell, IntWeight> child) {
            var lastChase = c;
            var chase = child.get1();
            var step = child.get2();

            while (!isVertex(chase)) {
                var nextEdge = getNextEdge(lastChase, chase);
                if (nextEdge == null)
                    return;
                lastChase = chase;
                chase = nextEdge.get1();
                step = step.combineEdge(nextEdge.get2());
            }
            if (!vertices.contains(chase)) {
                frontier.add(chase);
            }
            if (chase != child.get1()) {
                replaceEdge(c, child.get1(), chase, step);
            }
        }

        private Tuple2<Cell, IntWeight> getNextEdge(Cell lastChase, Cell chase) {
            List<Tuple2<Cell, IntWeight>> children = getChildren(chase);
            var nextEdge = children.get(0);
            if (nextEdge.get1() == lastChase) {
                if (children.size() == 1)
                    return null;
                nextEdge = children.get(1);
            }
            return nextEdge;
        }

        private void replaceEdge(Cell from, Cell oldTo, Cell newTo, IntWeight newWeight) {
            int oldToId = reverseCells.get(oldTo);
            int newToId = reverseCells.get(newTo);
            var oldEdge = edges.get(from).stream().filter(e -> e.to() == oldToId).findFirst().orElseThrow();
            edges.get(from).remove(oldEdge);
            edges.get(from).add(new Edge(oldEdge.from(), newToId, newWeight));
        }

        private void buildCells(Neighbors<Cell, IntWeight> neighbors) {
            ArrayList<Cell> frontier = new ArrayList<>();
            frontier.add(maybeAllocCell(new Cell(1, 0, true)));
            while (!frontier.isEmpty()) {
                var c = frontier.removeLast();
                neighbors.addNeighbors(c, (v, w) -> {
                    var child = maybeAllocCell(v);
                    edges.computeIfAbsent(c, n -> new ArrayList<>())
                            .add(new Edge(reverseCells.get(c), reverseCells.get(child), IntWeight.of(-1)));
                    if (child == v)
                        frontier.add(child);
                });
            }
        }

        int cellIds = 0;

        private Cell maybeAllocCell(Cell c) {
            if (reverseCells.containsKey(c)) {
                return cells.get(reverseCells.get(c));
            } else {
                int nextId = cellIds++;
                cells.put(nextId, c);
                reverseCells.put(c, nextId);
                return c;
            }
        }

        public Map<Integer, Cell> getCells() {
            return cells;
        }

        public Map<Cell, List<Edge>> getEdges() {
            return edges;
        }

        public List<Tuple2<Cell, IntWeight>> getChildren(Cell cell) {
            return edges.getOrDefault(cell, Collections.emptyList()).stream()
                    .map(e -> Tuple2.of(cells.get(e.to()), e.weight()))
                    .toList();
        }

        public List<Tuple2<Cell, IntWeight>> getChildren(int cellId) {
            return getChildren(cells.get(cellId));
        }
    }

    Predicate<CellWithMemory> goal = v -> v.cell.x == width - 2 && v.cell.y == height - 1;

    record CellWithMemory(Cell cell, BitSet paths) {

    }

    public int longestHike(Neighbors<Cell, IntWeight> neighbors) {

        Graph g = buildGraph(neighbors);

        var dijkstra = new Dijkstra<CellWithMemory, IntWeight>(
                (n, adder) -> g.getChildren(n.cell).forEach(c -> {

                    var childId = g.reverseCells.get(c.get1());
                    if (n.paths.get(childId))
                        return;

                    var bitset = (BitSet) n.paths.clone();
                    bitset.set(childId);

                    adder.accept(new CellWithMemory(c.get1(), bitset), c.get2());
                })

        );

        var start = g.getCells().get(0);
        var startBitset = new BitSet();
        startBitset.set(g.reverseCells.get(start));
        dijkstra.addVertex(new CellWithMemory(start, startBitset), IntWeight.of(0));

        dijkstra.calculate();

        IO.answer("\n\n" + visualize(dijkstra.getPath(goal)));
        return -dijkstra.findMin(goal).weight();
    }

    List<Tuple3<Integer, Integer, Character>> getNeighbors(int x, int y) {
        return List.of(Tuple3.of(x, y - 1, 'v'),
                Tuple3.of(x, y + 1, '^'),
                Tuple3.of(x + 1, y, '<'),
                Tuple3.of(x - 1, y, '>')).stream().filter(t -> validSquare(t.get1(), t.get2())).toList();
    }

    List<Tuple3<Integer, Integer, Character>> getNeighborsNoIce(int x, int y) {
        return List.of(Tuple3.of(x, y - 1, '#'),
                Tuple3.of(x, y + 1, '#'),
                Tuple3.of(x + 1, y, '#'),
                Tuple3.of(x - 1, y, '#')).stream().filter(t -> validSquare(t.get1(), t.get2())).toList();
    }

    void addNeighbors(Cell vertex, BiConsumer<Cell, IntWeight> add) {

        processPotentials(getNeighbors(vertex.x, vertex.y), add);

    }

    void addNeighborsNoIce(Cell vertex, BiConsumer<Cell, IntWeight> add) {

        processPotentials(getNeighborsNoIce(vertex.x, vertex.y), add);

    }

    // dijkstra with -1 weights gives us longest path
    // assuming this is a DAG
    static final IntWeight edge = IntWeight.of(-1);

    private void processPotentials(List<Tuple3<Integer, Integer, Character>> potentials,
            BiConsumer<Cell, IntWeight> add) {

        var actuals = potentials.stream().filter(t -> {
            var x = t.get1();
            var y = t.get2();
            var c = t.get3();
            return maze[y][x] != c;

        }).toList();

        for (var t : actuals) {
            var x = t.get1();
            var y = t.get2();

            add.accept(new Cell(x, y, getNeighborsNoIce(x, y).size() != 2), edge);
        }

    }

    private boolean validSquare(Integer x, Integer y) {
        return x >= 0 && x < width && y >= 0 && y < height && maze[y][x] != '#';
    }

    Graph buildGraph(Neighbors<Cell, IntWeight> neighbors) {
        return new Graph(neighbors);
    }

    public int longestHike() {
        return longestHike(this::addNeighbors);

    }

    public int longestHikeNoIce() {
        return longestHike(this::addNeighborsNoIce);

    }

    private String visualize(List<CellWithMemory> path) {
        char[][] mazeCopy = new char[height][width];
        for (int j = 0; j < height; j++) {
            mazeCopy[j] = Arrays.copyOf(maze[j], width);
        }

        for (var cell : path) {
            mazeCopy[cell.cell.y()][cell.cell.x()] = 'O';
        }
        StringBuilder sb = new StringBuilder();
        for (char[] row : mazeCopy) {
            sb.append(row);
            sb.append("\n");
        }
        return sb.toString();
    }
}
