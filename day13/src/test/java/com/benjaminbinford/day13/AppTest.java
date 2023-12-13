package com.benjaminbinford.day13;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
class AppTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    void testHorizontalReflection() {
        final var board = Board.of("""
                #.##..##.
                ..#.##.#.
                ##......#
                ##......#
                ..#.##.#.
                ..##..##.
                #.#.##.#.
                """);

        System.out.println(board);

        assertEquals(5, board.reflect());
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testVerticalReflection() {
        final var board = Board.of("""
                #...##..#
                #....#..#
                ..##..###
                #####.##.
                #####.##.
                ..##..###
                #....#..#
                """);

        assertEquals(400, board.reflect());
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testHorizontalReflectionSmudges() {
        final var board = Board.of("""
                #.##..##.
                ..#.##.#.
                ##......#
                ##......#
                ..#.##.#.
                ..##..##.
                #.#.##.#.
                """);

        System.out.println(board);

        assertEquals(300, board.reflect(1));
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    void testVerticalReflectionSmudges() {
        final var board = Board.of("""
                #...##..#
                #....#..#
                ..##..###
                #####.##.
                #####.##.
                ..##..###
                #....#..#
                """);

        assertEquals(100, board.reflect(1));
    }
}
