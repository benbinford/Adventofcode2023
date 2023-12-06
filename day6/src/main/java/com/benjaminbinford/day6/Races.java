package com.benjaminbinford.day6;

import java.util.ArrayList;
import java.util.List;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.parser.Input;
import org.typemeta.funcj.parser.Parser;
import static com.benjaminbinford.utils.ParserUtil.*;

public record Races(List<Race> races) {

    static final Parser<Chr, Races> PARSER;

    static {
        var timeParser = tok("Time:").andR(longTok().many1());
        var distanceParser = tok("Distance:").andR(longTok().many1());
        PARSER = grammar(timeParser.and(distanceParser).map(ts -> ds -> {
            assert (ts.size() == ds.size());
            var races = new ArrayList<Race>(ts.size());
            var tit = ts.iterator();
            var dit = ds.iterator();
            while (tit.hasNext()) {
                races.add(new Race(tit.next(), dit.next()));
            }

            return new Races(races);
        }));
    }

    public static Races parse(String resource) {
        return PARSER.parse(Input.of(resource)).getOrThrow();
    }

    public long multiplyWins() {
        return races.stream().mapToLong(Race::countWins).reduce(1l, (a, b) -> a * b);
    }
}
