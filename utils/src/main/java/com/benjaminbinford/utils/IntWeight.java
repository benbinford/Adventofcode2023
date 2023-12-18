package com.benjaminbinford.utils;

public record IntWeight(int weight) implements Weight<IntWeight> {
    @Override
    public int compareTo(IntWeight o) {
        return Integer.compare(weight, o.weight);
    }

    @Override
    public IntWeight combineEdge(IntWeight weight) {
        return new IntWeight(weight.weight + this.weight);
    }
}