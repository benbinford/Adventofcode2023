package com.benjaminbinford.day2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record Game(int gameId, List<Map<String, Integer>> hands) {
    public Game {
        Objects.requireNonNull(hands);
    }

    private static final String RED = "red";
    private static final String GREEN = "green";
    private static final String BLUE = "blue";

    public static Game parse(String line) {
        // Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue

        var s1 = line.split(":");
        var gameId = Integer.parseInt(s1[0].split(" ")[1]);
        var handsRep = s1[1].split(";");

        List<Map<String, Integer>> hands = Arrays.stream(handsRep)
                .map(handRep -> {
                    Map<String, Integer> m = new HashMap<>();
                    m.put(RED, 0);
                    m.put(GREEN, 0);
                    m.put(BLUE, 0);
                    var colors = handRep.split(",");
                    for (var color : colors) {
                        var s2 = color.trim().split("\\s+");
                        m.put(s2[1], Integer.parseInt(s2[0]));
                    }

                    return m;
                })
                .toList();

        return new Game(gameId, hands);
    }

    public int isPossible() {
        return hands.stream()
                .allMatch(hand -> hand.get(RED) <= 12 && hand.get(BLUE) <= 14 && hand.get(GREEN) <= 13)
                        ? this.gameId
                        : 0;
    }

    public Map<String, Integer> cubeSet() {
        Map<String, Integer> m = new HashMap<>();
        m.put(RED, 0);
        m.put(GREEN, 0);
        m.put(BLUE, 0);

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
}
