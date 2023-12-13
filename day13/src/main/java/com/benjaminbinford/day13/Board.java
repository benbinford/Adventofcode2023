package com.benjaminbinford.day13;

import java.util.Arrays;
import java.util.OptionalInt;

public record Board(char[][] board, char[][] transposedBoard) {

    public static Board of(String s) {
        var board = s.lines().map(String::toCharArray).toArray(char[][]::new);

        var transposedBoard = new char[board[0].length][board.length];
        for (var j = 0; j < board[0].length; j++) {
            for (var i = 0; i < board.length; i++) {
                transposedBoard[j][board.length - i - 1] = board[i][j];
            }
        }

        return new Board(board, transposedBoard);
    }

    public int reflect() {
        return reflect(0);
    }

    public int reflect(int targetDifferenceCount) {
        return reflect(board, 100, targetDifferenceCount)
                .orElseGet(() -> reflect(transposedBoard, 1, targetDifferenceCount).orElseThrow());
    }

    private static OptionalInt reflect(char[][] board, int multiplier, int targetDifferenceCount) {

        for (var j = 0; j < board.length - 1; j++) {
            var totalCount = 0;
            for (int t = j, u = j + 1; t >= 0 && u < board.length; t--, u++) {
                totalCount += differenceCount(board[t], board[u]);
                if (totalCount > targetDifferenceCount) {
                    break;
                }
            }
            if (totalCount == targetDifferenceCount) {
                return OptionalInt.of((j + 1) * multiplier);
            }

        }

        return OptionalInt.empty();
    }

    private static int differenceCount(char[] t, char[] u) {
        int count = 0;
        for (int i = 0; i < t.length && i < u.length; i++) {
            if (t[i] != u[i]) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Board other = (Board) obj;
        return Arrays.deepEquals(board, other.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "Board{" +
                "board=" + Arrays.deepToString(board) +
                "transpose=" + Arrays.deepToString(transposedBoard) +

                '}';
    }
}
