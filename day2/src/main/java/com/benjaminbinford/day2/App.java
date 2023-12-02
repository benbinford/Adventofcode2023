package com.benjaminbinford.day2;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.benjaminbinford.utils.IO;

public class App {
    public static void main(String[] args) {

        List<String> lines = IO.getResourceLines("com/benjaminbinford/day2/input.txt");

        var app = new App();

        System.out.println(app.areGamesPossible(lines.stream()));
        System.out.println(app.gamesCubeSet(lines.stream()));
    }

    public int isPossible(String line) {
        return Game.parse(line).isPossible();
    }

    public int areGamesPossible(Stream<String> r) {
        return r.map(this::isPossible).reduce(0, Integer::sum);
    }

    public Map<String, Integer> cubeSet(String line) {
        return Game.parse(line).cubeSet();
    }

    public int power(Map<String, Integer> cubeSet) {
        return cubeSet.values().stream().reduce(1, (a, b) -> a * b);
    }

    public int gamesCubeSet(Stream<String> r) {
        return r.map(this::cubeSet).map(this::power).reduce(0, Integer::sum);
    }
}
