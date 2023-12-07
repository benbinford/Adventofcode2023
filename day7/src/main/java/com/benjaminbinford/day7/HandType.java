package com.benjaminbinford.day7;

import java.util.Arrays;

public enum HandType {
    FIVE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD;

    public static HandType of(Card[] cards) {
        var counts = countCardOccurrences(cards);
        var max = findMaxCount(counts);

        if (max == 5) {
            return FIVE_OF_A_KIND;
        } else if (max == 4) {
            return FOUR_OF_A_KIND;
        } else if (max == 3) {
            return determineThreeOfAKindOrFullHouse(counts);
        } else if (max == 2) {
            return determineTwoPairOrOnePair(counts);
        } else {
            return HIGH_CARD;
        }
    }

    private static <T extends Enum<T>> int[] countCardOccurrences(T[] cards) {
        var counts = new int[13];
        for (var card : cards) {
            counts[card.ordinal()]++;
        }
        return counts;
    }

    private static int findMaxCount(int[] counts) {
        var max = 0;
        for (var count : counts) {
            if (count > max) {
                max = count;
            }
        }
        return max;
    }

    private static HandType determineThreeOfAKindOrFullHouse(int[] counts) {
        var hasPair = false;
        for (var count : counts) {
            if (count == 2) {
                hasPair = true;
                break;
            }
        }
        if (hasPair) {
            return FULL_HOUSE;
        } else {
            return THREE_OF_A_KIND;
        }
    }

    private static HandType determineTwoPairOrOnePair(int[] counts) {
        var pairCount = 0;
        for (var count : counts) {
            if (count == 2) {
                pairCount++;
            }
        }
        if (pairCount == 2) {
            return TWO_PAIR;
        } else {
            return ONE_PAIR;
        }
    }

    private static final int JOKER_ORDINAL = CardWithJoker.J.ordinal();

    public static HandType ofJokers(CardWithJoker[] cards) {
        var counts = countCardOccurrences(cards);
        var numJokers = counts[JOKER_ORDINAL];

        // remove jokers from consideration
        counts = Arrays.copyOfRange(counts, 0, counts.length - 1);

        var max = findMaxCount(counts);

        if (max == 5) {
            return FIVE_OF_A_KIND;
        } else if (max == 4) {
            return handleFourOfAKind(numJokers);
        } else if (max == 3) {
            return handleThreeOfAKindOrFullHouse(numJokers, counts);
        } else if (max == 2) {
            return handleTwoPairOrOnePair(numJokers, counts);
        } else {
            return handleRemainingCases(numJokers);
        }
    }

    private static HandType handleFourOfAKind(int numJokers) {
        if (numJokers == 1) {
            return FIVE_OF_A_KIND;
        } else {
            return FOUR_OF_A_KIND;
        }
    }

    private static HandType handleThreeOfAKindOrFullHouse(int numJokers, int[] counts) {
        if (numJokers == 2) {
            return FIVE_OF_A_KIND;
        } else if (numJokers == 1) {
            return FOUR_OF_A_KIND;
        } else {
            return determineThreeOfAKindOrFullHouse(counts);
        }
    }

    private static HandType handleTwoPairOrOnePair(int numJokers, int[] counts) {
        var pairs = determineTwoPairOrOnePair(counts);
        if (pairs == ONE_PAIR) {
            return handleOnePair(numJokers);
        } else if (pairs == TWO_PAIR) {
            return handleTwoPair(numJokers);
        }
        return HandType.FIVE_OF_A_KIND;
    }

    private static HandType handleOnePair(int numJokers) {
        if (numJokers == 3) {
            return FIVE_OF_A_KIND;
        } else if (numJokers == 2) {
            return FOUR_OF_A_KIND;
        } else if (numJokers == 1) {
            return THREE_OF_A_KIND;
        } else {
            return ONE_PAIR;
        }
    }

    private static HandType handleTwoPair(int numJokers) {
        if (numJokers == 1) {
            return FULL_HOUSE;
        } else {
            return TWO_PAIR;
        }
    }

    private static HandType handleRemainingCases(int numJokers) {
        if (numJokers == 1) {
            return ONE_PAIR;
        } else if (numJokers == 2) {
            return THREE_OF_A_KIND;
        } else if (numJokers == 3) {
            return FOUR_OF_A_KIND;
        } else if (numJokers >= 4) {
            return FIVE_OF_A_KIND;
        } else {
            return HIGH_CARD;
        }
    }

}
