package com.benjaminbinford.day10;

public record Destination(int row, int col) {

    public Destination adj(int rowDelta, int colDelta) {
        return new Destination(row + rowDelta, col + colDelta);
    }
}
