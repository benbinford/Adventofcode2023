package com.benjaminbinford.day23;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import com.benjaminbinford.utils.Dijkstra;
import com.benjaminbinford.utils.IO;
import com.benjaminbinford.utils.IntWeight;

/**
 * Hello world!
 *
 */
public class App {

    enum Dir {
        N, S, E, W;
    }

    record Cell(int x, int y, Dir dir) {

    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day23/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        IO.answer(app.longestHike());

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

        if (vertex.dir() != Dir.S && vertex.y() > 0
                && (maze[vertex.y() - 1][vertex.x()] != '#' && maze[vertex.y() - 1][vertex.x()] != 'v')) {
            add.accept(new Cell(vertex.x(), vertex.y() - 1, Dir.N), edge);
        }
        if (vertex.dir() != Dir.N && vertex.y() < height - 1
                && (maze[vertex.y() + 1][vertex.x()] != '#' && maze[vertex.y() + 1][vertex.x()] != '^')) {
            add.accept(new Cell(vertex.x(), vertex.y() + 1, Dir.S), edge);
        }
        if (vertex.dir() != Dir.W && vertex.x() < width - 1
                && (maze[vertex.y()][vertex.x() + 1] != '#' && maze[vertex.y()][vertex.x() + 1] != '<')) {
            add.accept(new Cell(vertex.x() + 1, vertex.y(), Dir.E), edge);
        }
        if (vertex.dir() != Dir.E && vertex.x() > 0
                && (maze[vertex.y()][vertex.x() - 1] != '#' && maze[vertex.y()][vertex.x() - 1] != '>')) {
            add.accept(new Cell(vertex.x() - 1, vertex.y(), Dir.W), edge);
        }
    }

    Predicate<Cell> goal = v -> v.x == width - 2 && v.y == height - 1;

    public Integer longestHike() {

        var dijkstra = new Dijkstra<Cell, IntWeight>(this::addNeighbors);

        dijkstra.addVertex(new Cell(1, 0, Dir.S), IntWeight.of(0));

        dijkstra.calculate();

        IO.answer("\n\n" + visualize(dijkstra.getPath(goal)));
        return -dijkstra.findMin(goal).weight();
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
