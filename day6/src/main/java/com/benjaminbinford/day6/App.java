package com.benjaminbinford.day6;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day6/input.txt");

        Races rs = Races.parse(input);

        IO.answer(rs.multiplyWins());

        final var input2 = IO.getResource("com/benjaminbinford/day6/input2.txt");

        Races rs2 = Races.parse(input2);

        IO.answer(rs2.multiplyWins());
    }
}
