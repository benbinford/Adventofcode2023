package com.benjaminbinford.day4;

import static com.benjaminbinford.utils.ParserUtil.grammar;
import static com.benjaminbinford.utils.ParserUtil.intTok;
import static com.benjaminbinford.utils.ParserUtil.tok;

import java.util.HashSet;
import java.util.Set;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.parser.Input;
import org.typemeta.funcj.parser.Parser;

public record Card(int gameId, Set<Integer> winners, Set<Integer> mine) {

    private static final Parser<Chr, Card> CARD_PARSER;

    private static Parser<Chr, Card> getCardParser() {
        return CARD_PARSER;

    }

    static {

        // Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue

        final var gameIdParser = intTok().between(tok("Card"), tok(':'));

        final var intList = intTok().many1();

        CARD_PARSER = grammar(gameIdParser.and(intList).andL(tok('|')).and(intList)
                .map(i -> w -> m -> new Card(i, new HashSet<>(w.toList()), new HashSet<>(m.toList()))));
    }

    public static Card parse(String line) {
        return getCardParser().parse(Input.of(line)).getOrThrow();
    }

    public int getNumMatches() {
        var matches = new HashSet<>(mine);
        matches.retainAll(winners);
        return matches.size();
    }

    public Integer score() {
        var matches = getNumMatches();
        if (matches == 0) {
            return 0;
        } else {
            return 1 << (matches - 1);
        }

    }

}
