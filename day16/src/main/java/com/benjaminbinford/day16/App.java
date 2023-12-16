package com.benjaminbinford.day16;

import java.util.Optional;
import java.util.Set;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    char[][] grid;
    int height;
    int width;

    static final char VERTICAL = '|';
    static final char HORIZONTAL = '-';
    static final char SLASH = '/';
    static final char BACKSLASH = '\\';

    public App(String input) {
        grid = input.lines().map(String::toCharArray).toArray(char[][]::new);
        height = grid.length;
        width = grid[0].length;

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
        UP, DOWN, LEFT, RIGHT, SENTINEL
    }

    private static final String CANNOT_ADVANCE_SENTINEL_TILE = "Cannot advance sentinel tile";

    class Tile implements Comparable<Tile> {
        int x;
        int y;
        Dir dir;

        Tile(int x, int y, Dir dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }

        int uniqueId() {
            return y * 1_000_000 + x;
        }

        @Override
        public int compareTo(Tile o) {
            int result = Integer.compare(x, o.x);
            if (result == 0) {
                result = Integer.compare(y, o.y);
                if (result == 0) {
                    result = dir.compareTo(o.dir);
                }
            }
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Tile other = (Tile) obj;
            return x == other.x && y == other.y && dir == other.dir;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(x, y, dir);
        }

        Optional<Tile> advance() {
            return advance(dir);
        }

        Optional<Tile> advance(Dir dir) {
            switch (dir) {
                case UP:
                    if (y == 0)
                        return Optional.empty();
                    else
                        return Optional.of(new Tile(x, y - 1, dir));
                case DOWN:
                    if (y == height - 1)
                        return Optional.empty();
                    else
                        return Optional.of(new Tile(x, y + 1, dir));
                case LEFT:
                    if (x == 0)
                        return Optional.empty();
                    else
                        return Optional.of(new Tile(x - 1, y, dir));
                case RIGHT:
                    if (x == width - 1)
                        return Optional.empty();
                    else
                        return Optional.of(new Tile(x + 1, y, dir));
                default:
                    throw new UnsupportedOperationException(CANNOT_ADVANCE_SENTINEL_TILE);
            }

        }
    }

    interface Path {
        Tile head();
    }

    record LinearPath(Tile head, Optional<Tile> next) implements Path {
    }

    record BranchingPath(Tile head, Optional<Tile> branch1, Optional<Tile> branch2) implements Path {
    }

    record ExploringPath(Tile head) implements Path {
    }

    class Energizer {
        Set<Integer> seenSquares = new java.util.HashSet<>();
        Set<Tile> seenTiles = new java.util.HashSet<>();

        int energized = 0;

        public boolean everVisited(Tile t) {
            return seenSquares.contains(t.uniqueId());
        }

        @Override
        public String toString() {
            StringBuilder b = new StringBuilder();
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++)
                    b.append(everVisited(new Tile(i, j, Dir.SENTINEL)) ? '#' : grid[j][i]);
                b.append('\n');
            }
            return b.toString();
        }

        public int energize(Tile t) {

            if (seenTiles.contains(t)) {
                return energized;
            }

            if (!everVisited(t)) {
                energized++;
            }

            seenTiles.add(t);
            seenSquares.add(t.uniqueId());

            switch (grid[t.y][t.x]) {
                case VERTICAL:
                    if (t.dir == Dir.LEFT || t.dir == Dir.RIGHT) {
                        t.advance(Dir.UP).ifPresent(this::energize);
                        t.advance(Dir.DOWN).ifPresent(this::energize);

                    } else {
                        t.advance().ifPresent(this::energize);
                    }
                    break;
                case HORIZONTAL:
                    if (t.dir == Dir.UP || t.dir == Dir.DOWN) {
                        t.advance(Dir.LEFT).ifPresent(this::energize);
                        t.advance(Dir.RIGHT).ifPresent(this::energize);
                    } else {
                        t.advance().ifPresent(this::energize);
                    }
                    break;
                case SLASH:
                    switch (t.dir) {
                        case UP:
                            t.advance(Dir.RIGHT).ifPresent(this::energize);
                            break;
                        case DOWN:
                            t.advance(Dir.LEFT).ifPresent(this::energize);
                            break;
                        case LEFT:
                            t.advance(Dir.DOWN).ifPresent(this::energize);
                            break;
                        case RIGHT:
                            t.advance(Dir.UP).ifPresent(this::energize);
                            break;
                        case SENTINEL:
                            throw new UnsupportedOperationException(CANNOT_ADVANCE_SENTINEL_TILE);
                    }
                    break;
                case BACKSLASH:
                    switch (t.dir) {
                        case UP:
                            t.advance(Dir.LEFT).ifPresent(this::energize);
                            break;
                        case DOWN:
                            t.advance(Dir.RIGHT).ifPresent(this::energize);
                            break;
                        case LEFT:
                            t.advance(Dir.UP).ifPresent(this::energize);
                            break;
                        case RIGHT:
                            t.advance(Dir.DOWN).ifPresent(this::energize);
                            break;
                        case SENTINEL:
                            throw new UnsupportedOperationException(CANNOT_ADVANCE_SENTINEL_TILE);
                    }
                    break;
                default:
                    t.advance().ifPresent(this::energize);
            }

            return energized;
        }

    }

    public int energize() {
        return new Energizer().energize(new Tile(0, 0, Dir.RIGHT));
    }

    public int energizeMaximum() {
        int max = 0;
        for (var j = 0; j < height; j++) {
            max = Math.max(max, new Energizer().energize(new Tile(0, j, Dir.RIGHT)));
            max = Math.max(max, new Energizer().energize(new Tile(width - 1, j, Dir.LEFT)));
        }
        for (var i = 0; i < width; i++) {
            max = Math.max(max, new Energizer().energize(new Tile(i, 0, Dir.DOWN)));
            max = Math.max(max, new Energizer().energize(new Tile(i, height - 1, Dir.UP)));
        }
        return max;
    }
}