package com.benjaminbinford.day7;

public enum HandType {
    FIVE_OF_A_KIND, FOUR_OF_A_KIND, FULL_HOUSE, THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD;

    public static HandType of(Card[] cards, Card... jokers) {
        var counts = countCardOccurrences(cards);

        var jokerMap = createJokerMap(jokers);

        var max = findMaxCount(counts, jokerMap);

        var numJokers = findJokerCount(counts, jokerMap);

        if (max == 5) {
            return FIVE_OF_A_KIND;
        } else if (max == 4) {
            return handleFourOfAKind(numJokers);
        } else if (max == 3) {
            return handleThreeOfAKindOrFullHouse(numJokers, counts, jokerMap);
        } else if (max == 2) {
            return handleTwoPairOrOnePair(numJokers, counts, jokerMap);
        } else {
            return handleRemainingCases(numJokers);
        }
    }

    private static int findJokerCount(int[] counts, boolean[] jokerMap) {
        var sum = 0;
        for (var i = 0; i < counts.length; i++) {
            if (jokerMap[i]) {
                sum += counts[i];
            }

        }
        return sum;
    }

    private static boolean[] createJokerMap(Card[] jokers) {
        var counts = new boolean[13];
        for (var card : jokers) {
            counts[card.ordinal()] = true;
        }
        return counts;
    }

    private static <T extends Enum<T>> int[] countCardOccurrences(T[] cards) {
        var counts = new int[13];
        for (var card : cards) {
            counts[card.ordinal()]++;
        }
        return counts;
    }

    private static int findMaxCount(int[] counts, boolean[] jokerMap) {
        var max = 0;
        for (var i = 0; i < counts.length; i++) {
            if (jokerMap[i]) {
                continue;
            }
            var count = counts[i];
            if (count > max) {
                max = count;
            }
        }
        return max;
    }

    private static HandType handleFourOfAKind(int numJokers) {
        if (numJokers == 1) {
            return FIVE_OF_A_KIND;
        } else {
            return FOUR_OF_A_KIND;
        }
    }

    private static HandType handleThreeOfAKindOrFullHouse(int numJokers, int[] counts, boolean[] jokerMap) {
        if (numJokers == 2) {
            return FIVE_OF_A_KIND;
        } else if (numJokers == 1) {
            return FOUR_OF_A_KIND;
        } else {
            var hasPair = false;
            for (var i = 0; i < counts.length; i++) {
                if (!jokerMap[i]) {

                    var count = counts[i];
                    if (count == 2) {
                        hasPair = true;
                        break;
                    }
                }
            }
            if (hasPair) {
                return FULL_HOUSE;
            } else {
                return THREE_OF_A_KIND;
            }
        }
    }

    private static HandType handleTwoPairOrOnePair(int numJokers, int[] counts, boolean[] jokerMap) {
        var pairCount = 0;
        for (var i = 0; i < counts.length; i++) {
            if (!jokerMap[i]) {
                var count = counts[i];
                if (count == 2) {
                    pairCount++;
                }
            }
        }

        if (pairCount == 1) {
            return handleOnePair(numJokers);
        } else if (pairCount == 2) {
            return handleTwoPair(numJokers);
        }

        throw new IllegalStateException("Invalid pair count: " + pairCount);
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
