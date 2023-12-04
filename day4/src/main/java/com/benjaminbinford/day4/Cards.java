package com.benjaminbinford.day4;

import java.util.List;

public record Cards(List<CardTracker> cards) {

    public int sumCopies() {

        for (int i = 0; i < cards.size(); i++) {
            CardTracker card = cards.get(i);
            var score = card.getNumMatches();
            for (int j = i + 1; j < cards.size() && j < i + score + 1; j++) {
                CardTracker other = cards.get(j);
                other.incrementCopies(card.getCopies());
            }
        }

        return cards.stream().mapToInt(CardTracker::getCopies).sum();
    }

}
