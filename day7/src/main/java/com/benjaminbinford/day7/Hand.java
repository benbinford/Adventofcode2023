package com.benjaminbinford.day7;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public record Hand(Card[] cards, int bid, HandType type, Comparator<Card> ordering) implements Comparable<Hand> {

    public static Hand of(String s, Comparator<Card> ordering, Card... jokers) {
        var cards = s.split(" ");
        var bid = Integer.parseInt(cards[1]);
        Card[] cardObjs = cards[0].chars().mapToObj(Card::of).toArray(Card[]::new);
        return new Hand(cardObjs, bid, HandType.of(cardObjs, jokers), ordering);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Hand hand = (Hand) o;
        return bid == hand.bid && Arrays.equals(cards, hand.cards);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(bid);
        result = 31 * result + Arrays.hashCode(cards);
        return result;
    }

    @Override
    public String toString() {
        return "Hand{" +
                "cards=" + Arrays.toString(cards) +
                ", bid=" + bid +
                ", type=" + type +
                '}';
    }

    @Override
    public int compareTo(Hand other) {
        // Compare based on hand type
        int typeComparison = this.type.compareTo(other.type);
        if (typeComparison != 0) {
            return typeComparison;
        }

        // Compare cards one by one
        for (int i = 0; i < cards.length; i++) {
            int cardComparison = ordering.compare(this.cards[i], other.cards[i]);
            if (cardComparison != 0) {
                return cardComparison;
            }
        }

        // If all cards are equal, the hands are considered equal
        return 0;
    }
}
