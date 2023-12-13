package com.benjaminbinford.day12;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.typemeta.funcj.tuples.Tuple2;

public record ConditionRecord(
        String conditions, List<Integer> runLengths) {

    private static final char OPERATIONAL = '.';
    private static final char DAMAGED = '#';
    private static final char UNKNOWN = '?';

    public static ConditionRecord explode(String line) {
        var components = line.split(" ");
        var cond = components[0];
        var runLengths = components[1];
        cond = cond + UNKNOWN + cond + UNKNOWN + cond + UNKNOWN + cond + UNKNOWN + cond;
        runLengths = runLengths + "," + runLengths + "," + runLengths + "," + runLengths + "," + runLengths;
        return ofParts(cond, runLengths);
    }

    public static ConditionRecord of(String line) {
        var components = line.split(" ");
        return ofParts(components[0], components[1]);
    }

    public static ConditionRecord ofParts(String conditionsRep, String runLengthsRep) {

        var runLengths = getRunLengths(runLengthsRep);

        return new ConditionRecord(conditionsRep, runLengths);

    }

    private static ArrayList<Integer> getRunLengths(String s) {
        return Arrays.stream(s.split(",")).mapToInt(Integer::parseInt)
                .<ArrayList<Integer>>collect(ArrayList::new,
                        ArrayList::add, ArrayList::addAll);
    }

    private class Finder {
        private HashMap<Tuple2<Integer, Integer>, Long> cache = new HashMap<>();

        public long findArrangements(int cIdx, int rIdx) {
            var key = Tuple2.of(cIdx, rIdx);
            if (cache.containsKey(key)) {
                return cache.get(key);
            }

            long result = 0l;

            if (rIdx == runLengths.size()) {
                if (conditions.indexOf(DAMAGED, cIdx) == -1) {
                    return 1;
                } else {
                    return 0;
                }
            }

            while (cIdx < conditions.length() && conditions.charAt(cIdx) == OPERATIONAL) {
                cIdx++;
            }

            var runLength = runLengths.get(rIdx);

            if (cIdx >= conditions.length() || cIdx + runLength > conditions.length()) {
                return 0;
            }

            if (conditions.indexOf(OPERATIONAL, cIdx, cIdx + runLength) == -1 &&
                    (conditions.length() == cIdx + runLength || conditions.charAt(cIdx + runLength) != DAMAGED)) {

                result += findArrangements(cIdx + runLength + 1, rIdx + 1);
            }

            if (conditions.charAt(cIdx) == UNKNOWN) {
                result += findArrangements(cIdx + 1, rIdx);
            }

            cache.put(key, result);
            return result;
        }

    }

    public long findArrangements() {
        return new Finder().findArrangements(0, 0);
    }

}
