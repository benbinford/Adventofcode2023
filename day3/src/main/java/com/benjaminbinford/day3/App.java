package com.benjaminbinford.day3;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {
    private static final Schematic SCHEMATIC = Schematic.parse(IO.getResource("com/benjaminbinford/day3/input.txt"));

    public static void main(String[] args) {
        IO.answer(SCHEMATIC.sumAttachedPartNumbers());
        IO.answer(SCHEMATIC.sumGearRations());

    }
}
