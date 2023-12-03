package com.benjaminbinford.day3;

import static com.benjaminbinford.utils.ParserUtil.lineTrackingInput;
import static com.benjaminbinford.utils.ParserUtil.position;
import static org.typemeta.funcj.parser.Combinators.satisfy;
import static org.typemeta.funcj.parser.Text.chr;
import static org.typemeta.funcj.parser.Text.uintr;
import static org.typemeta.funcj.parser.Text.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.typemeta.funcj.data.Chr;
import org.typemeta.funcj.data.IList;
import org.typemeta.funcj.parser.Parser;

import com.benjaminbinford.utils.LineTrackingInput.Position;

public record Schematic(List<EnginePart> engineParts, List<PartNumber> partNumbers) {

    public static interface Marker {

    }

    public static record EnginePart(Position position, boolean gear) implements Marker {

    }

    public static record PartNumber(Position position, Position end, int partNumber) implements Marker {

    }

    private static Parser<Chr, IList.NonEmpty<Marker>> SCHEMATIC_PARSER;

    static {

        var junk = ws.or(chr('.'));

        var num = position().and(uintr).and(position())
                .map((p1, n, p2) -> (Marker) new PartNumber(p1, p2.backupOne(), n))
                .andL(junk.many());

        var engine = position()
                .and(satisfy("enginepart",
                        c -> c.charValue() != '.' && !Character.isLetter(c.charValue())
                                && !Character.isDigit(c.charValue()) && !Character.isWhitespace(c.charValue())))
                .map(p -> c -> (Marker) new EnginePart(p, c.charValue() == '*'))
                .andL(junk.many());

        SCHEMATIC_PARSER = junk.many().andR(Parser.choice(num, engine).many1());
    }

    public static Schematic parse(String input) {

        var markers = SCHEMATIC_PARSER.parse(lineTrackingInput(input)).getOrThrow();

        ArrayList<EnginePart> engineParts = new ArrayList<>();
        ArrayList<PartNumber> partNumbers = new ArrayList<>();

        for (var marker : markers) {
            if (marker instanceof EnginePart) {
                engineParts.add((EnginePart) marker);
            } else if (marker instanceof PartNumber) {
                partNumbers.add((PartNumber) marker);
            }
        }

        return new Schematic(engineParts, partNumbers);
    }

    public Stream<PartNumber> findAttachedPartNumbers() {
        return partNumbers.stream()
                .filter(p -> engineParts.stream().anyMatch(e -> e.position().touchingBox(p.position(), p.end())));
    }

    public int sumAttachedPartNumbers() {
        return findAttachedPartNumbers().mapToInt(p -> p.partNumber()).sum();
    }

    public IntStream findGearRatios() {
        return engineParts().stream()
                .filter(e -> e.gear())
                .mapToInt(e -> {

                    List<PartNumber> touching = partNumbers().stream()
                            .filter(p -> e.position().touchingBox(p.position(), p.end())).collect(Collectors.toList());

                    if (touching.size() != 2) {
                        return 0;
                    } else {
                        return touching.get(0).partNumber() * touching.get(1).partNumber();
                    }

                })
                .filter(r -> r > 0);
    }

    public Object sumGearRations() {
        return findGearRatios().sum();
    }

}
