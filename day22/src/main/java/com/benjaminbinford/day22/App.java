package com.benjaminbinford.day22;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.benjaminbinford.utils.AdventException;
import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    record Point2(int x, int y) {
    }

    record Point3(int x, int y, int z) {

        public static Point3 of(String string) {
            var coords = string.split(",");
            return new Point3(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
        }

        Point3 setZ(int z) {
            return new Point3(this.x, this.y, z);
        }

        @Override
        public String toString() {
            return String.format("%d,%d,%d", x, y, z);
        }
    }

    static class Brick {
        Point3 p1;
        Point3 p2;
        String id;
        Set<Brick> supportedBy = new HashSet<>();
        Set<Brick> supports = new HashSet<>();

        public Brick(Point3 p1, Point3 p2, Optional<String> id) {
            this.p1 = p1;
            this.p2 = p2;
            this.id = id.orElse(UUID.randomUUID().toString());
        }

        static Brick of(String rep) {
            var parts = rep.split("\\s*<-\\s*");
            var id = parts.length == 2 ? Optional.of(parts[1]) : Optional.<String>empty();
            var points = parts[0].split("~");
            var p1 = Point3.of(points[0]);
            var p2 = Point3.of(points[1]);
            return new Brick(p1, p2, id);
        }

        public Stream<Point2> bottomPoints() {
            if (p1.x() == p2.x() && p1.y() == p2.y())
                return Stream.of(new Point2(p1.x(), p1.y()));
            else if (p1.x() == p2.x() && p1.z() == p2.z())
                return IntStream.rangeClosed(p1.y(), p2.y()).mapToObj(y -> new Point2(p1.x(), y));
            else if (p1.y() == p2.y() && p1.z() == p2.z())
                return IntStream.rangeClosed(p1.x(), p2.x()).mapToObj(x -> new Point2(x, p1.y()));
            else {
                throw new AdventException("Brick is diagonal");
            }
        }

        public int highestZ() {
            return Math.max(p1.z(), p2.z());
        }

        public int lowestZ() {
            return Math.min(p1.z(), p2.z());
        }

        public void setZ(int newZ) {
            int delta = p1.z() - p2.z();
            if (delta == 0) {
                p1 = p1.setZ(newZ);
                p2 = p2.setZ(newZ);
            } else if (delta > 0) {
                p2 = p2.setZ(newZ);
                p1 = p1.setZ(newZ - delta);
            } else if (delta < 0) {
                p1 = p1.setZ(newZ);
                p2 = p2.setZ(newZ - delta);
            }
        }

        public void addSupportedBy(Brick brick) {
            supportedBy.add(brick);
        }

        public void addSupports(Brick b) {
            supports.add(b);
        }

        public boolean disintegratable() {
            return supports.isEmpty() || supports.stream().allMatch(b -> b.supportedBy.size() > 1);
        }

        public Set<Brick> getSupportedBy() {

            return supportedBy;
        }

        public Set<Brick> getSupports() {
            return supports;
        }

        @Override
        public String toString() {
            return String.format("%s~%s    <- %s", p1, p2, id);
        }

        public int chainReaction() {
            return 0;
        }
    }

    Comparator<Brick> brickComparator = (b1, b2) -> {
        // Bricks cannot intersect, so
        // we can safely just take the smallest coordinate
        // for each axis and compare them
        int z1 = Math.min(b1.p1.z(), b1.p2.z());
        int z2 = Math.min(b2.p1.z(), b2.p2.z());
        int y1 = Math.min(b1.p1.y(), b1.p2.y());
        int y2 = Math.min(b2.p1.y(), b2.p2.y());
        int x1 = Math.min(b1.p1.x(), b1.p2.x());
        int x2 = Math.min(b2.p1.x(), b2.p2.x());
        if (z1 == z2) {
            if (y1 == y2) {
                return x1 - x2;
            }
            return y1 - y2;
        }
        return z1 - z2;
    };

    List<Brick> bricks;

    class BrickLayer {
        Map<Point2, Brick> layers = new HashMap<>();

        void layBrick(Brick b) {
            var newZ = b.bottomPoints().map(layers::get).filter(p -> p != null).mapToInt(Brick::highestZ).max()
                    .orElse(0)
                    + 1;

            b.setZ(newZ);

            bricks.add(b);

            b.bottomPoints().forEach(p -> {
                if (layers.containsKey(p) && layers.get(p).highestZ() == b.lowestZ() - 1) {
                    b.addSupportedBy(layers.get(p));
                    layers.get(p).addSupports(b);
                }
                layers.put(p, b);
            });

        }

    }

    public long disintegrationCount() {
        return bricks.stream().filter(Brick::disintegratable).count();
    }

    Optional<Brick> getBrickById(String id) {
        return bricks.stream().filter(b -> b.id.equals(id)).findFirst();
    }

    public App(String input) {
        bricks = new ArrayList<>();
        var workingBreaks = input.lines().map(Brick::of).sorted(brickComparator).toList();
        workingBreaks.forEach(new BrickLayer()::layBrick);
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day22/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);
        // 598 is too high
        IO.answer(app.disintegrationCount());
        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }
}
