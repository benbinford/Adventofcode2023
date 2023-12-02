package com.benjaminbinford.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

public interface IO {

    public static List<String> getResourceLines(String path) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(ClassLoader.getSystemResourceAsStream(path)))) {

            return reader.lines().collect(Collectors.toList());

        } catch (IOException e) {
            throw new AdventException(e);
        }
    }

    public static List<String> getStaticStringLines(String input) {
        try (BufferedReader reader = new BufferedReader(
                new StringReader(input))) {

            return reader.lines().collect(Collectors.toList());

        } catch (IOException e) {
            throw new AdventException(e);
        }
    }
}
