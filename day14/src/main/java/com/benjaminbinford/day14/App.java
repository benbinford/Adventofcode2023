package com.benjaminbinford.day14;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.typemeta.funcj.tuples.Tuple2;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {

    private char[][] platform;
    private int height;
    private int width;
    private int spinCount = 0;

    private Map<String, Integer> memoizedSpin = new HashMap<>();
    private Map<Integer, String> memoizedPlatform = new HashMap<>();

    public App(String input) {
        this.platform = toRep(input);
        this.height = platform.length;
        this.width = platform[0].length;
    }

    private char[][] toRep(String input) {
        return input.lines().map(String::toCharArray).toArray(char[][]::new);
    }

    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day14/input.txt");

        long startTime = System.nanoTime();
        final var app = new App(input);

        app.slideNorth();

        IO.answer(app.findLoad());

        final var appSpin = new App(input);

        IO.answer(appSpin.findSpinCycle());

        long elapsedTime = System.nanoTime() - startTime;
        IO.answer(String.format("Elapsed time: %d", elapsedTime / 100_000));
    }

    public long findSpinCycle() {
        while (true) {
            String key = this.toString();
            int keySpin = spinCount;
            if (memoizedSpin.containsKey(key)) {

                return findSpinLoad(key, keySpin);

            }

            memoizedSpin.put(key, spinCount);
            memoizedPlatform.put(spinCount, key);

            spinCount++;

            spin();

        }
    }

    private long findSpinLoad(String platformRep, int keySpin) {

        int cycleStart = memoizedSpin.get(platformRep);
        int cycleEnd = keySpin;

        int cycleLength = cycleEnd - cycleStart;

        int maxSpin = 1000000000;

        int cycleIndex = (maxSpin - cycleStart) % cycleLength;

        String cyclePlatform = memoizedPlatform.get(cycleIndex + cycleStart);

        return findLoad(cyclePlatform);
    }

    private long findLoad(String cyclePlatform) {
        return findLoad(toRep(cyclePlatform), height, width);
    }

    private static long findLoad(char[][] platform, int height, int width) {
        long sum = 0l;
        for (int j = 0; j < height; j++) {
            int load = height - j;
            for (int i = 0; i < width; i++) {
                if (platform[j][i] == ROUND) {
                    sum += load;
                }
            }
        }
        return sum;
    }

    public void spin() {

        slideNorth();
        slideWest();
        slideSouth();
        slideEast();

    }

    public long findLoad() {
        return findLoad(platform, height, width);
    }

    private static final char ROUND = 'O';
    private static final char EMPTY = '.';

    public void slideNorth() {
        for (int j = 1; j < this.height; j++) {
            for (int i = 0; i < this.width; i++) {
                if (platform[j][i] == ROUND) {
                    slideRounderNorth(j, i);
                }
            }
        }

    }

    private void slideRounderNorth(int j, int i) {
        for (int k = j; k > 0; k--) {
            if (platform[k - 1][i] == EMPTY) {
                platform[k - 1][i] = ROUND;
                platform[k][i] = EMPTY;
            } else {
                break;
            }
        }
    }

    public void slideWest() {
        for (int j = 0; j < this.height; j++) {
            for (int i = 1; i < this.width; i++) {
                if (platform[j][i] == ROUND) {
                    slideRounderWest(j, i);
                }
            }
        }

    }

    private void slideRounderWest(int j, int i) {
        for (int k = i; k > 0; k--) {
            if (platform[j][k - 1] == EMPTY) {
                platform[j][k - 1] = ROUND;
                platform[j][k] = EMPTY;
            } else {
                break;
            }
        }
    }

    public void slideSouth() {
        for (int j = this.height - 2; j >= 0; j--) {
            for (int i = 0; i < this.width; i++) {
                if (platform[j][i] == ROUND) {
                    slideRounderSouth(j, i);
                }
            }
        }

    }

    private void slideRounderSouth(int j, int i) {
        for (int k = j; k < this.height - 1; k++) {
            if (platform[k + 1][i] == EMPTY) {
                platform[k + 1][i] = ROUND;
                platform[k][i] = EMPTY;
            } else {
                break;
            }
        }
    }

    public void slideEast() {
        for (int j = 0; j < this.height; j++) {
            for (int i = this.width - 2; i >= 0; i--) {
                if (platform[j][i] == ROUND) {
                    slideRounderEast(j, i);
                }
            }
        }

    }

    private void slideRounderEast(int j, int i) {
        for (int k = i; k < this.width - 1; k++) {
            if (platform[j][k + 1] == EMPTY) {
                platform[j][k + 1] = ROUND;
                platform[j][k] = EMPTY;
            } else {
                break;
            }
        }
    }

    public String toString() {
        return Arrays.stream(platform).map(String::new).collect(Collectors.joining("\n"));
    }
}
