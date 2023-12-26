package com.benjaminbinford.day24;

import java.util.List;
import java.util.Optional;

import com.benjaminbinford.utils.AdventException;
import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    enum EarlierCrossing {
        A, B, BOTH
    }

    sealed interface PathResult permits PathInside, PathOutside, PathNonIntersecting,
            PathEarlier {

        default boolean isInside() {
            return this instanceof PathInside;

        }

        default boolean intersects() {
            return this instanceof PathInside || this instanceof PathOutside;
        }
    }

    record PathInside(Vector3 intersection, double t, double s) implements PathResult {

        @Override
        public String toString() {
            return String.format("PathInside(%s)", intersection);
        }

    }

    record PathOutside(Vector3 intersection, double t, double s) implements PathResult {
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

    private static final double EPSILON = 1e-9;

    record HailstoneConstraint(double b0, double bv, Constraint a0Left, Constraint a0Right) {

        Constraint getVelocityConstraint(double a0) {
            if (a0 < b0) {
                return a0Left;
            } else if (a0 > b0) {
                return a0Right;
            } else {
                return Constraint.ofConstant(bv);
            }
        }

        /*
         * 
         * if a0 > b0
         * 
         * 0-------b0---------------a0
         * if av = bv
         * no values
         * if av > 0 and bv >0
         * 0-------b0>---------------a0>
         * av < bv
         * 
         * 
         * if av <0 and bv >0
         * 0-------b0>---------------<a0
         * all values
         * if av>0 and bv < 0
         * 0-------<b0---------------a0>
         * no values
         * 
         * if av < 0 and bv <0
         * 0-------<b0---------------<a0
         * av>bv
         * 
         * else if b0 > a0
         * 0-------a0---------------b0
         * if av = bv
         * no values
         * 
         * if av > 0 and bv >0
         * 0-------a0>---------------b0>
         * av > bv
         * 
         * 
         * if av <0 and bv >0
         * 0-------<a0---------------b0>
         * no values
         * if av>0 and bv < 0
         * 0-------a0>---------------<b0
         * all values
         * 
         * if av < 0 and bv <0
         * 0-------<a0---------------<b0
         * av<bv
         */
        static HailstoneConstraint of(double b0, double bv) {

            // Constraint a0RightAvPositive;
            // Constraint a0RightAvNegative;
            // Constraint a0LeftAvPositive;
            // Constraint a0LeftAvNegative;
            var a0Right = Constraint.ofMax(bv - 1);
            var a0Left = Constraint.ofMin(bv + 1);

            // if (bv < 0) {
            // // 0-------<b0---------------a0>
            // // a0RightAvPositive = Constraint.ofNone();
            // // 0-------<b0---------------<a0
            // // a0RightAvNegative = Constraint.ofMax(bv - 1);

            // a0Right = Constraint.ofMax(bv - 1);

            // // 0-------a0>---------------<b0
            // // a0LeftAvPositive = Constraint.ofAll();
            // // 0-------<a0---------------<b0
            // // a0LeftAvNegative = Constraint.of(bv + 1, 1);

            // a0Left = Constraint.ofMin(bv + 1);
            // } else {
            // // 0-------b0>---------------a0>
            // // a0RightAvPositive = Constraint.of(1, bv - 1);
            // // 0-------b0>---------------<a0
            // // a0RightAvNegative = Constraint.ofAll();

            // a0Right = Constraint.ofMax(bv - 1);

            // // 0-------a0>---------------b0>
            // // a0LeftAvPositive = Constraint.ofMin(bv + 1);
            // // 0-------<a0---------------b0>
            // // a0LeftAvNegative = Constraint.ofNone();

            // a0Left = Constraint.ofMin(bv + 1);
            // }

            return new HailstoneConstraint(b0, bv, a0Left, a0Right);
        }
    }

    enum ConstraintType {
        // NONE,
        // ALL,
        Constant,
        MIN,
        MAX,
        // BOTH

    }

    record Constraint(ConstraintType type, double minimum, double maximum) {

        static Constraint ofConstant(double value) {
            return new Constraint(ConstraintType.Constant, value, value);
        }

        static Constraint ofMin(double min) {
            return new Constraint(ConstraintType.MIN, min, 0);
        }

        static Constraint ofMax(double max) {
            return new Constraint(ConstraintType.MAX, 0, max);
        }

        // static Constraint ofNone() {
        // return new Constraint(ConstraintType.NONE, 0, 0);
        // }

        // static Constraint ofAll() {
        // return new Constraint(ConstraintType.ALL, 0, 0);
        // }

    }

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

        PathResult pathIntersects2d(Hailstone b, Vector3 startRange, Vector3 endRange) {

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
                return new PathNonIntersecting();
            }

            var x = (aSlope * a.position.x - bSlope * b.position.x + b.position.y - a.position.y)
                    / (aSlope - bSlope);
            var t = (x - a.position.x) / a.velocity.x;
            var y = a.position.y + a.velocity.y * t;
            var s = (x - b.position.x) / b.velocity.x;

            if (t < 0 && s < 0) {
                return new PathEarlier(EarlierCrossing.BOTH);
            } else if (t < 0) {
                return new PathEarlier(EarlierCrossing.A);
            } else if (s < 0) {
                return new PathEarlier(EarlierCrossing.B);
            } else if (x >= startRange.x && x <= endRange.x && y >= startRange.y && y <= endRange.y) {
                return new PathInside(new Vector3(x, y, 0), t, s);
            } else {
                return new PathOutside(new Vector3(x, y, 0), t, s);
            }
        }

        PathResult pathIntersects3d(Hailstone b, Vector3 startRange, Vector3 endRange) {

            // check for z intersection
            // a0z + avz*t = az
            // b0z + bvz*s = bz

            Hailstone a = this;

            PathResult result = pathIntersects2d(b, startRange, endRange);

            if (result instanceof PathEarlier) {
                return result;
            } else if (result instanceof PathNonIntersecting) {
                return result;
            } else if (result instanceof PathOutside) {
                PathOutside resultOutside = (PathOutside) result;
                double az = a.position.z + a.velocity.z * resultOutside.t;
                double bz = b.position.z + b.velocity.z * resultOutside.s;
                if (epsEquals(az, bz)) {
                    return new PathOutside(new Vector3(resultOutside.intersection.x, resultOutside.intersection.y, az),
                            resultOutside.t, resultOutside.s);
                } else {
                    return new PathNonIntersecting();
                }
            } else if (result instanceof PathInside) {
                PathInside r = (PathInside) result;
                double az = a.position.z + a.velocity.z * r.t;
                double bz = b.position.z + b.velocity.z * r.s;
                if (epsEquals(az, bz)) {
                    if (az >= startRange.z && az <= endRange.z) {
                        return new PathInside(new Vector3(r.intersection.x, r.intersection.y, az), r.t, r.s);
                    } else {
                        return new PathOutside(new Vector3(r.intersection.x, r.intersection.y, az), r.t, r.s);
                    }
                } else {
                    return new PathNonIntersecting();
                }
            } else {
                throw new AdventException("Unknown PathResult type");
            }

        }

        record Intersection(Vector3 position, double t) {

        }

        Intersection intersects(Hailstone b, Vector3 startRange, Vector3 endRange) {

            // y = a0y + avy*t
            // t = s = (x-a0x)/avx
            // a0z + avz*t = az
            // b0z + bvz*s = bz
            // z = a0z+avz * t

            // x = (avy/avx*a0x - bvy/bvx*b0x + b0y - a0y)/(avy/avx - bvy/bvx)
            // y = b0y + bvy*(x-b0x)/bvx
            // z = b0z + bvz*(x-b0x)/bvx = a0z + avz*(x-a0x)/avx

            // unknowns avy, avx, avz, a0x, a0y, a0z;

            //
            var result = pathIntersects3d(b, startRange, endRange);

            if (result instanceof PathOutside) {
                PathOutside r = (PathOutside) result;
                if (epsEquals(r.t, r.s)) {
                    return new Intersection(r.intersection, r.t);
                } else {
                    return null;
                }
            } else if (result instanceof PathInside) {
                PathInside r = (PathInside) result;
                if (epsEquals(r.t, r.s)) {
                    return new Intersection(r.intersection, r.t);
                } else {
                    return null;
                }
            } else {
                return null;
            }

        }

    }

    List<Hailstone> hailstones2d;
    List<Hailstone> hailstones3d;
    List<HailstoneConstraint> hailstoneXConstraints;
    List<HailstoneConstraint> hailstoneYConstraints;
    List<HailstoneConstraint> hailstoneZConstraints;

    public App(String input) {
        hailstones2d = input.lines().map(Hailstone::of2D).toList();
        hailstones3d = input.lines().map(Hailstone::of3D).toList();
        hailstoneXConstraints = hailstones3d.stream()
                .map(h -> HailstoneConstraint.of(h.position.x, h.velocity.x)).toList();

        hailstoneYConstraints = hailstones3d.stream()
                .map(h -> HailstoneConstraint.of(h.position.y, h.velocity.y)).toList();

        hailstoneZConstraints = hailstones3d.stream()
                .map(h -> HailstoneConstraint.of(h.position.z, h.velocity.z)).toList();

    }

    public long countInsideIntersections(Vector3 startRange, Vector3 endRange) {

        var sum = 0l;
        for (var i = 0; i < hailstones2d.size() - 1; i++) {
            for (var j = i + 1; j < hailstones2d.size(); j++) {
                if (hailstones2d.get(i).pathIntersects3d(hailstones2d.get(j), startRange, endRange).isInside()) {
                    sum++;
                }
            }
        }
        return sum;
    }

    public Hailstone findIntersector(Vector3 startRange, Vector3 endRange) {
        for (var pi = startRange.x; pi <= endRange.x; pi++) {
            for (var pj = startRange.y; pj <= endRange.y; pj++) {
                for (var pk = startRange.z; pk <= endRange.z; pk++) {

                    for (var vi = -(endRange.x - startRange.x) / hailstones3d.size(); vi <= (endRange.x - startRange.x)
                            / hailstones3d.size(); vi++) {
                        for (var vj = -(endRange.y - startRange.y)
                                / hailstones3d.size(); vj <= (endRange.y - startRange.y) /
                                        hailstones3d.size(); vj++) {
                            for (var vk = -(endRange.z - startRange.z)
                                    / hailstones3d.size(); vk <= (endRange.z - startRange.z)
                                            / hailstones3d.size(); vk++) {

                                var p = new Vector3(pi, pj, pk);
                                var v = new Vector3(vi, vj, vk);
                                var a = new Hailstone(p, v);
                                if (hailstones3d.stream()
                                        .allMatch(b -> a.intersects(b, startRange, endRange) != null)) {
                                    return a;
                                }
                            }
                        }
                    }
                }
            }
        }
        throw new AdventException("No intersector found");

    }

    public Hailstone findIntersector2d(Vector3 startRange, Vector3 endRange) {
        for (var pi = startRange.x; pi <= endRange.x; pi++) {
            for (var pj = startRange.y; pj <= endRange.y; pj++) {

                for (var vi = -(endRange.x - startRange.x) / hailstones3d.size(); vi <= (endRange.x - startRange.x)
                        / hailstones3d.size(); vi++) {
                    for (var vj = -(endRange.y - startRange.y)
                            / hailstones3d.size(); vj <= (endRange.y - startRange.y) /
                                    hailstones3d.size(); vj++) {
                        {

                            var p = new Vector3(pi, pj, 0);
                            var v = new Vector3(vi, vj, 0);
                            var a = new Hailstone(p, v);
                            if (hailstones3d.stream()
                                    .allMatch(b -> a.pathIntersects2d(b, startRange, endRange).intersects())) {
                                return a;
                            }
                        }
                    }
                }
            }
        }

        throw new AdventException("No intersector found");

    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day24/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        IO.answer(String.format("Part 1: %d", app.countInsideIntersections(new Vector3(200_000_000_000_000.0,
                200000000000000.0, 0), new Vector3(400000000000000.0, 400000000000000.0, 0))));
        IO.answer(String.format("Part 2: %s", app.findIntersector2d(new Vector3(200000000000000.0,
                200000000000000.0, 0), new Vector3(400000000000000.0, 400000000000000.0, 0))));
        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }
}
