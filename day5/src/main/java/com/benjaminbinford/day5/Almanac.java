package com.benjaminbinford.day5;

import static org.typemeta.funcj.parser.Text.chr;
import static org.typemeta.funcj.parser.Text.string;
import static org.typemeta.funcj.parser.Text.ulng;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.data.IList.NonEmpty;
import org.typemeta.funcj.parser.Combinators;
import org.typemeta.funcj.parser.Input;
import org.typemeta.funcj.parser.Parser;
import org.typemeta.funcj.parser.Result;

import com.benjaminbinford.utils.AdventException;

public record Almanac(List<Long> seeds, List<Mapping> maps) {

    static final Parser<Chr, Chr> spc = chr(' ');
    static final Parser<Chr, Chr> nl = chr('\n');
    static final Parser<Chr, String> rs = string("\n\n");

    static final Parser<Chr, List<Long>> numList = ulng.sepBy1(spc).map(IList::toList);
    static final Parser<Chr, List<Long>> seedParser = string("seeds: ").andR(numList);
    static final Parser<Chr, String> mapName = Combinators
            .<Chr>satisfy("map-name", c -> (Chr.isLetter(c) || c.charValue() == '-')).map(Chr::charValue).many1()
            .map(NonEmpty::listToString);

    static final Parser<Chr, Conversion> conversion = ulng.andL(spc).and(
            ulng).andL(spc).and(
                    ulng)
            .map((d, s, l) -> new Conversion(d, s, s + l, l, d - s));

    static final Parser<Chr, SortedSet<Conversion>> mapList = spc.many()
            .andR(spc.many().andR(conversion)
                    .sepBy1(nl)
                    .map(il -> new TreeSet<Conversion>(il.toList())));

    static final Parser<Chr, Mapping> mappingRecord = spc.many()
            .andR(mapName.andL(string(" map:\n")).and(mapList)
                    .map(n -> l -> new Mapping(n, l)));

    public static Almanac parse(String input) {

        var recs = input.split("\n\n");

        var seeds = seedParser.parse(Input.of(recs[0])).getOrThrow();

        var maps = Arrays.stream(recs).skip(1).map(Input::of).map(mappingRecord::parse).map(Result::getOrThrow)
                .collect(Collectors.toList());

        return new Almanac(seeds, maps);

    }

    public long applyMaps(long seed) {
        long location = seed;
        for (var map : maps) {
            location = map.apply(location);
        }
        return location;
    }

    public long applyReverseMaps(long location) {
        long seed = location;
        for (var map : maps.reversed()) {
            seed = map.reverseApply(seed);
        }
        return seed;
    }

    public long minLocation() {

        for (var location = 1l; location < Long.MAX_VALUE; location++) {
            var seed = applyReverseMaps(location);
            if (seeds.contains(seed)) {
                return location;
            }

        }

        throw new AdventException("No seed found");
    }

    public long minLocationExpandedSeeds() {
        for (var location = 1l; location < Long.MAX_VALUE; location++) {
            var seed = applyReverseMaps(location);
            for (var i = 0; i < seeds.size(); i += 2) {
                if (seed >= seeds.get(i) && seed <= seeds.get(i) + seeds.get(i + 1)) {
                    return location;
                }
            }

        }
        throw new AdventException("No seed found");
    }
}
