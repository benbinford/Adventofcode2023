package com.benjaminbinford.day24;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.linear.ArrayFieldVector;
import org.apache.commons.math3.linear.FieldLUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.util.BigReal;

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

        public Vector3 to1d() {
            return new Vector3(x, 0, 0);
        }
    }

    static boolean epsEquals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }

    record Hailstone(Vector3 position, Vector3 velocity) {

        Vector3 positionAt(double t) {
            return position.add(velocity.multiply(t));
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
                return confirmZIntersection(b, a, (PathOutside) result);
            } else if (result instanceof PathInside) {
                return confirmZIntersection(b, startRange, endRange, a, (PathInside) result);
            } else {
                throw new AdventException("Unknown PathResult type");
            }

        }

        private PathResult confirmZIntersection(Hailstone b, Vector3 startRange, Vector3 endRange, Hailstone a,
                PathInside r) {
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
        }

        private PathResult confirmZIntersection(Hailstone b, Hailstone a, PathOutside resultOutside) {
            double az = a.position.z + a.velocity.z * resultOutside.t;
            double bz = b.position.z + b.velocity.z * resultOutside.s;
            if (epsEquals(az, bz)) {
                return new PathOutside(new Vector3(resultOutside.intersection.x, resultOutside.intersection.y, az),
                        resultOutside.t, resultOutside.s);
            } else {
                return new PathNonIntersecting();
            }
        }

        record Intersection(Vector3 position, double t) {

        }

        Intersection intersects(Hailstone b, Vector3 startRange, Vector3 endRange) {

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

    List<Hailstone> hailstones3d;

    public App(String input) {
        hailstones3d = input.lines().map(Hailstone::of3D).toList();

    }

    public long countInsideIntersections(Vector3 startRange, Vector3 endRange) {

        var sum = 0l;
        for (var i = 0; i < hailstones3d.size() - 1; i++) {
            for (var j = i + 1; j < hailstones3d.size(); j++) {
                if (hailstones3d.get(i).pathIntersects2d(hailstones3d.get(j), startRange, endRange).isInside()) {
                    sum++;
                }
            }
        }
        return sum;
    }

    public Hailstone findIntersector() {

        var a = hailstones3d.get(0);
        var b = hailstones3d.get(1);
        var c = hailstones3d.get(2);
        var d = hailstones3d.get(3);
        var e = hailstones3d.get(4);

        var a0x = BigDecimal.valueOf(a.position.x);
        var a0y = BigDecimal.valueOf(a.position.y);
        var a0z = BigDecimal.valueOf(a.position.z);
        var avx = BigDecimal.valueOf(a.velocity.x);
        var avy = BigDecimal.valueOf(a.velocity.y);
        var avz = BigDecimal.valueOf(a.velocity.z);

        var b0x = BigDecimal.valueOf(b.position.x);
        var b0y = BigDecimal.valueOf(b.position.y);
        var b0z = BigDecimal.valueOf(b.position.z);
        var bvx = BigDecimal.valueOf(b.velocity.x);
        var bvy = BigDecimal.valueOf(b.velocity.y);
        var bvz = BigDecimal.valueOf(b.velocity.z);

        var c0x = BigDecimal.valueOf(c.position.x);
        var c0y = BigDecimal.valueOf(c.position.y);
        var c0z = BigDecimal.valueOf(c.position.z);
        var cvx = BigDecimal.valueOf(c.velocity.x);
        var cvy = BigDecimal.valueOf(c.velocity.y);
        var cvz = BigDecimal.valueOf(c.velocity.z);

        var d0x = BigDecimal.valueOf(d.position.x);
        var d0y = BigDecimal.valueOf(d.position.y);
        var d0z = BigDecimal.valueOf(d.position.z);
        var dvx = BigDecimal.valueOf(d.velocity.x);
        var dvy = BigDecimal.valueOf(d.velocity.y);
        var dvz = BigDecimal.valueOf(d.velocity.z);

        var e0x = BigDecimal.valueOf(e.position.x);
        var e0y = BigDecimal.valueOf(e.position.y);
        var e0z = BigDecimal.valueOf(e.position.z);
        var evx = BigDecimal.valueOf(e.velocity.x);
        var evy = BigDecimal.valueOf(e.velocity.y);
        var evz = BigDecimal.valueOf(e.velocity.z);

        /*
         * see math.txt
         * 
         *
         */

        var coefficients = new BigDecimal[][] {
                { a0x.subtract(b0x), avy.subtract(bvy), a0y.negate().add(b0y), avx.negate().add(bvx) },
                { b0x.subtract(c0x), bvy.subtract(cvy), b0y.negate().add(c0y), bvx.negate().add(cvx) },
                { c0x.subtract(d0x), cvy.subtract(dvy), c0y.negate().add(d0y), cvx.negate().add(dvx) },
                { d0x.subtract(e0x), dvy.subtract(evy), d0y.negate().add(e0y), dvx.negate().add(evx) }
        };

        var coefficientsZ = new BigDecimal[][] {
                { a0x.subtract(b0x), avz.subtract(bvz), a0z.negate().add(b0z), avx.negate().add(bvx) },
                { b0x.subtract(c0x), bvz.subtract(cvz), b0z.negate().add(c0z), bvx.negate().add(cvx) },
                { c0x.subtract(d0x), cvz.subtract(dvz), c0z.negate().add(d0z), cvx.negate().add(dvx) },
                { d0x.subtract(e0x), dvz.subtract(evz), d0z.negate().add(e0z), dvx.negate().add(evx) }
        };

        var constants = new BigDecimal[] {
                a0y.negate().multiply(avx).add(a0x.multiply(avy)).add(b0y.multiply(bvx)).subtract(b0x.multiply(bvy)),
                b0y.negate().multiply(bvx).add(b0x.multiply(bvy)).add(c0y.multiply(cvx)).subtract(c0x.multiply(cvy)),
                c0y.negate().multiply(cvx).add(c0x.multiply(cvy)).add(d0y.multiply(dvx)).subtract(d0x.multiply(dvy)),
                d0y.negate().multiply(dvx).add(d0x.multiply(dvy)).add(e0y.multiply(evx)).subtract(e0x.multiply(evy)) };

        var constantsZ = new BigDecimal[] {
                a0z.negate().multiply(avx).add(a0x.multiply(avz)).add(b0z.multiply(bvx)).subtract(b0x.multiply(bvz)),
                b0z.negate().multiply(bvx).add(b0x.multiply(bvz)).add(c0z.multiply(cvx)).subtract(c0x.multiply(cvz)),
                c0z.negate().multiply(cvx).add(c0x.multiply(cvz)).add(d0z.multiply(dvx)).subtract(d0x.multiply(dvz)),
                d0z.negate().multiply(dvx).add(d0x.multiply(dvz)).add(e0z.multiply(evx)).subtract(e0x.multiply(evz)) };

        var coefficientsDoubles = new double[][] {
                { a0x.doubleValue() - b0x.doubleValue(), avy.doubleValue() - bvy.doubleValue(),
                        -a0y.doubleValue() + b0y.doubleValue(), -avx.doubleValue() + bvx.doubleValue() },
                { b0x.doubleValue() - c0x.doubleValue(), bvy.doubleValue() - cvy.doubleValue(),
                        -b0y.doubleValue() + c0y.doubleValue(), -bvx.doubleValue() + cvx.doubleValue() },
                { c0x.doubleValue() - d0x.doubleValue(), cvy.doubleValue() - dvy.doubleValue(),
                        -c0y.doubleValue() + d0y.doubleValue(), -cvx.doubleValue() + dvx.doubleValue() },
                { d0x.doubleValue() - e0x.doubleValue(), dvy.doubleValue() - evy.doubleValue(),
                        -d0y.doubleValue() + e0y.doubleValue(), -dvx.doubleValue() + evx.doubleValue() }
        };

        var coefficientsZDoubles = new double[][] {
                { a0x.doubleValue() - b0x.doubleValue(), avz.doubleValue() - bvz.doubleValue(),
                        -a0z.doubleValue() + b0z.doubleValue(), -avx.doubleValue() + bvx.doubleValue() },
                { b0x.doubleValue() - c0x.doubleValue(), bvz.doubleValue() - cvz.doubleValue(),
                        -b0z.doubleValue() + c0z.doubleValue(), -bvx.doubleValue() + cvx.doubleValue() },
                { c0x.doubleValue() - d0x.doubleValue(), cvz.doubleValue() - dvz.doubleValue(),
                        -c0z.doubleValue() + d0z.doubleValue(), -cvx.doubleValue() + dvx.doubleValue() },
                { d0x.doubleValue() - e0x.doubleValue(), dvz.doubleValue() - evz.doubleValue(),
                        -d0z.doubleValue() + e0z.doubleValue(), -dvx.doubleValue() + evx.doubleValue() }
        };

        var constantsDoubles = new double[] {
                -a0y.doubleValue() * avx.doubleValue() + a0x.doubleValue() * avy.doubleValue()
                        + b0y.doubleValue() * bvx.doubleValue() - b0x.doubleValue() * bvy.doubleValue(),
                -b0y.doubleValue() * bvx.doubleValue() + b0x.doubleValue() * bvy.doubleValue()
                        + c0y.doubleValue() * cvx.doubleValue() - c0x.doubleValue() * cvy.doubleValue(),
                -c0y.doubleValue() * cvx.doubleValue() + c0x.doubleValue() * cvy.doubleValue()
                        + d0y.doubleValue() * dvx.doubleValue() - d0x.doubleValue() * dvy.doubleValue(),
                -d0y.doubleValue() * dvx.doubleValue() + d0x.doubleValue() * dvy.doubleValue()
                        + e0y.doubleValue() * evx.doubleValue() - e0x.doubleValue() * evy.doubleValue() };

        var constantsZDoubles = new double[] {
                -a0z.doubleValue() * avx.doubleValue() + a0x.doubleValue() * avz.doubleValue()
                        + b0z.doubleValue() * bvx.doubleValue() - b0x.doubleValue() * bvz.doubleValue(),
                -b0z.doubleValue() * bvx.doubleValue() + b0x.doubleValue() * bvz.doubleValue()
                        + c0z.doubleValue() * cvx.doubleValue() - c0x.doubleValue() * cvz.doubleValue(),
                -c0z.doubleValue() * cvx.doubleValue() + c0x.doubleValue() * cvz.doubleValue()
                        + d0z.doubleValue() * dvx.doubleValue() - d0x.doubleValue() * dvz.doubleValue(),
                -d0z.doubleValue() * dvx.doubleValue() + d0x.doubleValue() * dvz.doubleValue()
                        + e0z.doubleValue() * evx.doubleValue() - e0x.doubleValue() * evz.doubleValue() };

        assert (vectorEquals(constants, constantsDoubles));
        assert (vectorEquals(constantsZ, constantsZDoubles));
        assert (matrixEquals(coefficients, coefficientsDoubles));
        assert (matrixEquals(coefficientsZ, coefficientsZDoubles));
        BigDecimal[] solution = solve(coefficients, constants);

        var hvy = solution[0].doubleValue();
        var h0x = solution[1].doubleValue();
        var hvx = solution[2].doubleValue();
        var h0y = solution[3].doubleValue();

        BigDecimal[] solutionZ = solve(coefficientsZ, constantsZ);

        var hvz = solutionZ[0].doubleValue();
        assert (epsEquals(h0x, solutionZ[1].doubleValue()));
        assert (epsEquals(hvx, solutionZ[2].doubleValue()));
        var h0z = solutionZ[3].doubleValue();

        var h = new Hailstone(new Vector3(h0x, h0y, h0z),
                new Vector3(hvx, hvy, hvz));

        var range = new Vector3(0, 0, 0);
        var rangeEnd = new Vector3(1, 1, 1);

        for (var i = 0; i < hailstones3d.size(); i++) {
            var h2 = hailstones3d.get(i);
            assert (h.intersects(h2, range, rangeEnd) != null);
        }

        return h;
    }

    private boolean matrixEquals(BigDecimal[][] coefficients, double[][] coefficientsDoubles) {
        for (int j = 0; j < coefficients.length; j++) {
            for (int i = 0; i < coefficients[j].length; i++) {
                if (!epsEquals(coefficients[j][i].doubleValue(), coefficientsDoubles[j][i])) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean vectorEquals(BigDecimal[] constants, double[] constantsDoubles) {
        for (int i = 0; i < constants.length; i++) {
            if (!epsEquals(constants[i].doubleValue(), constantsDoubles[i])) {
                return false;
            }
        }
        return true;
    }

    private BigDecimal[] solve(BigDecimal[][] coefficients, BigDecimal[] constants) {

        var m = MatrixUtils.createFieldMatrix(Arrays.stream(coefficients)
                .map(r -> Arrays.stream(r).map(BigReal::new).toArray(BigReal[]::new)).toArray(BigReal[][]::new));
        var c = new ArrayFieldVector<>(Arrays.stream(constants).map(BigReal::new).toArray(BigReal[]::new));

        var solver = new FieldLUDecomposition<>(m).getSolver();
        var solution = solver.solve(c);
        var result = new BigDecimal[constants.length];
        for (var i = 0; i < constants.length; i++) {
            result[i] = solution.getEntry(i).bigDecimalValue();
        }
        return result;

    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day24/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        IO.answer(String.format("Part 1: %d", app.countInsideIntersections(new Vector3(200_000_000_000_000.0,
                200000000000000.0, 0),
                new Vector3(400000000000000.0, 400000000000000.0,
                        0))));
        Hailstone h = app.findIntersector();
        IO.answer(String.format("Part 2: %s", h));
        IO.answer(h.position.x + h.position.y + h.position.z);

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 1_000_000));
    }
}
