package com.benjaminbinford.day5;

import java.util.SortedSet;

public record Mapping(String name, SortedSet<Conversion> conversions) {

    public long apply(long location) {

        var head = conversions.headSet(new Conversion(0l, location + 1, 0l, 0l, 0l));

        if (head.isEmpty()) {
            return location;
        }

        var conversion = head.last();
        if (location < conversion.sourceEnd()) {
            return location + conversion.delta();
        }

        return location;

    }

    public long reverseApply(long destinationLocation) {
        for (var conversion : conversions) {
            if (destinationLocation >= conversion.destination()
                    && destinationLocation < conversion.destination() + conversion.length()) {
                return conversion.source() + (destinationLocation - conversion.destination());
            }
        }
        return destinationLocation;
    }
}
