package com.benjaminbinford.day7;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        final var input = IO.getResource("com/benjaminbinford/day7/input.txt");

        var g = Game.of(input);
        IO.answer(g.totalWinnings());

        var g2 = Game.ofJokers(input);
        IO.answer(g2.totalWinnings());

    }
}
