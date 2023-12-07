package com.benjaminbinford.day7;

import java.util.List;

public record Game(List<Hand> hands) {

    public static Game of(String s) {
        var hands = s.lines().map(sh -> Hand.of(sh, Card::compareTo)).sorted().toList();
        return new Game(hands);
    }

    public static Game ofJokers(String s) {
        var hands = s.lines().map(sh -> Hand.of(sh, (a, b) -> {
            int a0 = a.ordinal();
            if (a == Card.J) {
                a0 = Integer.MAX_VALUE;
            }
            int b0 = b.ordinal();
            if (b == Card.J) {
                b0 = Integer.MAX_VALUE;
            }
            return Integer.compare(a0, b0);

        }, Card.J)).sorted().toList();
        return new Game(hands);
    }

    public long totalWinnings() {
        var total = 0L;
        for (var i = 0; i < hands.size(); i++) {
            total += hands.get(i).bid() * (hands.size() - i);
        }
        return total;
    }

}
