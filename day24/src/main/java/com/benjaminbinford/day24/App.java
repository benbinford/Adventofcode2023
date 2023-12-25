package com.benjaminbinford.day24;

import java.util.List;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    enum EarlierCrossing {
        A, B, BOTH
    }

    sealed interface PathResult permits PathResult.PathInside, PathResult.PathOutside, PathResult.PathNonIntersecting,
            PathResult.PathEarlier {

        record PathInside(Vector3 intersection) implements PathResult {

            @Override
            public String toString() {
                return String.format("PathInside(%s)", intersection);
            }

        }

        record PathOutside(Vector3 intersection) implements PathResult {
            @Override
            public String toString() {
                return String.format("PathOutside(%s)", intersection);
            }
        }

        record PathNonIntersecting() implements PathResult {
            @Override
            public String toString() {
                return "PathNonIntersecting";
            }
        }

        record PathEarlier(EarlierCrossing crossing) implements PathResult {
            @Override
            public String toString() {
                return String.format("PathEarlier(%s)", crossing);
            }
        }

        default boolean isInside() {
            return this instanceof PathInside;

        }

    }

    private static final double EPSILON = 1e-9;

    record Vector3(double x, double y, double z) {

        @Override
        public String toString() {
            return String.format("%.3f'%.3f'%.3f", x, y, z);
        }

        Vector3 to2d() {
            return new Vector3(x, y, 0);
        }

        Vector3 unit() {
            var mag = Math.sqrt(x * x + y * y + z * z);
            return new Vector3(x / mag, y / mag, z / mag);
        }

        Vector3 subtract(Vector3 other) {
            return new Vector3(x - other.x, y - other.y, z - other.z);
        }

        Vector3 multiply(double scalar) {
            return new Vector3(x * scalar, y * scalar, z * scalar);
        }

        Vector3 add(Vector3 other) {
            return new Vector3(x + other.x, y + other.y, z + other.z);
        }

        @Override
        public boolean equals(Object otherO) {
            if (!(otherO instanceof Vector3)) {
                return false;
            }
            var other = (Vector3) otherO;
            return epsEquals(x, other.x) && epsEquals(y, other.y)
                    && epsEquals(z, other.z);
        }
    }

    static boolean epsEquals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    record Hailstone(Vector3 position, Vector3 velocity) {

        Vector3 positionAt(double t) {
            return position.add(velocity.multiply(t));
        }

        static Hailstone of2D(String rep) {
            Hailstone h = of3D(rep);
            return new Hailstone(h.position().to2d(), h.velocity().to2d());

        }

        static Hailstone of3D(String rep) {
            var parts = rep.split("\\s*@\\s*");
            var pparts = parts[0].split("\\s*,\\s*");
            var vparts = parts[1].split("\\s*,\\s*");
            return new Hailstone(
                    new Vector3(
                            Double.parseDouble(pparts[0]),
                            Double.parseDouble(pparts[1]),
                            Double.parseDouble(pparts[2])),
                    new Vector3(
                            Double.parseDouble(vparts[0]),
                            Double.parseDouble(vparts[1]),
                            Double.parseDouble(vparts[2])));
        }

        PathResult intersects2d(Hailstone b, Vector3 startRange, Vector3 endRange) {

            /*
             * Parametric for t
             * 
             * 
             * a0x + avx*t = ax
             * a0y + avy*t = ay
             * 
             * (ax-a0x)/avx = (ay-a0y)/avy
             * avy/avx*(ax-a0x) + a0y = ay
             * 
             * b0x + bvx*s = bx
             * b0y + bvy*s = by
             * bvy/bvx*(bx-b0x) + b0y = by
             * 
             * at intersect, ax = bx and ay=by
             * avy/avx*(x-a0x) + a0y = bvy/bvx*(x-b0x) + b0y
             * avy/avx*x - avy/avx*a0x + a0y = bvy/bvx*x - bvy/bvx*b0x + b0y
             * avy/avx*x - bvy/bvx*x = -bvy/bvx*b0x + avy/avx*a0x + b0y - a0y
             * (avy/avx - bvy/bvx)*x = -bvy/bvx*b0x + avy/avx*a0x + b0y - a0y
             * x = (-bvy/bvx*b0x + avy/avx*a0x + b0y - a0y)/(avy/avx - bvy/bvx)
             * x= (avy/avx*a0x - bvy/bvx*b0x + b0y - a0y)/(avy/avx - bvy/bvx)
             * 
             * t = (x-a0x)/avx
             * s = (x-b0x)/bvx
             */

            var a = this;

            var aSlope = a.velocity.y / a.velocity.x;
            var bSlope = b.velocity.y / b.velocity.x;

            if (epsEquals(aSlope, bSlope)) {
                return new PathResult.PathNonIntersecting();
            }

            var x = (aSlope * a.position.x - bSlope * b.position.x + b.position.y - a.position.y)
                    / (aSlope - bSlope);
            var t = (x - a.position.x) / a.velocity.x;
            var y = a.position.y + a.velocity.y * t;
            var s = (x - b.position.x) / b.velocity.x;

            if (t < 0 && s < 0) {
                return new PathResult.PathEarlier(EarlierCrossing.BOTH);
            } else if (t < 0) {
                return new PathResult.PathEarlier(EarlierCrossing.A);
            } else if (s < 0) {
                return new PathResult.PathEarlier(EarlierCrossing.B);
            } else if (x >= startRange.x && x <= endRange.x && y >= startRange.y && y <= endRange.y) {
                return new PathResult.PathInside(new Vector3(x, y, 0));
            } else {
                return new PathResult.PathOutside(new Vector3(x, y, 0));
            }
        }

    }

    List<Hailstone> hailstones2d;

    public App(String input) {
        hailstones2d = input.lines().map(Hailstone::of2D).toList();
    }

    public long countInsideIntersections2D(Vector3 startRange, Vector3 endRange) {

        var sum = 0l;
        for (var i = 0; i < hailstones2d.size() - 1; i++) {
            for (var j = i + 1; j < hailstones2d.size(); j++) {
                if (hailstones2d.get(i).intersects2d(hailstones2d.get(j), startRange, endRange).isInside()) {
                    sum++;
                }
            }
        }
        return sum;
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day24/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        IO.answer(String.format("Part 1: %d", app.countInsideIntersections2D(new Vector3(200000000000000.0,
                200000000000000.0, 0), new Vector3(400000000000000.0, 400000000000000.0, 0))));
        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }
}
