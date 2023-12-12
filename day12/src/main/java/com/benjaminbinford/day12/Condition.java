package com.benjaminbinford.day12;

public enum Condition {
    OPERATIONAL, DAMAGED, UNKNOWN;

    @Override
    public String toString() {
        switch (this) {
            case OPERATIONAL:
                return ".";
            case DAMAGED:
                return "#";
            case UNKNOWN:
                return "?";
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Condition of(int c) {
        switch (c) {
            case '.':
                return OPERATIONAL;
            case '#':
                return DAMAGED;
            case '?':
                return UNKNOWN;
            default:
                throw new IllegalArgumentException();
        }
    }
}
