package com.benjaminbinford.day16;

import java.util.Arrays;
import java.util.stream.IntStream;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    // indexed by (y*height + x)*4 + dir
    // -1 is empty
    int[] nextCell;
    int[] nextCell2;

    int height;
    int width;

    static final char VERTICAL = '|';
    static final char HORIZONTAL = '-';
    static final char SLASH = '/';
    static final char BACKSLASH = '\\';

    int cellIndex(int y, int x, Dir dir) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return -1;
        return (y * width + x) * 4 + dir.ordinal();
    }

    String cellIndexDescription(int cellid) {
        int dir = cellid % 4;
        cellid /= 4;
        int x = cellid % width;
        cellid /= width;
        int y = cellid;
        return String.format("(%d, %d, %s)", x, y, Dir.values()[dir]);
    }

    int getNextCell(int y, int x, Dir dir) {
        return nextCell[cellIndex(y, x, dir)];
    }

    int getNextCell2(int y, int x, Dir dir) {
        return nextCell2[cellIndex(y, x, dir)];
    }

    public App(String input) {
        var grid = input.lines().map(String::toCharArray).toArray(char[][]::new);
        height = grid.length;
        width = grid[0].length;

        nextCell = new int[height * width * 4];
        nextCell2 = new int[height * width * 4];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < height; i++) {
                switch (grid[j][i]) {
                    case VERTICAL:
                        nextCell[cellIndex(j, i, Dir.LEFT)] = cellIndex(j - 1, i, Dir.UP);
                        nextCell2[cellIndex(j, i, Dir.LEFT)] = cellIndex(j + 1, i, Dir.DOWN);
                        nextCell[cellIndex(j, i, Dir.RIGHT)] = cellIndex(j - 1, i, Dir.UP);
                        nextCell2[cellIndex(j, i, Dir.RIGHT)] = cellIndex(j + 1, i, Dir.DOWN);
                        nextCell[cellIndex(j, i, Dir.UP)] = cellIndex(j - 1, i, Dir.UP);
                        nextCell2[cellIndex(j, i, Dir.UP)] = -1;
                        nextCell[cellIndex(j, i, Dir.DOWN)] = cellIndex(j + 1, i, Dir.DOWN);
                        nextCell2[cellIndex(j, i, Dir.DOWN)] = -1;
                        break;
                    case HORIZONTAL:
                        nextCell[cellIndex(j, i, Dir.LEFT)] = cellIndex(j, i - 1, Dir.LEFT);
                        nextCell2[cellIndex(j, i, Dir.LEFT)] = -1;
                        nextCell[cellIndex(j, i, Dir.RIGHT)] = cellIndex(j, i + 1, Dir.RIGHT);
                        nextCell2[cellIndex(j, i, Dir.RIGHT)] = -1;
                        nextCell[cellIndex(j, i, Dir.UP)] = cellIndex(j, i - 1, Dir.LEFT);
                        nextCell2[cellIndex(j, i, Dir.UP)] = cellIndex(j, i + 1, Dir.RIGHT);
                        nextCell[cellIndex(j, i, Dir.DOWN)] = cellIndex(j, i - 1, Dir.LEFT);
                        nextCell2[cellIndex(j, i, Dir.DOWN)] = cellIndex(j, i + 1, Dir.RIGHT);

                        break;

                    case SLASH:
                        nextCell[cellIndex(j, i, Dir.LEFT)] = cellIndex(j + 1, i, Dir.DOWN);
                        nextCell2[cellIndex(j, i, Dir.LEFT)] = -1;
                        nextCell[cellIndex(j, i, Dir.RIGHT)] = cellIndex(j - 1, i, Dir.UP);
                        nextCell2[cellIndex(j, i, Dir.RIGHT)] = -1;
                        nextCell[cellIndex(j, i, Dir.UP)] = cellIndex(j, i + 1, Dir.RIGHT);
                        nextCell2[cellIndex(j, i, Dir.UP)] = -1;
                        nextCell[cellIndex(j, i, Dir.DOWN)] = cellIndex(j, i - 1, Dir.LEFT);
                        nextCell2[cellIndex(j, i, Dir.DOWN)] = -1;

                        break;
                    case BACKSLASH:
                        nextCell[cellIndex(j, i, Dir.LEFT)] = cellIndex(j - 1, i, Dir.UP);
                        nextCell2[cellIndex(j, i, Dir.LEFT)] = -1;
                        nextCell[cellIndex(j, i, Dir.RIGHT)] = cellIndex(j + 1, i, Dir.DOWN);
                        nextCell2[cellIndex(j, i, Dir.RIGHT)] = -1;
                        nextCell[cellIndex(j, i, Dir.UP)] = cellIndex(j, i - 1, Dir.LEFT);
                        nextCell2[cellIndex(j, i, Dir.UP)] = -1;
                        nextCell[cellIndex(j, i, Dir.DOWN)] = cellIndex(j, i + 1, Dir.RIGHT);
                        nextCell2[cellIndex(j, i, Dir.DOWN)] = -1;

                        break;
                    default:
                        nextCell[cellIndex(j, i, Dir.LEFT)] = cellIndex(j, i - 1, Dir.LEFT);
                        nextCell2[cellIndex(j, i, Dir.LEFT)] = -1;
                        nextCell[cellIndex(j, i, Dir.RIGHT)] = cellIndex(j, i + 1, Dir.RIGHT);
                        nextCell2[cellIndex(j, i, Dir.RIGHT)] = -1;
                        nextCell[cellIndex(j, i, Dir.UP)] = cellIndex(j - 1, i, Dir.UP);
                        nextCell2[cellIndex(j, i, Dir.UP)] = -1;
                        nextCell[cellIndex(j, i, Dir.DOWN)] = cellIndex(j + 1, i, Dir.DOWN);
                        nextCell2[cellIndex(j, i, Dir.DOWN)] = -1;
                }

            }
        }

    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day16/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        int energy = app.energize();

        int maxEnergy = app.energizeMaximum();

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(energy);
        IO.answer(maxEnergy);
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }

    enum Dir {
        UP, DOWN, LEFT, RIGHT
    }

    class Energizer {
        boolean[] seenSquares = new boolean[nextCell.length / 4];
        boolean[] seenTiles = new boolean[nextCell.length];

        int energized = 0;

        public boolean everVisited(int cellId) {
            return seenSquares[cellId / 4];
        }

        void reset() {
            Arrays.fill(seenSquares, false);
            Arrays.fill(seenTiles, false);
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++)
                    b.append(everVisited(cellIndex(j, i, Dir.UP)) ? '#' : '.');
                b.append('\n');
            }
            return b.toString();
        }

        public void energize(int cellId) {

            if (cellId < 0) {
                return;
            }
            if (seenTiles[cellId]) {
                return;
            }

            // IO.answer("Visiting " + cellIndexDescription(cellId) + "\n");

            seenTiles[cellId] = true;
            var squareId = cellId / 4;
            if (!seenSquares[squareId]) {
                energized++;
                seenSquares[squareId] = true;
            }

            energize(nextCell[cellId]);
            energize(nextCell2[cellId]);
        }

        public int getEnergy() {
            return energized;
        }
    }

    public int energize() {
        var e = new Energizer();
        e.energize(cellIndex(0, 0, Dir.RIGHT));
        return e.getEnergy();
    }

    public int energizeMaximum() {
        int max = 0;
        max = IntStream.range(0, height).parallel().map(j -> {
            var e = new Energizer();
            e.energize(cellIndex(j, 0, Dir.RIGHT));
            var max2 = e.getEnergy();
            e.reset();
            e.energize(cellIndex(j, width - 1, Dir.LEFT));
            max2 = Math.max(max2, e.getEnergy());
            e.reset();
            return max2;
        }).max().getAsInt();

        var max3 = IntStream.range(0, width).parallel().map(i -> {
            var e = new Energizer();
            e.energize(cellIndex(0, i, Dir.DOWN));
            var max4 = e.getEnergy();
            e.reset();
            e.energize(cellIndex(height - 1, i, Dir.UP));
            max4 = Math.max(max4, e.getEnergy());
            e.reset();
            return max4;
        }).max().getAsInt();
        return Math.max(max, max3);
    }
}
