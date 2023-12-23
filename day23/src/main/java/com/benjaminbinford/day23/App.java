package com.benjaminbinford.day23;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import org.typemeta.funcj.tuples.Tuple5;
import org.typemeta.funcj.tuples.Tuple6;

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

    record Cell(int x, int y, Dir dir, Set<Point2> path) {

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

    // dijkstra with -1 weights gives us longest path
    // assuming this is a DAG
    static final IntWeight edge = IntWeight.of(-1);

    void addNeighbors(Cell vertex, BiConsumer<Cell, IntWeight> add) {

        var potentials = List.of(Tuple5.of(Dir.S, Dir.N, vertex.x(), vertex.y() - 1, 'v'),
                Tuple5.of(Dir.N, Dir.S, vertex.x(), vertex.y() + 1, '^'),
                Tuple5.of(Dir.W, Dir.E, vertex.x() + 1, vertex.y(), '<'),
                Tuple5.of(Dir.E, Dir.W, vertex.x() - 1, vertex.y(), '>'));
        processPotentials(vertex, potentials, add);

    }

    private void processPotentials(Cell vertex, List<Tuple5<Dir, Dir, Integer, Integer, Character>> potentials,
            BiConsumer<Cell, IntWeight> add) {

        var actuals = potentials
                .stream().filter(t -> {
                    var badDir = t.get1();
                    var x = t.get3();
                    var y = t.get4();
                    var c = t.get5();
                    var p = new Point2(x, y);

                    return badDir != vertex.dir() &&
                            x >= 0 && x < width && y >= 0 && y < height && maze[y][x] != '#' && maze[y][x] != c
                            && !vertex.path().contains(p);

                }).toList();

        if (actuals.size() == 1) {
            var x = actuals.get(0).get3();
            var y = actuals.get(0).get4();
            var newDir = actuals.get(0).get2();
            add.accept(new Cell(x, y, newDir, vertex.path()), edge);
        } else if (actuals.size() > 1) {
            Point2 p = new Point2(vertex.x(), vertex.y());
            for (var t : actuals) {
                var x = t.get3();
                var y = t.get4();
                var newDir = t.get2();
                var newPath = new HashSet<>(vertex.path());
                newPath.add(p);

                add.accept(new Cell(x, y, newDir, newPath), edge);
            }
        }

    }

    void addNeighborsNoIce(Cell vertex, BiConsumer<Cell, IntWeight> add) {

        var potentials = List.of(Tuple5.of(Dir.S, Dir.N, vertex.x(), vertex.y() - 1, '#'),
                Tuple5.of(Dir.N, Dir.S, vertex.x(), vertex.y() + 1, '#'),
                Tuple5.of(Dir.W, Dir.E, vertex.x() + 1, vertex.y(), '#'),
                Tuple5.of(Dir.E, Dir.W, vertex.x() - 1, vertex.y(), '#'));
        processPotentials(vertex, potentials, add);

    }

    Predicate<Cell> goal = v -> v.x == width - 2 && v.y == height - 1;

    public int longestHike(Neighbors<Cell, IntWeight> neighbors) {

        var dijkstra = new Dijkstra<Cell, IntWeight>(neighbors);

        dijkstra.addVertex(new Cell(1, 0, Dir.S, new HashSet<>()), IntWeight.of(0));

        dijkstra.calculate();

        IO.answer("\n\n" + visualize(dijkstra.getPath(goal)));
        return -dijkstra.findMin(goal).weight();
    }

    public int longestHike() {
        return longestHike(this::addNeighbors);

    }

    public int longestHikeNoIce() {
        return longestHike(this::addNeighborsNoIce);

    }

    private String visualize(List<Cell> path) {
        char[][] mazeCopy = new char[height][width];
        for (int j = 0; j < height; j++) {
            mazeCopy[j] = Arrays.copyOf(maze[j], width);
        }

        for (var cell : path) {
            mazeCopy[cell.y()][cell.x()] = 'O';
        }
        StringBuilder sb = new StringBuilder();
        for (char[] row : mazeCopy) {
            sb.append(row);
            sb.append("\n");
        }
        return sb.toString();
    }
}
