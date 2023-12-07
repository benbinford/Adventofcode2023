package com.benjaminbinford.day7;

import java.util.List;

public record Game<T extends Comparable<T>>(List<Hand<T>> hands) {

    public static Game<Card> of(String s) {
        var hands = s.lines().map(Hand::of).sorted().toList();
        return new Game<>(hands);
    }

    public static Game<CardWithJoker> ofJokers(String s) {
        var hands = s.lines().map(Hand::ofJokers).sorted().toList();
        return new Game<>(hands);
    }

    public long totalWinnings() {
        var total = 0L;
        for (var i = 0; i < hands.size(); i++) {
            total += hands.get(i).bid() * (hands.size() - i);
        }
        return total;
    }

}
