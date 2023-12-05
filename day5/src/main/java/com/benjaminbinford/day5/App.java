package com.benjaminbinford.day5;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day5/input.txt");

        IO.answer(Almanac.parse(input).minLocation());
        IO.answer(Almanac.parse(input).minLocationExpandedSeeds());
    }
}
