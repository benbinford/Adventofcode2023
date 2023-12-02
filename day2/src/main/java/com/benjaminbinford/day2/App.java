package com.benjaminbinford.day2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class App {
    public static void main(String[] args) {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(ClassLoader.getSystemResourceAsStream("com/benjaminbinford/day2/input.txt")))) {
            var app = new App();

            System.out.println(app.areGamesPossible(reader));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(ClassLoader.getSystemResourceAsStream("com/benjaminbinford/day2/input.txt")))) {
            var app = new App();

            System.out.println(app.gamesCubeSet(reader));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int isPossible(String line) {
        return Game.parse(line).isPossible();
    }

    public int areGamesPossible(BufferedReader r) {
        return r.lines().map(this::isPossible).reduce(0, Integer::sum);
    }

    public Map<String, Integer> cubeSet(String line) {
        return Game.parse(line).cubeSet();
    }

    public int power(Map<String, Integer> cubeSet) {
        return cubeSet.values().stream().reduce(1, (a, b) -> a * b);
    }

    public int gamesCubeSet(BufferedReader r) {
        return r.lines().map(this::cubeSet).map(this::power).reduce(0, Integer::sum);
    }
}
