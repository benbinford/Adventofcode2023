package com.benjaminbinford.day11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import org.typemeta.funcj.tuples.Tuple2;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    static class Galaxy {
        private long row;
        private long col;
        private ObjectType type;
        private int size;

        public Galaxy(long row, long col, ObjectType type) {
            this.row = row;
            this.col = col;
            this.type = type;
            this.size = 1;
        }

        public long getRow() {
            return row;
        }

        public void setRow(long row) {
            this.row = row;
        }

        public long getSize() {
            return size;
        }

        public long getCol() {
            return col;
        }

        public void setCol(long col) {
            this.col = col;
        }

        public ObjectType getType() {
            return type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Galaxy galaxy = (Galaxy) o;
            return row == galaxy.row && col == galaxy.col && type == galaxy.type;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col, type);
        }

        @Override
        public String toString() {
            if (type == ObjectType.GALAXY) {
                return "#";
            } else {
                return ".".repeat(size);
            }
        }

        public void setSize(int expansion) {
            this.size = expansion;
        }
    }

    enum ObjectType {
        EMPTY,
        GALAXY;

        static ObjectType fromChar(char c) {
            switch (c) {
                case '.':
                    return EMPTY;
                case '#':
                    return GALAXY;
                default:
                    throw new IllegalArgumentException("Invalid object type: " + c);
            }
        }

        char toChar() {
            if (this == ObjectType.EMPTY) {
                return '.';
            } else if (this == ObjectType.GALAXY) {
                return '#';
            }
            throw new IllegalArgumentException("Invalid object type: " + this);
        }
    }

    ArrayList<ArrayList<Galaxy>> image;

    public App(String input, int expansion) {
        image = getImage(input, expansion);

    }

    public List<Galaxy> getGalaxyCenters() {

        var centers = new ArrayList<Galaxy>();
        for (var i = 0; i < image.size(); i++) {
            var row = image.get(i);
            for (var j = 0; j < row.size(); j++) {
                var galaxy = row.get(j);
                if (galaxy.getType() == ObjectType.GALAXY) {
                    centers.add(galaxy);
                }
            }
        }
        return centers;
    }

    public List<Tuple2<Galaxy, Galaxy>> getGalaxyPairs() {
        var pairs = new ArrayList<Tuple2<Galaxy, Galaxy>>();
        var centers = getGalaxyCenters();
        for (var i = 0; i < centers.size(); i++) {
            for (var j = i + 1; j < centers.size(); j++) {
                pairs.add(new Tuple2<>(centers.get(i), centers.get(j)));
            }
        }
        return pairs;
    }

    public long getGalaxyPairDistanceSums() {
        return getGalaxyPairs().stream().mapToLong(p -> cityBlockDistance(p._1, p._2)).sum();
    }

    public long cityBlockDistance(Galaxy a, Galaxy b) {
        return Math.abs(a.getCol() - b.getCol()) + Math.abs(a.getRow() - b.getRow());
    }

    @Override
    public String toString() {
        return image.stream().map(row -> row.stream().map(Galaxy::toString)
                .collect(StringBuilder::new, StringBuilder::append,
                        StringBuilder::append)
                .append('\n').toString())
                .collect(StringBuilder::new, StringBuilder::append,
                        StringBuilder::append)
                .toString();
    }

    private static ArrayList<ArrayList<Galaxy>> getImage(String input, int expansion) {

        long row = 0l;
        var image = new ArrayList<ArrayList<Galaxy>>();
        var sawGalaxyInColumn = new HashSet<Integer>();
        var maxGalaxyColumn = 0l;
        for (var line : (Iterable<String>) input.lines()::iterator) {
            var col = 0;
            var galaxyRow = new ArrayList<Galaxy>();
            var sawGalaxy = false;
            image.add(galaxyRow);
            for (var c : (Iterable<Integer>) line.chars()::iterator) {
                if (c == '#') {
                    galaxyRow.add(new Galaxy(row, col, ObjectType.GALAXY));
                    sawGalaxy = true;
                    sawGalaxyInColumn.add(col);
                    maxGalaxyColumn = Math.max(maxGalaxyColumn, col);
                } else {
                    galaxyRow.add(new Galaxy(row, col, ObjectType.EMPTY));
                }
                col++;
            }
            if (sawGalaxy) {
                row++;
            } else {
                row += expansion;
            }
        }

        for (var galaxyRow : image) {
            for (var i = 0; i <= maxGalaxyColumn; i++) {
                if (!sawGalaxyInColumn.contains(i)) {
                    galaxyRow.get(i).setSize(expansion);
                    for (var j = i + 1; j <= maxGalaxyColumn; j++) {
                        galaxyRow.get(j).setCol(galaxyRow.get(j).getCol() + expansion - 1);
                    }
                }
            }
        }
        return image;
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day11/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input, 2);

        IO.answer(app.getGalaxyPairDistanceSums());

        final var big = new App(input, 1_000_000);

        IO.answer(big.getGalaxyPairDistanceSums());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 100_000));
    }
}
