package com.benjaminbinford.day11;

import java.util.ArrayList;
import java.util.List;

import org.typemeta.funcj.tuples.Tuple2;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

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

    ArrayList<ArrayList<ObjectType>> image;

    public App(String input) {
        image = getImage(input);
        expandVertically(image);
        expandHorizontally(image);

    }

    public List<Tuple2<Integer, Integer>> getGalaxyCenters() {
        var centers = new ArrayList<Tuple2<Integer, Integer>>();
        for (var i = 0; i < image.size(); i++) {
            var row = image.get(i);
            for (var j = 0; j < row.size(); j++) {
                if (row.get(j) == ObjectType.GALAXY) {
                    centers.add(new Tuple2<>(i, j));
                }
            }
        }
        return centers;
    }

    public List<Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>>> getGalaxyPairs() {
        var pairs = new ArrayList<Tuple2<Tuple2<Integer, Integer>, Tuple2<Integer, Integer>>>();
        var centers = getGalaxyCenters();
        for (var i = 0; i < centers.size(); i++) {
            for (var j = i + 1; j < centers.size(); j++) {
                pairs.add(new Tuple2<>(centers.get(i), centers.get(j)));
            }
        }
        return pairs;
    }

    public int getGalaxyPairDistanceSums() {
        return getGalaxyPairs().stream().mapToInt(p -> cityBlockDistance(p._1, p._2)).sum();
    }

    public int cityBlockDistance(Tuple2<Integer, Integer> a, Tuple2<Integer, Integer> b) {
        return Math.abs(a._1 - b._1) + Math.abs(a._2 - b._2);
    }

    @Override
    public String toString() {
        return image.stream().map(row -> row.stream().map(ObjectType::toChar)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).append('\n').toString())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

    private static void expandVertically(ArrayList<ArrayList<ObjectType>> image) {
        for (var j = image.size() - 1; j >= 0; j--) {
            var row = image.get(j);
            if (row.stream().allMatch(o -> o == ObjectType.EMPTY)) {
                image.add(j, new ArrayList<>(row));
            }
        }
    }

    private static void expandHorizontally(ArrayList<ArrayList<ObjectType>> image) {
        for (var i = image.get(0).size() - 1; i >= 0; i--) {
            var sawGalaxy = false;
            for (var j = 0; j < image.size(); j++) {
                var row = image.get(j);
                if (row.get(i) == ObjectType.GALAXY) {
                    sawGalaxy = true;
                    break;
                }
            }
            if (!sawGalaxy) {
                for (var j = 0; j < image.size(); j++) {
                    image.get(j).add(i, ObjectType.EMPTY);
                }
            }
        }
    }

    private static ArrayList<ArrayList<ObjectType>> getImage(String input) {
        return new ArrayList<>(input.lines()
                .map(l -> new ArrayList<>(l.chars().mapToObj(c -> ObjectType.fromChar((char) c)).toList())).toList());
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day11/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        IO.answer(app.getGalaxyPairDistanceSums());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 100_000));
    }
}
