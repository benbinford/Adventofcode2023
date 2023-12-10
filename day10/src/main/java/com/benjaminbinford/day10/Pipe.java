package com.benjaminbinford.day10;

import org.typemeta.funcj.tuples.Tuple2;

public record Pipe(char rep, char shape, Destination me, Tuple2<Destination, Destination> destinations) {

}
