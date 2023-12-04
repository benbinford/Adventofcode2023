package com.benjaminbinford.day4;

public class CardTracker {

    private final Card card;
    private int copies;

    public CardTracker(Card card) {
        this.card = card;
        this.copies = 1;
    }

    public int getCopies() {
        return copies;
    }

    public void incrementCopies(int n) {
        copies += n;
    }

    public int getNumMatches() {
        return card.getNumMatches();
    }

    @Override
    public String toString() {
        return "CardTracker [Card=" + card.gameId() + ", copies=" + copies + "]";
    }
}
