package com.benjaminbinford.day2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.parser.Input;
import org.typemeta.funcj.parser.Parser;

import static com.benjaminbinford.utils.ParserUtil.*;

public record Game(int gameId, List<Map<String, Integer>> hands) {
    public Game

    {
        Objects.requireNonNull(hands);
    }

    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";

    public int isPossible() {
        return hands.stream()
                .allMatch(hand -> hand.get(RED) <= 12 && hand.get(BLUE) <= 14 && hand.get(GREEN) <= 13)
                        ? this.gameId
                        : 0;
    }

    public Map<String, Integer> cubeSet() {
        Map<String, Integer> m = emptyHand();

        return hands.stream()
                .reduce(m, (nextM, h) -> {
                    if (nextM.get(RED) < h.get(RED)) {
                        nextM.put(RED, h.get(RED));
                    }
                    if (nextM.get(GREEN) < h.get(GREEN)) {
                        nextM.put(GREEN, h.get(GREEN));
                    }
                    if (nextM.get(BLUE) < h.get(BLUE)) {
                        nextM.put(BLUE, h.get(BLUE));
                    }
                    return nextM;
                });
    }

    private static Map<String, Integer> emptyHand() {
        Map<String, Integer> m = new HashMap<>();
        m.put(RED, 0);
        m.put(GREEN, 0);
        m.put(BLUE, 0);
        return m;
    }

    public static Game parse(String input) {

        final Parser<Chr, Game> gameParser = getGameParser();

        return gameParser.parse(Input.of(input)).getOrThrow();

    }

    private static final Parser<Chr, Game> GAME_PARSER;

    private static Parser<Chr, Game> getGameParser() {
        return GAME_PARSER;

    }

    static {

        // Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue

        final var gameIdParser = intTok().between(tok("Game"), tok(':'));

        final var color = intTok().and(tok(RED, GREEN, BLUE))
                .map((i, c) -> Map.of(c, i));

        final var hand = color.sepBy(tok(','))
                .map(mergeMaps(Game::emptyHand));

        final var hands = hand.sepBy(tok(';'));

        GAME_PARSER = grammar(gameIdParser.and(hands).map(i -> l -> new Game(i, l.toList())));
    }
}
