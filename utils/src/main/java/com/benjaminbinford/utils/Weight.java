package com.benjaminbinford.utils;

public interface Weight<E> extends Comparable<E> {
    E combineEdge(E weight);
}