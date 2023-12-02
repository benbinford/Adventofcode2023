package com.benjaminbinford.day2;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.benjaminbinford.utils.IO;

class AppTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green|1",
            "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue|2",
            "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red|0",
            "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red|0",
            "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green|5",
    })
    void testGamePossible(String line, int expected) {
        App app = new App();
        int result = app.isPossible(line);
        assertEquals(expected, result);
    }

    @Test
    void testGames() {
        App app = new App();
        String input = "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green\n" +
                "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue\n" +
                "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red\n" +
                "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red\n" +
                "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green";

        assertEquals(8, app.areGamesPossible(IO.getStaticStringLines(input).stream()));
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green|4|2|6",
            "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue|1|3|4",
            "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red|20|13|6",
            "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red|14|3|15",
            "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green|6|3|2|5",
    })
    void testGameCubeSet(String line, int red, int green, int blue) {
        App app = new App();

        Map<String, Integer> result = app.cubeSet(line);

        assertEquals(red, result.get("red"));
        assertEquals(green, result.get("green"));
        assertEquals(blue, result.get("blue"));

    }

    @Test
    void testParsers() {
        String line = "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue";

        Game g = Game.parse(line);

        Game expected = new Game(2,
                List.of(Map.of("red", 0, "blue", 1, "green", 2), Map.of("green", 3, "blue", 4, "red", 1),
                        Map.of("red", 0, "green", 1, "blue", 1)));

        assertEquals(
                expected,
                g);

    }

    @Test
    void testGamesPower() {
        App app = new App();
        String input = "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green\n" +
                "Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue\n" +
                "Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red\n" +
                "Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red\n" +
                "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green";

        assertEquals(2286, app.gamesCubeSet(IO.getStaticStringLines(input).stream()));

    }
}