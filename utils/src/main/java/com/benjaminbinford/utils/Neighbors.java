package com.benjaminbinford.utils;

import java.util.function.BiConsumer;

public interface Neighbors<V, E extends Weight<E>> {
    void addNeighbors(V vertex, BiConsumer<V, E> add);
}