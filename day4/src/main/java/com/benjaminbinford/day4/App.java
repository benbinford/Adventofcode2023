package com.benjaminbinford.day4;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.benjaminbinford.utils.IO;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        List<String> lines = IO.getResourceLines("com/benjaminbinford/day4/input.txt");

        var app = new App();

        IO.answer(app.sumCardScores(lines.stream()));

        IO.answer(app.makeCards(lines.stream()).sumCopies());
    }

    private int sumCardScores(Stream<String> stream) {
        return stream.map(Card::parse).mapToInt(Card::score).sum();
    }

    public Cards makeCards(Stream<String> lines) {
        return new Cards(lines.map(Card::parse).map(CardTracker::new).collect(Collectors.toList()));
    }

}
