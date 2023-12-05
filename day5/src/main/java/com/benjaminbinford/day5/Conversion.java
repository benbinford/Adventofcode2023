package com.benjaminbinford.day5;

public record Conversion(long destination, long source, long sourceEnd, long length, long delta)
        implements Comparable<Conversion> {

    @Override
    public int compareTo(Conversion o) {
        return Long.compare(source, o.source);
    }
}
