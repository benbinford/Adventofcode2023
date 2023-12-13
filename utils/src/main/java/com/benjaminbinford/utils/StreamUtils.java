package com.benjaminbinford.utils;

import java.util.function.Supplier;
import java.util.stream.Stream;

public interface StreamUtils {
    public static <T> Stream<T> repeat(Supplier<Stream<T>> stream, int n) {
        return Stream.generate(stream).limit(n).flatMap(s -> s);
    }
}
