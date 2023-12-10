package com.benjaminbinford.day10;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.parser.Input;
import org.typemeta.funcj.tuples.Tuple2;

import com.benjaminbinford.utils.IO;
import com.benjaminbinford.utils.LineTrackingInput.Position;
import com.benjaminbinford.utils.ParserUtil;

/**
 * Hello world!
 *
 */
public class App {

    private static Destination posToDest(Position pos) {
        return new Destination(pos.line(), pos.column());
    }

    private static Pipe newPipe(char rep, Position me, char sShape) {
        final char shape;
        if (rep == 'S') {
            shape = sShape;
        } else {
            shape = rep;
        }

        Destination d = posToDest(me);
        switch (shape) {
            case '|':
                return new Pipe(rep, shape, d, new Tuple2<>(d.adj(-1, 0), d.adj(1, 0)));
            case '-':
                return new Pipe(rep, shape, d, new Tuple2<>(d.adj(0, -1), d.adj(0, 1)));
            case 'L':
                return new Pipe(rep, shape, d, new Tuple2<>(d.adj(-1, 0), d.adj(0, 1)));
            case 'J':
                return new Pipe(rep, shape, d, new Tuple2<>(d.adj(-1, 0), d.adj(0, -1)));
            case '7':
                return new Pipe(rep, shape, d, new Tuple2<>(d.adj(0, -1), d.adj(1, 0)));
            case 'F':
                return new Pipe(rep, shape, d, new Tuple2<>(d.adj(0, 1), d.adj(1, 0)));
            default:
                throw new IllegalArgumentException("Invalid pipe character: " + rep);
        }

    }

    private Map<Destination, Pipe> pipes;
    private Set<Destination> loopTiles;
    private int minPipeRow = Integer.MAX_VALUE;
    private int maxPipeRow = Integer.MIN_VALUE;
    private int minPipeCol = Integer.MAX_VALUE;
    private int maxPipeCol = Integer.MIN_VALUE;
    private Pipe start;

    public Pipe nextPipe(Destination from, Pipe current) {
        if (current.destinations().get1().equals(from)) {
            return pipes.get(current.destinations().get2());
        } else if (current.destinations().get2().equals(from)) {
            return pipes.get(current.destinations().get1());
        } else {
            throw new IllegalArgumentException("Pipe " + current + " is not connected to " + from);
        }
    }

    public int findMaxDistance() {
        loopTiles = new LinkedHashSet<>();
        Destination prior = start.me();
        Pipe current = pipes.get(start.destinations().get1());
        int distance = 1;
        do {
            loopTiles.add(prior);
            minPipeRow = Math.min(minPipeRow, current.me().row());
            maxPipeRow = Math.max(maxPipeRow, current.me().row());
            minPipeCol = Math.min(minPipeCol, current.me().col());
            maxPipeCol = Math.max(maxPipeCol, current.me().col());
            var nextPrior = current.me();
            current = nextPipe(prior, current);
            prior = nextPrior;
            distance++;
        } while (current.rep() != 'S');
        loopTiles.add(prior);
        return distance / 2;
    }

    private enum State {
        SAW_NONE, SAW_L, SAW_F
    }

    public int findAreaEnclosed() {
        if (loopTiles == null) {
            findMaxDistance();
        }

        int area = 0;
        for (var j = minPipeRow; j <= maxPipeRow; j++) {
            for (var i = minPipeCol; i <= maxPipeCol; i++) {
                Destination key = new Destination(j, i);
                var isPipe = loopTiles.contains(key);
                if (isPipe) {
                    continue;
                }
                int intersectCount = findIntersectCount(j, i);
                if (intersectCount % 2 == 1) {
                    area++;
                }

            }
        }
        return area;
    }

    private int findIntersectCount(int j, int i) {
        int intersectCount = 0;
        var state = State.SAW_NONE;

        for (var rayX = minPipeCol; rayX <= i; rayX++) {
            Destination rayXKey = new Destination(j, rayX);
            if (loopTiles.contains(rayXKey)) {
                Pipe rayXPipe = pipes.get(rayXKey);
                switch (rayXPipe.shape()) {
                    case '-':
                        break;
                    case '|':
                        state = State.SAW_NONE;
                        intersectCount++;
                        break;
                    case 'L':
                        state = State.SAW_L;
                        break;
                    case 'F':
                        state = State.SAW_F;
                        break;
                    case 'J':
                        if (state == State.SAW_F) {
                            intersectCount++;
                        }
                        state = State.SAW_NONE;
                        break;
                    case '7':
                        if (state == State.SAW_L) {
                            intersectCount++;
                        }
                        state = State.SAW_NONE;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid pipe character: " + rayXPipe.shape());
                }

            }
        }
        return intersectCount;
    }

    public App(String input, char sShape) {

        Input<Chr> in = ParserUtil.lineTrackingInput(input);

        pipes = new HashMap<>();
        while (!in.isEof()) {
            char ch = in.get().charValue();
            switch (ch) {
                case '|':
                case '-':
                case 'L':
                case 'J':
                case '7':
                case 'F':
                case 'S':
                    var pipe = newPipe(ch, (Position) in.position(), sShape);
                    if (pipe.rep() == 'S') {
                        start = pipe;
                    }
                    pipes.put(pipe.me(), pipe);
                    break;
                default:
                    assert (ch == '.' || ch == '\n');
            }
            in = in.next();
        }
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day10/input.txt");

        long startTime = System.nanoTime();

        final var app = new App(input, '|');

        IO.answer(app.findMaxDistance());
        IO.answer(app.findAreaEnclosed());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 100_000));
    }

}
